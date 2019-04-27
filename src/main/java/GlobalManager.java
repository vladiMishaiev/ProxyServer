package main.java;
import main.java.event.EventBase;
import main.java.session.ProxySession;
import main.java.tunnel.TunnelsManager;

import java.util.concurrent.BlockingQueue;

public class GlobalManager {
	public static final int dummy_value = 1000;

	private static ConfigurationsManager configManager = new ConfigurationsManager();
	private static BlockingQueue<ProxySession> securityLayerQueue = null;
	private static BlockingQueue<EventBase> eventsQueue = null;
	private static TunnelsManager tunnelsManager;

	private GlobalManager() {}

	public static void setConfigManager(){
		configManager = new ConfigurationsManager();
	}

	public static ConfigurationsManager getConfigManager(){
		return configManager;
	}

	public static BlockingQueue<ProxySession> getSecurityLayerQueue(){
		return securityLayerQueue;
	}
	
	public static TunnelsManager getTunnelsManager() {
		return tunnelsManager;
	}
	
	public static void setSecurityLayerQueue(BlockingQueue<ProxySession> securityLayerQueueArg) {
		securityLayerQueue = securityLayerQueueArg;
	}
	
	public static void setTunnelsManager (TunnelsManager tunnelsManagerArg) {
		tunnelsManager = tunnelsManagerArg;
	}

	public static BlockingQueue<EventBase> getEventsQueue() {
		return eventsQueue;
	}

	public static void setEventsQueue(BlockingQueue<EventBase> eventsQueue) {
		GlobalManager.eventsQueue = eventsQueue;
	}
}
