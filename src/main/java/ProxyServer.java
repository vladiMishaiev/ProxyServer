package main.java;
import main.java.event.EventBase;
import main.java.event.EventHandlerThread;
import main.java.security.SecurityLayerThread;
import main.java.session.ProxySession;
import main.java.tunnel.TunnelsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyServer {
	private static final Logger LOG = LogManager.getLogger("trace");
	private static BlockingQueue<ProxySession> securityLayerQueue = null;
	private static BlockingQueue<EventBase> eventsQueue = null;
	private static TunnelsManager tunnelsManager;
	
	public static void main(String[] args) {
		init_system();
		new Thread(new SecurityLayerThread(securityLayerQueue)).start();
		new Thread(new EventHandlerThread()).start();
		//add management thread

	}

	private static void init_system() {
		LOG.info("Proxy server system init");
		securityLayerQueue = new LinkedBlockingQueue<>(100);
		eventsQueue = new LinkedBlockingQueue<>(100);
		tunnelsManager = new TunnelsManager(securityLayerQueue);
		GlobalManager.setSecurityLayerQueue(securityLayerQueue);
		GlobalManager.setEventsQueue(eventsQueue);
		GlobalManager.setTunnelsManager(tunnelsManager);
	}
}
