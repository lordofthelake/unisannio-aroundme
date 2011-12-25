package it.unisannio.aroundme.client;

public class HttpStatusException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private int code;
	
	public HttpStatusException(int code) {
		this.code = code;
	}
	
	public int getStatusCode() {
		return code;
	}
}
