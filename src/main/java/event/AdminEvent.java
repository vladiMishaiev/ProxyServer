package main.java.event;

import com.google.gson.annotations.Expose;

public class AdminEvent extends EventBase{
    @Expose
    private String subType;
    @Expose
    private String source;
    @Expose
    private String description;

    public AdminEvent(String subType, String source, String description) {
        this.subType = subType;
        this.source = source;
        this.description = description;
    }

    @Override
    public String toString() {
        return "AdminEvent{" +
                "ID='" + super.getID()+ '\'' +
                "subType='" + subType + '\'' +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
