package main.java.session.Message;

import rawhttp.core.RawHttpResponse;

public class HTTPResponseMessage extends Message{
	private RawHttpResponse<?> response = null;

	public HTTPResponseMessage() {
		super();
	}

	public HTTPResponseMessage(String srcIP, int srcPort, String dstIP, int dstPort) {
		super(srcIP, srcPort, dstIP, dstPort);
	}

	public RawHttpResponse getResponse() {
		return response;
	}

	public void setResponse(RawHttpResponse<?> response) {
		this.response = response;
	}
	
	
}
