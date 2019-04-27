package main.java.session.Message;

public enum FTPCommandType {
    HELLO,
    PORT,
    PASV,
    USER,
    PASS,
    CMD,
    LIST,
    TYPE,
    UPLOAD,
    DOWNLOAD,
    INFO,
    BYPASS,
    OPEN,
    CWD,
    RETR,
    STOR,
    LOGIN_SUCCESS,
    LOGIN_FAILURE
}
