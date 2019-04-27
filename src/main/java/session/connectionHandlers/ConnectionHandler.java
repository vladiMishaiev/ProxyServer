package main.java.session.connectionHandlers;
import main.java.GlobalManager;
import main.java.session.Message.Message;
import main.java.session.ProxySession;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.SocketHandler;

public abstract class ConnectionHandler implements Runnable{
    public static final int CLIENT_SOCKET_TIMOUT_VALUE;// = 1000;
    private static final int SERVER_SOCKET_TIMOUT_VALUE = 1000;
    static{
        CLIENT_SOCKET_TIMOUT_VALUE = GlobalManager.dummy_value;
    }
    protected Message message;
    protected Socket socket;
    protected BlockingQueue<Message> repliesQueue;
    protected BlockingQueue<Message> requestsQueue;
    protected ProxySession mySession;

    public ConnectionHandler (){}
    public abstract void setSession(ProxySession proxySession);
    public abstract ProxySession getSession();
}
