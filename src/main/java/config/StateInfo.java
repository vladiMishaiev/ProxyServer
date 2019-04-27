package main.java.config;

import com.google.gson.annotations.Expose;

public final class StateInfo {
    @Expose
    private int eventsStartID = 0;

    public StateInfo(){}

    public StateInfo(int id)
    {
        this.setEventsStartID(id);
    }

    public int getEventsStartID() {
        return eventsStartID;
    }

    public void setEventsStartID(int eventsStartID) {
        this.eventsStartID = eventsStartID;
    }
}
