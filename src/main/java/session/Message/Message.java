package main.java.session.Message;

public class Message {
    private String srcIP;
    private int srcPort;
    private String dstIP;
    private int dstPort;
    private int tunnelID;
    private int sessionID;

    public Message(String srcIP, int srcPort, String dstIP, int dstPort) {
        super();
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.dstIP = dstIP;
        this.dstPort = dstPort;
    }

    public Message() {
    }

    public void setTunnelID(int ID) {
        this.tunnelID = ID;
    }

    public int getTunnelID() {
        return tunnelID;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }
}
