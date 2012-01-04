package it.unisannio.aroundme.client;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class HttpStatusException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private int code;
	
	public HttpStatusException(int code, String message) {
		super("[" + code + "]" + message);
		this.code = code;
	}
	
	public HttpStatusException(int code) {
		super("[" + code + "]");
		this.code = code;
	}
	
	public int getStatusCode() {
		return code;
	}
}
