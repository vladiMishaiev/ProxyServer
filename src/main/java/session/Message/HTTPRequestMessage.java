package main.java.session.Message;

import main.java.session.Message.File.ProxyFile;
import org.apache.http.NameValuePair;
import rawhttp.core.RawHttpRequest;

import java.util.ArrayList;
import java.util.List;

public class HTTPRequestMessage extends Message{
	private RawHttpRequest request = null;
	private List<NameValuePair> bodyParams;
	private List<ProxyFile> files;
	private boolean hasBody;
	private boolean hasBodyParams;
	private boolean isBodyTextual;

	public HTTPRequestMessage() {
		super();
		hasBodyParams = false;
		files = new ArrayList<>();
	}

	public HTTPRequestMessage(String srcIP, int srcPort, String dstIP, int dstPort) {
		super(srcIP, srcPort, dstIP, dstPort);
		hasBodyParams = false;
		files = new ArrayList<>();
	}

	public RawHttpRequest getRequest() {
		return request;
	}

	public void setRequest(RawHttpRequest request) {
		this.request = request;
	}

	public List<NameValuePair> getBodyParams() {
		return bodyParams;
	}

	public void setBodyParams(List<NameValuePair> bodyParams) {
		this.bodyParams = bodyParams;
	}

	public boolean isHasBodyParams() {
		return hasBodyParams;
	}

	public void setHasBodyParams(boolean hasBodyParams) {
		this.hasBodyParams = hasBodyParams;
		this.hasBodyParams = true;
	}

	public List<ProxyFile> getFiles() {
		return files;
	}

	public void setFiles(List<ProxyFile> files) {
		this.files = files;
	}
}
