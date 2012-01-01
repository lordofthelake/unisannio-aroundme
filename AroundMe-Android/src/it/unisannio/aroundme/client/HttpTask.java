package it.unisannio.aroundme.client;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Context;

/**
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 *
 * @param <T>
 */
public abstract class HttpTask<T> implements Callable<T> {	
	private final String url;
	private final String method;
	private final Map<String, String> headers = new HashMap<String, String>();
	private final Identity identity;
	
	public HttpTask(Identity identity, String method, String url, Object... args) {
		this(identity, method, String.format(url, args));
	}
	
	public HttpTask(Identity identity, String method, String url) {
		this.url = url;
		this.method = method;
		this.identity = identity;
	}
	
	public HttpTask(String method, String url, Object... args) {
		this(Identity.get(), method, url, args);
	}
	
	public HttpTask(String method, String url) {
		this(Identity.get(), method, url);
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	@Override
	public T call() throws Exception {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(this.url).openConnection();

			urlConnection.setConnectTimeout(Setup.NETWORK_TIMEOUT);
			
			urlConnection.setUseCaches(true);
			urlConnection.setRequestMethod(method);
			
			if(identity != null)
				urlConnection.setRequestProperty(Setup.BACKEND_AUTH_HEADER, identity.getAccessToken());
			
			for(Map.Entry<String, String> e : headers.entrySet())
				urlConnection.setRequestProperty(e.getKey(), e.getValue());
					
			if(!method.equalsIgnoreCase("get")) {
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
				throw new HttpStatusException(status);

		    InputStream in = new BufferedInputStream(urlConnection.getInputStream(), Setup.NETWORK_CHUNCK_SIZE);
		    
		    try {
		    	return read(in);
		    } finally {
		    	in.close();
		    }
		} finally {
			urlConnection.disconnect();
		}
	}
	
	
	protected abstract T read(InputStream in) throws Exception;
	
	protected void write(OutputStream out) throws Exception {
	
	}

}
