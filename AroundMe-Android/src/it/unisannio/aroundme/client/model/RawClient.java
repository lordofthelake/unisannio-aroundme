package it.unisannio.aroundme.client.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

import it.unisannio.aroundme.model.DataListener;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class RawClient implements Client<byte[]> {
	private String endpoint;
	
	public RawClient(String endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public void get(String path, final DataListener<byte[]> listener) {
		this.asyncRequest(path, "GET", null, listener);
	}

	@Override
	public void put(String path, byte[] data, DataListener<byte[]> listener) {
		this.asyncRequest(path, "PUT", data, listener);
	}

	@Override
	public void post(String path, final byte[] data, final DataListener<byte[]> listener) {
		this.asyncRequest(path, "POST", data, listener);
	}

	@Override
	public void delete(String path, DataListener<byte[]> listener) {
		this.asyncRequest(path, "DELETE", null, listener);
	}
	
	private void asyncRequest(String path, final String method, final byte[] data, final DataListener<byte[]> listener) {
		try {
			URL url = new URL(endpoint + path);
			new AsyncTask<URL, Integer, byte[]>() {
	
				@Override
				protected byte[] doInBackground(URL... params) {
					HttpURLConnection urlConnection = null;
					try {
						urlConnection = (HttpURLConnection) params[0].openConnection();
						urlConnection.setRequestMethod(method);
						if(data != null) {
							urlConnection.setDoOutput(true);
						    urlConnection.setFixedLengthStreamingMode(data.length);
	
						    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
						    out.write(data);
						}

					    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						 
						for(int b = 0; (b = in.read()) > 0; bout.write(b));
						return bout.toByteArray();
						   
					} catch (Exception e){
						listener.onError(e);
					} finally {
						if(urlConnection != null)
							urlConnection.disconnect();
					}
					
					return null;
				}
				
				protected void onPostExecute(byte[] result) {
					listener.onData(result);
				};
				
			}.execute(url);
		} catch (MalformedURLException e) {
			listener.onError(e);
		}
	}
}
