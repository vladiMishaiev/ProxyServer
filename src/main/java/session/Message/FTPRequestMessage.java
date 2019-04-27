package main.java.session.Message;

import main.java.session.Message.File.ProxyFile;

public class FTPRequestMessage extends Message {
    private FTPMessageType messageType;
    private FTPCommandType commandType;
    private String moreInfo;
    private String rawMessage;
    private String host = "";
    private String user = "";
    private String passward = "";
    private ProxyFile file;
    /*
        type - command / data
        info - port ... upload, download, string/message

        data network info IP and port
     */

    public FTPRequestMessage(String srcIP, int srcPort, String dstIP, int dstPort,
                             FTPMessageType messageType, FTPCommandType commandType, String moreInfo) {
        super(srcIP, srcPort, dstIP, dstPort);
        this.messageType = messageType;
        this.commandType = commandType;
        this.moreInfo = moreInfo;
        this.file = new ProxyFile();
    }

    public FTPRequestMessage(){
        super();
        this.file = new ProxyFile();
    }

    public FTPMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(FTPMessageType messageType) {
        this.messageType = messageType;
    }

    public FTPCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(FTPCommandType commandType) {
        this.commandType = commandType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassward() {
        return passward;
    }

    public void setPassward(String passward) {
        this.passward = passward;
    }

    public void setCommandType(String commandString) {
        switch (commandString.toLowerCase()){
            case "user":{
                this.commandType = FTPCommandType.USER;
            }break;
            case "cwd":{
                this.commandType = FTPCommandType.CWD;
            }break;
            case "pass":{
                this.commandType = FTPCommandType.PASS;
            }break;
            case "open":{
                this.commandType = FTPCommandType.OPEN;
            }break;
            case "port":{
                this.commandType = FTPCommandType.PORT;
            }break;
            case "list":{
                this.commandType = FTPCommandType.LIST;
            }break;
            case "type":{
                this.commandType = FTPCommandType.TYPE;
            }break;
            case "retr":{
                this.commandType = FTPCommandType.RETR;
            }break;
            case "stor":{
                this.commandType = FTPCommandType.STOR;
            }break;
            default:{
                this.commandType = FTPCommandType.BYPASS;
                //System.out.println("bypass-"+commandString);
            }break;


        }



        this.commandType = commandType;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean parseRawMessage (String rawMessage){
        this.setRawMessage(rawMessage);

        String[] parsedMessage = rawMessage.split(" ");
        String command = parsedMessage[0];
        setCommandType(command);
        setMoreInfo(rawMessage.substring(command.length()).trim());

        switch (commandType){
            case USER:{
                if (moreInfo.contains("@")){
                    setUser(moreInfo.split("@")[0].trim());
                    setHost(moreInfo.split("@")[1].trim());
                }break;
            }
            case PASS:{
                setPassward(moreInfo.trim());
            }break;
        }

        return true;
    }

    public ProxyFile getFile() {
        return file;
    }

    public void setFile(ProxyFile file) {
        this.file = file;
    }
}
