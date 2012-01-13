package it.unisannio.aroundme.client;

/**
 * Eccezione sollevata nel caso in cui una richiesta HTTP si completi con uno Status Code
 * di tipo 4xx o 5xx.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class HttpStatusException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private int code;
	
	/**
	 * Crea una nuova eccezione con il codice HTTP e il messaggio indicato.
	 * 
	 * @param code lo status code restituito dall'interazione remota
	 * @param message un messaggio esplicativo dell'errore
	 */
	public HttpStatusException(int code, String message) {
		super("[" + code + "]" + message);
		this.code = code;
	}
	
	/**
	 * Crea una nuova eccezione con il codice HTTP indicato.
	 *  
	 * @param code lo status code restituito dall'interazione remota
	 */
	public HttpStatusException(int code) {
		super("[" + code + "]");
		this.code = code;
	}
	
	/**
	 * Restituisce lo status code associato a questa eccezione.
	 * 
	 * @return lo status code HTTP
	 */
	public int getStatusCode() {
		return code;
	}
}
