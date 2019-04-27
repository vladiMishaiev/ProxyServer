package main.java.session;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import main.java.tunnel.TunnelType;

public class SessionManager {
	private int tunnelID;
	private SessionType defaultSessionType;
	private ArrayList<ProxySession> sessionsList;
	
	public SessionManager(int tunnelID,TunnelType tunnelType) {
		this.tunnelID = tunnelID;
		sessionsList = new ArrayList<>();
		this.defaultSessionType = getDefaultSessionType(tunnelType);
	}
	
	public ProxySession getSessionByID (int ID) {
		Iterator<ProxySession> itr = sessionsList.iterator();
		while (itr.hasNext()) {
			ProxySession tempSession = itr.next();
			if (tempSession.getID() == ID) {
				return tempSession;
			}
		}
		return null;
	}

	public void addNewSession(Socket clientSocket) {
		//refactor
		//ProxySession newSession = new ProxySession(clientSocket,defaultSessionType);
		//newSession.setTunnelID(tunnelID);
		sessionsList.add(new ProxySession(clientSocket,defaultSessionType));
		//sessionsList.add(newSession);
	}

	public void addNewSession(ProxySession newSession) {
		//newSession.setTunnelID(tunnelID);
		newSession.setSessionType(defaultSessionType);
		sessionsList.add(newSession);
		//newSession.startHandlers();
	}
	
	private SessionType getDefaultSessionType(TunnelType tunnelType) {
		switch (tunnelType) {
		case HTTP_TUNNEL:
			return SessionType.HTTP_SESSION;
		case FTP_TUNNEL:
			return SessionType.FTP_SESSION;
		case SMTP_TUNNEL:
			return SessionType.SMTP_SESSION;
		case HTTPS_TUNNEL:
			return SessionType.HTTPS_SESSION;
		}
		return null;
	}
	
	
	
	
}
