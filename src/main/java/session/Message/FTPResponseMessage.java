package main.java.session.Message;

import org.apache.commons.net.ftp.FTPFile;

public class FTPResponseMessage extends Message {
    private FTPMessageType messageType;
    private FTPReplyStatusCode statusCode;
    private String moreInfo;
    private String rawMessage;
    private FTPCommandType commandType;
    private FTPFile[] files;

    public FTPResponseMessage (){

    }

    public FTPMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(FTPMessageType messageType) {
        this.messageType = messageType;
    }

    public FTPReplyStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(FTPReplyStatusCode statusCode) {
        this.statusCode = statusCode;
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

    public FTPCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(FTPCommandType commandType) {
        this.commandType = commandType;
    }

    public FTPFile[] getFiles() {
        return files;
    }

    public void setFiles(FTPFile[] files) {
        this.files = files;
    }
}
