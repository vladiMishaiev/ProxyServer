package main.java.session.connectionHandlers;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import main.java.session.Message.EndStreamMessage;
import main.java.session.Message.HTTPResponseMessage;
import main.java.session.Message.HTTPRequestMessage;
import main.java.session.Message.Message;
import main.java.session.ProxySession;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.errors.InvalidHttpRequest;

public class HTTPServerConnectionHandler extends ConnectionHandler{
	private HTTPResponseMessage replyMessage;
	private Socket serverSocket;
	private BlockingQueue<Message> requestsQueue;
	private BlockingQueue<Message> repliesQueue;
	private RawHttp http;
	
	public HTTPServerConnectionHandler(BlockingQueue<Message> requestsQueue,BlockingQueue<Message> repliesQueue) {
		//this.replyMessage = (HTTPResponseMessage) reply;
		//this.serverSocket = serverSocket;
		this.replyMessage = new HTTPResponseMessage();
		this.requestsQueue = requestsQueue;
		this.repliesQueue = repliesQueue;
		http = new RawHttp();
	}
	
	@Override
	public void run() {
		try{
			while (true) {
				//HTTPRequestMessage requestMessage = (HTTPRequestMessage) requestsQueue.take();
				Message requestMessage = requestsQueue.take();
				if (requestMessage instanceof EndStreamMessage){
					throw new InvalidHttpRequest("",1);
				}
				if (!(requestMessage instanceof HTTPRequestMessage))
					System.out.println("Problem");
				System.out.println(Thread.currentThread().getId() + "New Reply from server");
				
				String dstIP = ((HTTPRequestMessage) requestMessage).getRequest().getHeaders().get("host").toString();
				dstIP = dstIP.substring(1, dstIP.length() - 1);
				if (serverSocket == null || serverSocket.isClosed()) {
					serverSocket = new Socket(dstIP, 80);
					System.out.println(Thread.currentThread().getId() + " " + "Opened connection to " + dstIP);
				}
				RawHttpRequest tempRequest = http.parseRequest(((HTTPRequestMessage) requestMessage).getRequest().eagerly().toString());
				tempRequest.writeTo(serverSocket.getOutputStream());
				//((HTTPRequestMessage) requestMessage).getRequest().writeTo(serverSocket.getOutputStream());
				// receive reply and send to client
				RawHttpResponse<?> reply = http.parseResponse(serverSocket.getInputStream()).eagerly();
				replyMessage.setResponse(reply);
				//push reply to requests queue
				repliesQueue.put(replyMessage);
			}
			
		} catch (InvalidHttpRequest | IOException e) {
			try {
				if (serverSocket != null)
					serverSocket.close();
				System.out.println(Thread.currentThread().getId()+" "+"Done reading message, server connection closed");
			} catch (IOException e1) {e1.printStackTrace();}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSession(ProxySession proxySession) {
		this.mySession = proxySession;
	}

	@Override
	public ProxySession getSession() {
		return this.mySession;
	}
}
