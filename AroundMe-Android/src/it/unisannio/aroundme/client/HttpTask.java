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

public abstract class HttpTask<T> implements Callable<T> {
	private final String url;
	private final String method;
	private final Map<String, String> headers = new HashMap<String, String>();
	private final Identity identity;
	
	public HttpTask(String method, String url, Identity identity) {
		this.url = url;
		this.method = method;
		this.identity = identity;
	}
	
	public HttpTask(String method, String url) {
		this(method, url, Identity.get());
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	@Override
	public T call() throws Exception {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(this.url).openConnection();

			urlConnection.setConnectTimeout(30000); // 30 sec. connection timeout
			
			urlConnection.setUseCaches(true);
			urlConnection.setRequestMethod(method);
			
			if(identity != null)
				urlConnection.setRequestProperty(Constants.AUTH_HEADER, identity.getAccessToken());
			
			for(Map.Entry<String, String> e : headers.entrySet())
				urlConnection.setRequestProperty(e.getKey(), e.getValue());
					
			if(!method.equalsIgnoreCase("get")) {
				urlConnection.setDoOutput(true);
			    urlConnection.setChunkedStreamingMode(1024); // FIXME 1024 bytes? Is it optimal?

			    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream(), 1024);
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

		    InputStream in = new BufferedInputStream(urlConnection.getInputStream(), 1024);
		    
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
