package it.unisannio.aroundme.model;

/**
 * Oggetto che viene notificato nel momento in cui sono disponibili i dati richiesti.
 * 
 * Nel momento in cui i dati siano stati correttamente caricati (per esempio dalla rete o da un DB), l'utilizzatore dovrebbe richiamare il metodo
 * {@see onData(T)}. In caso di errori, dovrebbe essere notificato il metodo onError(Exception). A seconda dell'implementazione, il caricamento pu� essere sincrono
 * o asincrono.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <T> Il tipo di dato caricato.
 */
public interface DataListener<T> {
	
	/**
	 * Metodo richiamato nel momento in cui il caricamento dei dati � stato completato.
	 * 
	 * @param object L'oggetto caricato
	 */
	void onData(T object);
	
	/**
	 * Richiamato quando si � verificato un errore nel caricamento dei dati.
	 * 
	 * @param e Eccezione contenente informazioni circa l'errore verificatosi.
	 */
	void onError(Exception e);
}
