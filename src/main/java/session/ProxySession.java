package main.java.session;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import main.java.session.Message.Message;
import main.java.session.connectionHandlers.*;

public class ProxySession {
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private int ID;
    private int tunnelID;
    private SessionType sessionType;
    //private Socket clientSocket;
    //private Socket serverSocket;
    //private Message request;
    //private Message reply;
    private BlockingQueue<Message> requestsQueue = null;
    private BlockingQueue<Message> repliesQueue = null;
    private ConnectionHandler clientHandler;
    private ConnectionHandler serverHandler;

    public ProxySession(Socket clientSocket, SessionType sessionType) {
        this.ID = idGenerator.getAndIncrement();
        ;
        requestsQueue = new LinkedBlockingQueue<>(100);
        repliesQueue = new LinkedBlockingQueue<>(100);
        //this.clientSocket = clientSocket;
        setSessionType(sessionType);
        //setRequest();
        //setReply();
        startHandlers(clientSocket);
    }

    public ProxySession() {
    }


    public void startHandlers(Socket clientSocket) {
        startClientHandler(clientSocket);
        startServerHandler();
    }

    public void startClientHandler(Socket clientSocket) {
        switch (sessionType) {
            case HTTP_SESSION: {
                clientHandler = new HTTPClientConnectionHandler(clientSocket, repliesQueue, requestsQueue);
                clientHandler.setSession(this);
                new Thread(clientHandler).start();
                break;
            }
            case FTP_SESSION: {
                clientHandler = new FTPClientConnectionHandler(clientSocket, repliesQueue, requestsQueue);
                clientHandler.setSession(this);
                new Thread(clientHandler).start();
            }
            default:
                break;
        }
    }

    public void startServerHandler() {
        switch (sessionType) {
            case HTTP_SESSION:
                serverHandler = new HTTPServerConnectionHandler(requestsQueue, repliesQueue);
                serverHandler.setSession(this);
                new Thread(serverHandler).start();
                break;
            case FTP_SESSION:
                serverHandler = new FTPServerConnectionHandler(requestsQueue, repliesQueue);
                serverHandler.setSession(this);
                new Thread(serverHandler).start();
            default:
                break;
        }
    }

    public SessionType getSessionType() {
        return sessionType;
    }


    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }
	
	/*public void setRequest() {
		switch (sessionType) {
		case HTTP_SESSION:
			this.request = new HTTPRequestMessage();
			this.request.setTunnelID(tunnelID);
			this.request.setSessionID(ID);
			break;
		default:
			break;
		}
	}*/
	
	/*public void setReply() {
		switch (sessionType) {
		case HTTP_SESSION:
			this.reply = new HTTPResponseMessage();
			this.reply.setTunnelID(tunnelID);
			this.reply.setSessionID(ID);
			break;
		default:
			break;
		}
	}*/

    public void setTunnelID(int ID) {
        this.tunnelID = ID;
    }

    public int getID() {
        return ID;
    }

    public BlockingQueue<Message> getRequestsQueue() {
        return requestsQueue;
    }

    public BlockingQueue<Message> getRepliesQueue() {
        return repliesQueue;
    }

    public ConnectionHandler getClientHandler() {
        return clientHandler;
    }

    public ConnectionHandler getServerHandler() {
        return serverHandler;
    }

    public void setRequestsQueue(BlockingQueue<Message> requestsQueue) {
        this.requestsQueue = requestsQueue;
    }

    public void setRepliesQueue(BlockingQueue<Message> repliesQueue) {
        this.repliesQueue = repliesQueue;
    }

    public void setClientHandler(ConnectionHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void setServerHandler(ConnectionHandler serverHandler) {
        this.serverHandler = serverHandler;
    }
}
