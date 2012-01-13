package it.unisannio.aroundme.async;

/**
 * Oggetto che viene notificato nel momento in cui &egrave; terminata una richiesta in background.
 * 
 * Nel momento in cui i dati siano stati correttamente caricati (per esempio dalla rete o da un DB), 
 * viene richiamato il metodo {@link #onSuccess(V)}. In caso di errori, viene notificato 
 * {@link #onError(Throwable)}.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <V> Il tipo di dato caricato.
 */
public interface FutureListener<V> {
	
	/**
	 * Metodo richiamato nel momento in cui il task &egrave; stato completato in modo corretto.
	 * 
	 * @param object Il risultato dell'operazione
	 */
	void onSuccess(V object);
	
	/**
	 * Richiamato quando si &egrave verificato un errore nel caricamento dei dati.
	 * 
	 * @param e Eccezione contenente informazioni circa l'errore verificatosi.
	 */
	void onError(Throwable e);
}
