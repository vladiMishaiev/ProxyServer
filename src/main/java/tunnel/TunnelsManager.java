package main.java.tunnel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import main.java.GlobalManager;
import main.java.event.EventBase;
import main.java.session.ProxySession;

public class TunnelsManager {
	//private final static Logger logger = Logger.getLogger(TunnelsManager.class);
	//private static final AtomicInteger idGenerator = new AtomicInteger();
	//private static final String tunnelIDGeneratorFilePath = new File("src\\config\\TunnelIDGenerator.txt").getAbsolutePath();

	private ArrayList<Tunnel> tunnelsList;
	private BlockingQueue<ProxySession> securityQueue;
	private BlockingQueue<EventBase> eventsQueue;

	public TunnelsManager(BlockingQueue<ProxySession> securityQueue) {
		tunnelsList = new ArrayList<>();
		this.securityQueue = securityQueue;
		this.eventsQueue = GlobalManager.getEventsQueue();
		this.initTunnels();
		this.startTunnels();
	}
	
	public Tunnel getTunnel(int tunnelID) {
		Iterator<Tunnel> itr = tunnelsList.iterator();
		while (itr.hasNext()) {
			Tunnel tempTunnl = itr.next();
			if (tempTunnl.getID() == tunnelID)
				return tempTunnl;
		}
		return null;
	}
	
	public boolean addTunnel(Tunnel tunnel) {
		tunnelsList.add(tunnel);
		return true;
	}
	
	public void removeAllTunnels() {
		tunnelsList.clear();
	}
	
	public void initTunnels() {
		this.tunnelsList = GlobalManager.getConfigManager().getTunnelsConfig().getTunnels();
		//Iterate tunnels and reset tunnels config
		Iterator<Tunnel> itr = this.tunnelsList.iterator();
		while (itr.hasNext()){
			itr.next().init();
		}

		//Generate tunnels from configuration file
		//tunnelsList.add(new Tunnel(1, "127.0.0.1", 8000));
		//tunnelsList.add(new Tunnel(2, "127.0.0.1", 8010));
		//tunnelsList.add(new Tunnel(3, "127.0.0.1", 8020));
		//tunnelsList.add(new Tunnel(4, "127.0.0.1", 8030,TunnelType.FTP_TUNNEL));
		//tunnelsList.add(new Tunnel(4, "127.0.0.1", 21));
	}
	
	public void startTunnels() {
		Iterator<Tunnel> itr = tunnelsList.iterator();
		while (itr.hasNext()) {
			new Thread(itr.next()).start();
		}
	}

	/*private void writeIntToFile(int value, String filePath) {
		Writer wr;
		try {
			wr = new FileWriter(filePath);
			wr.write(String.valueOf(value));
			wr.close();
			//logger.info("Saves tunnel id generator value");
			System.out.println("Saves tunnel id generator value");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/*private void writeIntToFile(String filePath) {
		writeIntToFile(0, filePath);
	}*/

	/*private int readIntFromFile(String filePath) {
		File f = null;
		Scanner scan = null;
		try {
			f = new File(filePath);
			scan = new Scanner(f);
		} catch (Exception e) {
			writeIntToFile(filePath);
			//logger.warn("Could not find tunnel id generator");
			return 0;
		}

		int value = 0;
		if (scan.hasNext())
			value = scan.nextInt();
		scan.close();
		//logger.info("Id generator value loaded succesfully");
		return value;
	}*/

/*	public static void main(String[] args) {
		String str = new File("src\\config\\TunnelIDGenerator.txt").getAbsolutePath();
		writeIntToFile(999,str);
		System.out.println(str);
	}*/
}
