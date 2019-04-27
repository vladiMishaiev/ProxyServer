package main.java.config;

import com.google.gson.annotations.Expose;
import main.java.tunnel.Tunnel;

import java.util.ArrayList;

public class TunnelsConfig {
    @Expose
    private ArrayList<Tunnel> tunnels;

    public TunnelsConfig(){
        tunnels = new ArrayList<>();
    }

    public ArrayList<Tunnel> getTunnels() {
        return tunnels;
    }

    public void setTunnels(ArrayList<Tunnel> tunnels) {
        this.tunnels = tunnels;
    }

    @Override
    public String toString() {
        return "TunnelsConfig{" +
                "tunnels=" + tunnels +
                '}';
    }
}
