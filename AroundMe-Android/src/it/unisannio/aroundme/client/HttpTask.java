package it.unisannio.aroundme.client;


import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.async.AsyncQueue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.util.Log;

/**
 * Task per una richiesta HTTP con il backend remoto.
 * 
 * Implementa {@link Callable}, il che permette di eseguire la richiesta in background in una
 * {@link AsyncQueue}. 
 * 
 * <p>Le classi che la estendono dovrebbero generalmente implementare i 
 * metodi {@link HttpTask#read(InputStream)} e {@link #write(OutputStream)}, rispettivamente
 * per leggere e scrivere i dati necessari associati alla richiesta HTTP.</p>
 * 
 * <p>Supporto l'uso di una {@link Identity} per impostare automaticamente gli header di 
 * autenticazione (il nome dell'header usato &egrave; definito in {@link Setup#BACKEND_AUTH_HEADER}.</p>
 * 
 * @param <T> Il tipo di dato letto come risultato dell'interrogazione HTTP
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class HttpTask<T> implements Callable<T> {	
	private final String url;
	private final String method;
	private final Map<String, String> headers = new HashMap<String, String>();
	private final Identity identity;
	
	/**
	 * Crea un task con l'identit&agrave; assegnata e un URL parametrizzato.
	 * 
	 * La parametrizzazione dell'URL viene effettuata usando il formato supportato 
	 * da {@link Formatter} e {@link String#format(String, Object...)}.
	 * 
	 * @param identity l'identit&agrave; utilizzata, o {@code null} per effettuare una richiesta non autenticata
	 * @param method il metodo HTTP da utilizzare, es. {@code GET}, {@code POST} o {@code PUT}
	 * @param url un URL parametrizzabile
	 * @param args gli argomenti usati nella parametrizzazione
	 */
	public HttpTask(Identity identity, String method, String url, Object... args) {
		this(identity, method, String.format(url, args));
	}
	
	/**
	 * Crea un task con l'identit&agrave; assegnata.
	 * 
	 * @param identity l'identit&agrave; utilizzata, o {@code null} per effettuare una richiesta non autenticata
	 * @param method il metodo HTTP da utilizzare, es. {@code GET}, {@code POST} o {@code PUT}
	 * @param url l'URL a cui viene effettuata la richiesta
	 */
	public HttpTask(Identity identity, String method, String url) {
		this.url = url;
		this.method = method;
		this.identity = identity;
	}
	
	/**
	 * Crea un task con l'identit&agrave; di default e un URL parametrizzato.
	 * 
	 * La parametrizzazione dell'URL viene effettuata usando il formato supportato 
	 * da {@link Formatter} e {@link String#format(String, Object...)}.
	 * 
	 * @param method il metodo HTTP da utilizzare, es. {@code GET}, {@code POST} o {@code PUT}
	 * @param url un URL parametrizzabile
	 * @param args gli argomenti usati nella parametrizzazione
	 * 
	 * @see Identity#get()
	 */
	public HttpTask(String method, String url, Object... args) {
		this(Identity.get(), method, url, args);
	}
	
	/**
	 * Crea un task con l'identit&agrave; di default.
	 * 
	 * @param method il metodo HTTP da utilizzare, es. {@code GET}, {@code POST} o {@code PUT}
	 * @param url l'URL a cui viene effettuata la richiesta
	 */
	public HttpTask(String method, String url) {
		this(Identity.get(), method, url);
	}
	
	/**
	 * Imposta un header HTTP per la richiesta.
	 * 
	 * @param key il nome dell'header
	 * @param value il valore da assegnare
	 */
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	/**
	 * Esegue la richiesta HTTP.
	 * 
	 * @throws Exception in caso di errori durante la richiesta
	 * @throws HttpStatusException nel caso in cui la risposta abbia uno status code di tipo 4xx o 5xx
	 * @throws SocketTimeoutException nel caso in cui la richiesta vada in timeout
	 */
	@Override
	public T call() throws Exception {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(this.url).openConnection();

			urlConnection.setConnectTimeout(Setup.NETWORK_TIMEOUT);
			
			urlConnection.setUseCaches(true);
			urlConnection.setRequestMethod(method);
			
			Log.d("HttpTask", method + " " + url);
			if(identity != null)
				urlConnection.setRequestProperty(Setup.BACKEND_AUTH_HEADER, identity.getAccessToken());
			
			for(Map.Entry<String, String> e : headers.entrySet())
				urlConnection.setRequestProperty(e.getKey(), e.getValue());
					
			if(!method.equalsIgnoreCase("get") && !method.equalsIgnoreCase("delete")) {
				urlConnection.setDoOutput(true);
			    urlConnection.setChunkedStreamingMode(Setup.NETWORK_CHUNCK_SIZE);
			    
			    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream(), Setup.NETWORK_CHUNCK_SIZE);
			    try {
			    	write(out);
			    } finally {
			    	out.flush();
			    	out.close();
			    }
			    
			}
			
			int status = urlConnection.getResponseCode();
			if(status >= 400)
				throw new HttpStatusException(status, method + " " + url);

		    InputStream in = new BufferedInputStream(urlConnection.getInputStream(), Setup.NETWORK_CHUNCK_SIZE);
		    
		    try {
		    	return read(in);
		    } finally {
		    	in.close();
		    }
		} finally {
			if(urlConnection != null)
				urlConnection.disconnect();
		}
	}
	
	/**
	 * In caso di risposta favorevole (codice 2xx) legge il contenuto inviato dal server
	 * e lo converte in un oggetto opportuno.
	 * 
	 * @param in lo stream da cui leggere i contenuti della risposta
	 * @return l'oggetto risultato della conversione
	 * @throws Exception in caso di errori durante la lettura o la conversione
	 */
	protected abstract T read(InputStream in) throws Exception;
	
	/**
	 * Per metodi che richiedono l'invio di dati (es. {@code POST} o {@code PUT}), scrive i dati
	 * necessari sull'OutputStream associato alla richiesta.
	 * 
	 * L'implementazione di default non fa niente, pertanto le sottoclassi ne dovrebbero
	 * fare l'override nel caso in cui volessero fare l'invio effettivo di dati.
	 * 
	 * @param out lo stream su cui scrivere i dati
	 * @throws Exception in caso di errori durante la scrittura
	 */
	protected void write(OutputStream out) throws Exception { }

}
