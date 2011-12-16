package it.unisannio.aroundme.client;

import it.unisannio.aroundme.model.DataListener;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * 
 */
public class Picture {
	private DataService service;
	private URL url;
	private SoftReference<Bitmap> cache;
	
	public Picture(DataService service, URL url) {
		this.service = service;
		this.url = url;
	}
	
	public Picture(DataService service, long id) {
		try {
			this.service = service;
			this.url = new URL("https://graph.facebook.com/" + id + "/picture");
		} catch (MalformedURLException e) {}
	}
	
	public URL getURL() {
		return url;
		
	}
	
	public void load(DataListener<Bitmap> listener) {
		Bitmap cachedBmp = null;
		if(cache != null && (cachedBmp = cache.get()) != null) {
			listener.onData(cachedBmp);
			return;
		}

		try {
			service.asyncHttpGet(url, new Transformer<InputStream, Bitmap>() {

				@Override
				public Bitmap transform(InputStream input) throws Exception {
					Bitmap bmp = BitmapFactory.decodeStream(input);
					if(bmp == null) 
						throw new RuntimeException("Cannot decode image");
					
					cache = new SoftReference<Bitmap>(bmp);
					
					return bmp;
				}
				
			}, listener);
		} catch (Exception e) {
			listener.onError(e);
		}
	}
	
	public void cleanCache() {
		this.cache = null;
	}
}
