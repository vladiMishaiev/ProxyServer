package main.java.security;

import main.java.session.Message.File.ProxyFile;
import main.java.session.ProxySession;
import main.java.session.connectionHandlers.FTPClientConnectionHandler;
import main.java.session.connectionHandlers.HTTPClientConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class SecurityLayerThread implements Runnable{
	private static final Logger LOG = LogManager.getLogger("trace");
	private BlockingQueue<ProxySession> securityLayerQueue;
	private ProxySession currentSession;
	
	public SecurityLayerThread(BlockingQueue<ProxySession> securityLayerQueueArg) {
		this.securityLayerQueue = securityLayerQueueArg;
	}
	
	@Override
	public void run() {
		
		try {
			while (true) {
				currentSession = securityLayerQueue.take();
				System.out.println("===== Security Layer got new message =====");
				switch (currentSession.getSessionType()){
					case HTTP_SESSION:{
						implementHTTPSecurity();
					}break;
					case FTP_SESSION:{
						implementFTPSecurity();
					}break;
					case SMTP_SESSION:{

					}break;

				}

				//Message message = securityLayerQueue.take();



				//GlobalManager.getTunnelsManager().getTunnel(message.getTunnelID()).getSessionManager().
				//	getSessionByID(message.getSessionID()).getRequestsQueue().put(message);
				
				
				// add a stop condition
				//System.out.println(Thread.currentThread().getName() + "Sec pop: " + number.getStr());
				//outputDispatcherQueue.put(number);
				//System.out.println(Thread.currentThread().getName() + "Sec push: " + number.getStr());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void implementHTTPSecurity() {
		try {
			currentSession.getRequestsQueue().put(((HTTPClientConnectionHandler)(currentSession.getClientHandler())).getRequestMessage());
			handleFiles();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void implementFTPSecurity() {
		try {
			currentSession.getRequestsQueue().put(((FTPClientConnectionHandler) (currentSession.getClientHandler())).getRequestMessage());
		} catch (InterruptedException e) {
		Thread.currentThread().interrupt();
		}
	}

	private void handleFiles () throws IOException {
		List<ProxyFile> files = ((HTTPClientConnectionHandler)currentSession.getClientHandler()).getRequestMessage().getFiles();
		Iterator<ProxyFile> itr = files.iterator();
		while (itr.hasNext()){
			ProxyFile file = itr.next();
			//detect file type
			Detector detector = new DefaultDetector();
			Metadata metadata = new Metadata();
			InputStream fileStream = new BufferedInputStream(new FileInputStream(file.getData()));
			MediaType mediaType = null;
			try {
				mediaType = detector.detect(fileStream, metadata);
			} catch (IOException e) {
				e.printStackTrace();
				fileStream.close();
			}
			fileStream.close();
			System.out.println("* * * * * File Detected as:" + mediaType.toString());
		}
	}
}
