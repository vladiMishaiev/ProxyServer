package main.java.event;

import com.google.gson.annotations.Expose;
import main.java.GlobalManager;
import main.java.config.StateInfo;
import main.java.utils.MyUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static main.java.utils.Constants.STATE_INFO_PATH;

public abstract class EventBase {
    private static AtomicInteger idGenerator = new AtomicInteger((
            (StateInfo) MyUtils.loadConfigFromFile(StateInfo.class,STATE_INFO_PATH)).getEventsStartID());
    @Expose
    private int ID;

    public EventBase(){
        this.ID = idGenerator.getAndIncrement();
    }

    public static void setIdGenerator(int value){
        idGenerator.set(value);
    }

    public static AtomicInteger getIdGenerator (){
        return idGenerator;
    }

    @Override
    public String toString() {
        return "EventBase{" +
                "ID=" + ID +
                '}';
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
