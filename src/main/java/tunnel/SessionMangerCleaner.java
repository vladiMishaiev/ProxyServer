package main.java.tunnel;

import main.java.session.SessionManager;

public class SessionMangerCleaner implements Runnable{
    private SessionManager sessionManager;

    public SessionMangerCleaner (SessionManager sessionManger){
        this.sessionManager = sessionManger;
    }

    @Override
    public void run() {
        //iterate manager and close inactive sessions





    }
}
