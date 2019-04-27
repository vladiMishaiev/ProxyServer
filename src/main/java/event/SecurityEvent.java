package main.java.event;

import com.google.gson.annotations.Expose;

public class SecurityEvent extends EventBase{
    @Expose
    private String pattern;
    @Expose
    private String tunnelName;
    @Expose
    private int tunnelID;
    @Expose
    private String URI;
    @Expose
    private String srcIP;
    @Expose
    private int srcPort;
    @Expose
    private String source;
    @Expose
    private String description;

    public SecurityEvent(String pattern, String tunnelName, int tunnelID, String URI, String srcIP,
                         int srcPort, String source, String description) {
        this.pattern = pattern;
        this.tunnelName = tunnelName;
        this.tunnelID = tunnelID;
        this.URI = URI;
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.source = source;
        this.description = description;
    }

    public SecurityEvent(){ }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public int getTunnelID() {
        return tunnelID;
    }

    public void setTunnelID(int tunnelID) {
        this.tunnelID = tunnelID;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
