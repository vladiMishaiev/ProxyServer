package main.java.session.connectionHandlers;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import main.java.utils.multipartParser.MultipartInput;
import main.java.utils.multipartParser.PartInput;
import main.java.session.Message.File.ProxyFile;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import main.java.GlobalManager;
import main.java.session.Message.EndStreamMessage;
import main.java.session.Message.HTTPResponseMessage;
import main.java.session.Message.HTTPRequestMessage;
import main.java.session.Message.Message;
import main.java.session.ProxySession;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.errors.InvalidHttpRequest;


public class HTTPClientConnectionHandler extends ConnectionHandler{
	private static final Logger LOG = LogManager.getLogger("trace");
	private HTTPRequestMessage requestMessage;
	private Socket clientSocket;
	private BlockingQueue<Message> repliesQueue;
	private BlockingQueue<Message> requestsQueue;
	private RawHttp http;
	
	public HTTPClientConnectionHandler(Socket clientSocket, BlockingQueue<Message> repliesQueue, BlockingQueue<Message> requestsQueue) {
		//this.requestMessage = (HTTPRequestMessage) request;
		this.requestMessage = new HTTPRequestMessage();
		this.clientSocket = clientSocket;
		this.repliesQueue = repliesQueue;
		this.requestsQueue = requestsQueue;
		http = new RawHttp();
	}
	
	@Override
	public void run() {
		try{
			clientSocket.setSoTimeout(CLIENT_SOCKET_TIMOUT_VALUE);
			while (true) {
				RawHttpRequest request = http.parseRequest(clientSocket.getInputStream()).eagerly();
				//System.out.println(Thread.currentThread().getId() + "New Request from client");
				requestMessage.setRequest(request);
				//System.out.println("Thread num: " + Thread.currentThread().getId()+ " " + request);
				LOG.info("Session:"+mySession.getID() + " New Request from client: " + request.getStartLine() + " req:" +System.identityHashCode(request));
				LOG.debug("Session:"+mySession.getID() + " Req:" + System.identityHashCode(request) + " Buffer: \n" + request);
				ParseQueryParams();
				ProcessRequestBody();
				//push request to security queue
				//GlobalManager.getSecurityLayerQueue().put(requestMessage);
				//GlobalManager.getSecurityLayerQueue().put(this.mySession);

				/*ProxySession securitySession = new ProxySession();
				securitySession.setSessionType(SessionType.HTTP_SESSION);
				securitySession.setClientHandler(this);
				securitySession.setRequestsQueue(requestsQueue);
				securitySession.setRepliesQueue(repliesQueue);
				GlobalManager.getSecurityLayerQueue().put(securitySession);*/
				GlobalManager.getSecurityLayerQueue().put(this.mySession);
				LOG.info("Session:"+mySession.getID() + " Request sent to security queue - req:" +System.identityHashCode(request));
				//pull response from queue
				//Thread.sleep(1000);
				HTTPResponseMessage replyMessage = (HTTPResponseMessage) repliesQueue.take();
				LOG.info("Session:"+mySession.getID() + " Recieved reply from security layer req:" +System.identityHashCode(request) +
						" rep:" + System.identityHashCode(replyMessage.getResponse()));
				LOG.debug("Session:"+mySession.getID() +" Rep:" + System.identityHashCode(replyMessage.getResponse()) +
						" Reply buffer:\n" + replyMessage.getResponse());
				//System.out.println(Thread.currentThread().getId() + " " +replyMessage.getResponse().getStartLine());
				//replyMessage.getResponse().writeTo(clientSocket.getOutputStream());
				replyMessage.getResponse().eagerly().writeTo(clientSocket.getOutputStream());
				LOG.info("Session:"+mySession.getID() +" Rep:"+System.identityHashCode(replyMessage.getResponse()) + " sent to client");
			}
			
		} catch (InvalidHttpRequest | IOException e) {
			System.out.println(e);
			try {
				clientSocket.close();
				//Message poisonPill = new Message();
				//poisonPill.setEndStreamMessage(true);
				requestsQueue.put(new EndStreamMessage());
				//System.out.println(Thread.currentThread().getId()+" "+"Done reading message, client connection closed");
				LOG.info("Done reading stream, client connection closed");
			} catch (IOException | InterruptedException e1) {e1.printStackTrace();}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void ParseQueryParams() {
		if (requestMessage.getRequest().getMethod().contains("GET")) {
			return;
		}
		String contentTypeHeader = requestMessage.getRequest().getHeaders().get("content-type").toString();
		List<NameValuePair> params;
		try {
			//check if body or url param
			if (contentTypeHeader.contains("x-www-form-urlencoded")){
				params = URLEncodedUtils.parse(new URI(requestMessage.getRequest().getUri() + "?" + requestMessage.getRequest().getBody().get().toString()), Charset.forName("UTF-8"));
				requestMessage.setBodyParams(params);
				LOG.info("Session:"+mySession.getID() +" Req:"+System.identityHashCode(requestMessage.getRequest()) + " Detected and parsed body params");
				LOG.debug("Session:"+mySession.getID() +" Req:"+System.identityHashCode(requestMessage.getRequest()) + " Params:" + requestMessage.getBodyParams().toString());
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	private void ProcessRequestBody() {
        try {
			String contentTypeHeader = requestMessage.getRequest().getHeaders().get("content-type").toString();
			contentTypeHeader = contentTypeHeader.substring(1,contentTypeHeader.length()-1);
            if (contentTypeHeader.contains("multipart/form-data")) {
				LOG.info("Session:"+mySession.getID() +" Req:"+System.identityHashCode(requestMessage.getRequest()) + " Processing multipart message");
                byte[] data = requestMessage.getRequest().getBody().get().asRawBytes();
                InputStream msgIS = new ByteArrayInputStream(data);
				MultipartInput multipart = new MultipartInput(msgIS,contentTypeHeader);
				PartInput currentPart = multipart.nextPart();
				while (currentPart!= null){
					ProxyFile newFile = new ProxyFile();
					String filename = currentPart.getHeaderField("Content-Disposition");
					filename = filename.substring(filename.indexOf("filename=")+10,filename.lastIndexOf('"'));
					String prefix="",suffix="";

					if (filename==null){
						prefix = "none";
					}else {
						int ii = filename.indexOf('.');
						prefix = filename.substring(
								0,
								filename.indexOf('.') == -1 ? filename.length() : filename.indexOf('.'));
						suffix = filename.substring(
								filename.indexOf('.') == -1 ? 0 : filename.indexOf('.')
								, filename.length() - 1);
					}
					File tempFile = File.createTempFile(prefix, suffix);
					OutputStream outStream = new FileOutputStream(tempFile);
					byte[] fileData = IOUtils.toByteArray(currentPart.getInputStream());
					outStream.write(fileData);
					newFile.setData(tempFile);
					newFile.setFileName(prefix+suffix);
					requestMessage.getFiles().add(newFile);
					newFile.setFileTypeStr(currentPart.getContentType());
					currentPart = multipart.nextPart();

					LOG.info("Session:"+mySession.getID() +" Req:"+System.identityHashCode(requestMessage.getRequest()) + " New file detected:"+newFile.getFileName());
					LOG.debug("Session:"+mySession.getID() +" Req:"+System.identityHashCode(requestMessage.getRequest()) + " File content:\n" + new String(fileData));


				}

//				currentPart = multipart.nextPart();
//				byte[] firstFile = IOUtils.toByteArray(currentPart.getInputStream());
//				String s = new String(firstFile);
//				System.out.println("Text Decryted : " + s);
//
//				currentPart = multipart.nextPart();
//				firstFile = IOUtils.toByteArray(currentPart.getInputStream());
//				s = new String(firstFile);
//				System.out.println("Text Decryted : " + s);
//
//				currentPart = multipart.nextPart();
//				if (currentPart == null){
//					System.out.println("** ** ** Last part YAY");
//				}
//				firstFile = IOUtils.toByteArray(currentPart.getInputStream());
//				s = new String(firstFile);
//				System.out.println("Text Decryted : " + s);
			}



        }catch (Exception e){
			System.out.println(e);
        }
    }

    public HTTPRequestMessage getRequestMessage() {
		return requestMessage;
	}

	@Override
	public void setSession(ProxySession proxySession) {
		this.mySession = proxySession;
	}

	@Override
	public ProxySession getSession() {
		return this.mySession;
	}

	public Socket getClientSocket(){
		return clientSocket;
	}
}
