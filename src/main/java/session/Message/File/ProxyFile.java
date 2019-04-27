package main.java.session.Message.File;

import java.io.File;

public class ProxyFile {
    private File data;
    private String fileName;
    private String fileTypeStr;
    private ProxyFileType fileType;

    public ProxyFile(){

    }

    public String getFileTypeStr() {
        return fileTypeStr;
    }

    public void setFileTypeStr(String fileTypeStr) {
        this.fileTypeStr = fileTypeStr;
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = new File(data.toPath().toString());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ProxyFileType getFileType() {
        return fileType;
    }

    public void setFileType(ProxyFileType fileType) {
        this.fileType = fileType;
    }
}
