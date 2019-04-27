package main.java.event;

import main.java.GlobalManager;
import main.java.config.StateInfo;
import main.java.utils.MyUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.BytesBody;
import rawhttp.core.body.StringBody;
import rawhttp.core.client.TcpRawHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import static main.java.utils.Constants.*;

public class EventHandlerThread implements Runnable {
    private static final Logger LOG = LogManager.getLogger("trace");
    private String logServerIP;
    private int logServerPort;
    private BlockingQueue<EventBase> eventsQueue = null;

    public EventHandlerThread(){
        //updateEventsIDStartValue();
        init();
    }

    private void updateEventsIDStartValue() {
        StateInfo info = (StateInfo) MyUtils.loadConfigFromFile(StateInfo.class,STATE_INFO_PATH);
        EventBase.setIdGenerator(info.getEventsStartID());

    }

    private void init() {
        this.eventsQueue = GlobalManager.getEventsQueue();
        this.logServerIP = GlobalManager.getConfigManager()
                                        .getAdminConfig().getEventLogServerIP();
        this.logServerPort = GlobalManager.getConfigManager()
                                        .getAdminConfig().getEventLogServerPort();
    }

    @Override
    public void run() {
        EventBase currentEvent = null;
        while (true){
            try{
                currentEvent = eventsQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (currentEvent == null)
                continue; // drop event

            String currentEventJSONstr = MyUtils.convertObjectToJSONStr(currentEvent);

            if (currentEvent instanceof AdminEvent)
                publishEvent(currentEventJSONstr,"http://"+logServerIP+":"+logServerPort+CREATE_ADMIN_EVENT_URI);
            if (currentEvent instanceof SecurityEvent)
                publishEvent(currentEventJSONstr,"http://"+logServerIP+":"+logServerPort+CREATE_SECURITY_EVENT_URI);

            //update events counter
            //EventBase.getIdGenerator()
            MyUtils.saveConfigToFile(new StateInfo(EventBase.getIdGenerator().get()),STATE_INFO_PATH);
        }
    }

    private boolean publishEvent (String JSONEventStr, String uri){
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(uri);

            StringEntity input = new StringEntity(JSONEventStr);
            input.setContentType("application/json");
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String read = "";
            String responseBody = "";
            while ((read = br.readLine()) != null) {
                responseBody += read + "\n";
            }

            if (response.getStatusLine().getStatusCode() != 201 || response.getStatusLine().getStatusCode() != 200) {
                LOG.info("Failed to send log event, Start-line: " + response.getStatusLine());
                LOG.debug("Failed Event: \n" + JSONEventStr +
                        "\nReply: \n" + responseBody);
            } else {
                LOG.info("Event succefully sent");
            }
            httpClient.getConnectionManager().shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
