package main.java.config;

import com.google.gson.annotations.Expose;

public class AdminConfig {
    @Expose
    private String mngIP;
    @Expose
    private int mngPort;
    @Expose
    private String eventLogServerIP;
    @Expose
    private int eventLogServerPort;

    public AdminConfig(){

    }

    public String getMngIP() {
        return mngIP;
    }

    public void setMngIP(String mngIP) {
        this.mngIP = mngIP;
    }

    public int getMngPort() {
        return mngPort;
    }

    public void setMngPort(int mngPort) {
        this.mngPort = mngPort;
    }

    public String getEventLogServerIP() {
        return eventLogServerIP;
    }

    public void setEventLogServerIP(String eventLogServerIP) {
        this.eventLogServerIP = eventLogServerIP;
    }

    public int getEventLogServerPort() {
        return eventLogServerPort;
    }

    public void setEventLogServerPort(int eventLogServerPort) {
        this.eventLogServerPort = eventLogServerPort;
    }
}
