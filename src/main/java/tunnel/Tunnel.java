package main.java.tunnel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

//import org.apache.log4j.Logger;

import com.google.gson.annotations.Expose;
import main.java.GlobalManager;
import main.java.event.AdminEvent;
import main.java.session.ProxySession;
import main.java.session.SessionManager;

public class Tunnel implements Runnable{
	private static final int MAX_CONNECTIONS_BACKLOG_DEFAULT = 1;
	@Expose
	private int ID;
	@Expose
	private String IP;
	@Expose
	private int port;
	@Expose
	private TunnelType tunnelType;
	private ServerSocket server;
	@Expose
	private int maxConnectionsBackLog;
	private SessionManager sessionManager;
	
	public Tunnel(int id, String ip, int port, TunnelType tunnelType) {
		ID = id;
		IP = ip;
		this.port = port;
		this.tunnelType = tunnelType;
		this.maxConnectionsBackLog = MAX_CONNECTIONS_BACKLOG_DEFAULT;
		init();
	}
	public Tunnel(int id, String ip, int port) {
		this(id, ip, port, TunnelType.HTTP_TUNNEL);
	}

	public void init(){
		this.server = null;
		this.sessionManager = new SessionManager(ID,this.tunnelType);
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(port, maxConnectionsBackLog, InetAddress.getByName(IP));
			//logger.info("Tunnel initiated");
			System.out.println("Tunnel initiated");
			try{
				GlobalManager.getEventsQueue().put(new AdminEvent("subType","proxy","Tunnel initiated"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//open a new thread - session manger cleaner cons(session manager)
			while (true) {
				Socket socket = null;
				socket = server.accept();
				socket.setSoTimeout(5000);
				System.out.println("Got new connection TunnelID : " + this.ID);
				//sessionManager.addNewSession(new ProxySession(socket));
				sessionManager.addNewSession(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return ID;
	}
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	@Override
	public String toString() {
		return "Tunnel{" +
				"ID=" + ID +
				", IP='" + IP + '\'' +
				", port=" + port +
				", tunnelType=" + tunnelType +
				", server=" + server +
				", maxConnectionsBackLog=" + maxConnectionsBackLog +
				", sessionManager=" + sessionManager +
				'}';
	}
}
