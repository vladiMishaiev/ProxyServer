package main.java.session.connectionHandlers;

import main.java.GlobalManager;
import main.java.session.Message.*;
import main.java.session.ProxySession;
import main.java.session.SessionType;
import org.apache.commons.net.ftp.FTPFile;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class FTPClientConnectionHandler extends ConnectionHandler {
    private Socket dataSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isAuthProvided = false; //provided User,Pass,Host
    private boolean isAuthValidated = false; //connected to real server

    public FTPClientConnectionHandler (Socket clientSocket, BlockingQueue<Message> repliesQueue, BlockingQueue<Message> requestsQueue){
        this.socket = clientSocket;
        this.repliesQueue = repliesQueue;
        this.requestsQueue = requestsQueue;
        this.message = new FTPRequestMessage();
        this.message.setSrcIP(clientSocket.getRemoteSocketAddress().toString().substring(1).split(":")[0]);
        //System.out.println("SRC ip is : " + message.getSrcIP());
        this.message.setSrcPort(clientSocket.getPort());
        //System.out.println("SRC ip is : " + message.getSrcPort());

    }

    @Override
    public void run() {
        FTPRequestMessage FTPRequest= (FTPRequestMessage)message;
        FTPResponseMessage FTPResponse;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("proxy-client: 220 (vsFTPd 3.0.3)");
            out.println("220 (vsFTPd 3.0.3)");
            String msg = "";
            boolean listening = true;
            while ( listening) {
                if (in.ready() && (msg = in.readLine()) != null) {
                    System.out.println("client-proxy: "+msg);
                    FTPRequest.parseRawMessage(msg);
                    System.out.println("***** command: " + FTPRequest.getCommandType());
                    switch(FTPRequest.getCommandType()){
                        case USER:{
                            if (FTPRequest.getPassward().compareTo("") == 0){
                                out.println("331 send password");
                                System.out.println("proxy-client: 331 send password");
                            }
                            if (FTPRequest.getHost().compareTo("") != 0 && FTPRequest.getPassward().compareTo("") != 0){
                                isAuthProvided = true;
                            }
                        }break;
                        case PASS:{
                            out.println("230 Login OK. Proceed.");
                            System.out.println("proxy-client: 230 Login OK. Proceed.");
                            if (FTPRequest.getHost().compareTo("") != 0){
                                isAuthProvided = true;
                            }
                        }break;
                        case OPEN:{

                        }break;
                        case LIST:{
                            ((FTPRequestMessage) message).setMessageType(FTPMessageType.FTP_COMMAND_MESSAGE);
                            requestsQueue.put(message);
                            FTPResponse = (FTPResponseMessage) repliesQueue.take();

                            //send files
                            PrintWriter dataOut = new PrintWriter(dataSocket.getOutputStream(),true);
                            //OutputStream dataOut = dataSocket.getOutputStream();
                            out.println("150 Here comes the directory listing.");
                            System.out.println("proxy-client: 150 Here comes the directory listing.");
                            String files = "";
                            for (FTPFile file : FTPResponse.getFiles()) {
                                files+=file.getRawListing()+"\r\n";
                                System.out.println(file.getRawListing().trim());
                            }
                            dataOut.print(files);
                            System.out.println("error check: " + dataOut.checkError());
                            dataOut.close();
                            //dataSocket.close();
                            out.println(FTPResponse.getRawMessage());
                            System.out.println("proxy-client:"+FTPResponse.getRawMessage());


                        }break;
                        case CWD:{
                            ((FTPRequestMessage) message).setMessageType(FTPMessageType.FTP_COMMAND_MESSAGE);
                            System.out.println("****** MoreInfo: " + FTPRequest.getMoreInfo());
                            requestsQueue.put(message);
                            FTPResponse = (FTPResponseMessage) repliesQueue.take();
                            out.println(FTPResponse.getRawMessage());
                            System.out.println("proxy-client:"+FTPResponse.getRawMessage());
                        }break;
                        case RETR:{
                            ((FTPRequestMessage) message).setMessageType(FTPMessageType.FTP_COMMAND_MESSAGE);
                            requestsQueue.put(message);
                            FTPResponse = (FTPResponseMessage) repliesQueue.take();
                            out.println(FTPResponse.getRawMessage());
                            System.out.println("proxy-client:"+FTPResponse.getRawMessage());
                        }break;
                        case TYPE:{
                            out.println("200 Switching to Binary mode.");
                            System.out.println("proxy-client: 200 Switching to Binary mode.");
                        }break;
                        case PORT:{
                            String[] morInfoArr = FTPRequest.getMoreInfo().split(",");
                            int dstPort = Integer.parseInt(morInfoArr[morInfoArr.length-2])*256
                                    +Integer.parseInt(morInfoArr[morInfoArr.length-1]);
                            dataSocket = new Socket(socket.getRemoteSocketAddress().toString().substring(1).split(":")[0],dstPort);
                            System.out.println("client handler: data socket connected, closed ? " + dataSocket.isConnected()+ " " + dataSocket.isClosed()
                            + " port: " + dstPort);
                            out.println("200 Port command succesfull");
                            System.out.println("proxy-client: 200 Port command succesfull");
                        }break;
                        case PASV:{
                            out.println("502 Command not implemented");
                            System.out.println("proxy-client: 502 Command not implemented");
                        }break;
                        case STOR:{
                            out.println("150 Ok to send data.");
                            System.out.println("proxy-client: 150 Ok to send data.");
                            byte[] bytesArray = new byte[4096];
                            int bytesRead = -1;
                            InputStream inputStream = dataSocket.getInputStream();

                            File tempFile = File.createTempFile("tmp", ".tmp");
                            OutputStream outStream = new FileOutputStream(tempFile);
                            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                                outStream.write(bytesArray, 0, bytesRead);
                                System.out.println("server handler: inside while");
                                System.out.println("Reading data");
                            }
                            System.out.println("File ***************");
                            System.out.println(tempFile.toString());
                            System.out.println("Upload done");
                            FTPRequest.getFile().setData(tempFile);
                            inputStream.close();

                            ProxySession securitySession = new ProxySession();
                            securitySession.setSessionType(SessionType.FTP_SESSION);
                            securitySession.setClientHandler(this);
                            securitySession.setRequestsQueue(requestsQueue);
                            securitySession.setRepliesQueue(repliesQueue);
                            GlobalManager.getSecurityLayerQueue().put(securitySession);

                            out.println("226 Transfer complete.");
                        }break;
                        default:{
                            ((FTPRequestMessage) message).setMessageType(FTPMessageType.FTP_COMMAND_MESSAGE);
                            requestsQueue.put(message);
                            FTPResponse = (FTPResponseMessage) repliesQueue.take();
                            out.println(FTPResponse.getRawMessage());
                            System.out.println("proxy-client:"+FTPResponse.getRawMessage());
                        }break;
                    }

                    if (isAuthProvided && !isAuthValidated) {
                        //System.out.println("Sending login message to server");
                        ((FTPRequestMessage) message).setMessageType(FTPMessageType.FTP_LOGIN_MESSAGE);
                        requestsQueue.put(message);
                        //System.out.println("Request put queue size: " + requestsQueue.size());
                        FTPResponse = (FTPResponseMessage) repliesQueue.take();
                        //out.println(FTPResponse.getRawMessage());
                        //System.out.println("proxy-client:"+FTPResponse.getRawMessage());
                        if (FTPResponse.getCommandType() == FTPCommandType.LOGIN_SUCCESS) {
                            isAuthValidated = true;
                            System.out.println("client handler: recieved success from server handler");
                        }else{
                            isAuthProvided = false;
                        }
                    }
                }
                else {
                    Thread.sleep(500);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSession(ProxySession proxySession) {
        this.mySession = proxySession;
    }

    @Override
    public ProxySession getSession() {
        return this.mySession;
    }

    public Socket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public FTPRequestMessage getRequestMessage() {
        return (FTPRequestMessage)message;
    }
}
