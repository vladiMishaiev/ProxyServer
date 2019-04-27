package main.java.session.connectionHandlers;

import main.java.session.Message.*;
import main.java.session.ProxySession;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.errors.InvalidHttpRequest;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class FTPServerConnectionHandler extends ConnectionHandler {
    private Socket serverSocket;
    private BlockingQueue<Message> requestsQueue;
    private BlockingQueue<Message> repliesQueue;
    private FTPClient ftp;
    private String currentDir;

    public FTPServerConnectionHandler(BlockingQueue<Message> requestsQueue,BlockingQueue<Message> repliesQueue){
        this.message = new FTPResponseMessage();
        this.requestsQueue = requestsQueue;
        this.repliesQueue = repliesQueue;
        ftp = new FTPClient();
        this.currentDir = "/";
    }

    @Override
    public void run() {
        FTPRequestMessage FTPRequest;
        FTPResponseMessage FTPResponse = (FTPResponseMessage) message;
        try{
            while (true) {
                Message requestMessage = requestsQueue.take();
                //System.out.println("Server handler got new message");
                if (requestMessage instanceof EndStreamMessage){
                    throw new InvalidHttpRequest("",1);
                }
                if (!(requestMessage instanceof FTPRequestMessage)) {
                    System.out.println("Problem");
                }
                FTPRequest = (FTPRequestMessage) requestMessage;
                if (FTPRequest.getMessageType() == FTPMessageType.FTP_LOGIN_MESSAGE || !ftp.isConnected() || !ftp.isAvailable()){
                    System.out.println("Server handler: new login request");
                    if (openFTPConnection(FTPRequest.getHost(),21,FTPRequest.getUser(),FTPRequest.getPassward())){
                        FTPResponse.setMessageType(FTPMessageType.FTP_LOGIN_MESSAGE);
                        FTPResponse.setCommandType(FTPCommandType.LOGIN_SUCCESS);
                        System.out.println("Server handler: Login success");
                    }else{
                        FTPResponse.setMessageType(FTPMessageType.FTP_LOGIN_MESSAGE);
                        FTPResponse.setCommandType(FTPCommandType.LOGIN_FAILURE);
                        System.out.println("Server handler: Login fail");
                    }
                    repliesQueue.put(FTPResponse);
                    continue;
                }

                if (FTPRequest.getMessageType() == FTPMessageType.FTP_COMMAND_MESSAGE){

                    switch (FTPRequest.getCommandType()){
                        case LIST:{
                            System.out.println("proxy-server: "+FTPRequest.getRawMessage());
                            ftp.enterLocalPassiveMode();
                            //ftp.enterLocalActiveMode();
                            //FTPFile[] files = ftp.listFiles("/");
                            FTPResponse.setFiles(ftp.listFiles(this.currentDir));
                            System.out.println("Server handler: done LIST command");
                            //FTPResponse.setRawMessage(ftp.getReplyString().replaceAll("(\\r|\\n)", ""));
                            FTPResponse.setRawMessage(ftp.getReplyString().trim());
                            System.out.println("server-proxy: " + FTPResponse.getRawMessage());
                            repliesQueue.put(FTPResponse);
                            /*for (FTPFile file : FTPResponse.getFiles()) {
                                System.out.println(file.getName());
                                System.out.println(file.toString());
                            }*/
                        }break;
                        case CWD:{
                            System.out.println("proxy-server: "+FTPRequest.getRawMessage());
                            ftp.changeWorkingDirectory("/"+FTPRequest.getMoreInfo());
                            this.currentDir += "/" + FTPRequest.getMoreInfo();
                            System.out.println("Server handler: done CWD command to " + FTPRequest.getMoreInfo());
                            FTPResponse.setRawMessage(ftp.getReplyString().trim());
                            System.out.println("server-proxy: " + FTPResponse.getRawMessage());
                            repliesQueue.put(FTPResponse);
                        }break;
                        case RETR:{
                            System.out.println("server handler : got RETR command");
                            System.out.println("server handler : session:" + mySession);
                            Socket clientDataSocket = ((FTPClientConnectionHandler)mySession.getClientHandler()).getDataSocket();
                            ftp.enterLocalPassiveMode();
                            ftp.setFileType(FTP.BINARY_FILE_TYPE);
                            //String remoteFile2 = "/test/song.mp3";
                            //File downloadFile2 = new File("D:/Downloads/song.mp3");
                            //OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
                            OutputStream clientOutData = clientDataSocket.getOutputStream();
                            InputStream inputStream = ftp.retrieveFileStream(FTPRequest.getMoreInfo());
                            System.out.println("server handler : before while : inputStream -" + inputStream.available());
                            byte[] bytesArray = new byte[4096];
                            int bytesRead = -1;
                            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                                System.out.println("server handler: inside while");
                                clientOutData.write(bytesArray, 0, bytesRead);
                            }

                            boolean success = ftp.completePendingCommand();
                            if (success) {
                                System.out.println("server handler : File has been downloaded successfully.");
                            }
                            clientOutData.close();
                            inputStream.close();

                            System.out.println("Server handler: done RETR command");
                            FTPResponse.setRawMessage(ftp.getReplyString().trim());
                            System.out.println("server-proxy: " + FTPResponse.getRawMessage());
                            repliesQueue.put(FTPResponse);
                        }break;
                        case STOR:{
                            System.out.println("server handler : got STOR command");
                            ftp.enterLocalPassiveMode();
                            ftp.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                            ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                            FileInputStream fis = new FileInputStream(FTPRequest.getFile().getData());
                            ftp.storeFile(FTPRequest.getMoreInfo(),fis);
                            fis.close();
                            System.out.println("server handler: file uploaded");
                        }break;
                        default: {
                            System.out.println("proxy-server:" + FTPRequest.getRawMessage());
                            int statusCode = ftp.sendCommand(FTPRequest.getRawMessage());
                            //FTPResponse.setRawMessage(ftp.getReplyString().replaceAll("(\\r|\\n)", ""));
                            FTPResponse.setRawMessage(ftp.getReplyString().trim());
                            System.out.println("server-proxy: "+FTPResponse.getRawMessage());
                            repliesQueue.put(FTPResponse);
                        }
                    }
                }
            }

    } catch (InvalidHttpRequest e) {
            System.out.println("error");
    } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("error");

        }
    catch (IOException e) {
            e.printStackTrace();
        System.out.println("error");

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

    public boolean openFTPConnection(String server,int port,String user,String password) throws IOException {
        //ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
        return ftp.login(user, password);
    }

    public void close() throws IOException {
        ftp.disconnect();
    }
}
