package it.unisannio.aroundme.client.pictures;

import it.unisannio.aroundme.http.PersistenceService;
import it.unisannio.aroundme.http.Trasformer;
import it.unisannio.aroundme.model.DataListener;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.concurrent.Future;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * 
 */
public class Picture {
	private Future<PersistenceService> service;
	private URL url;
	private SoftReference<Bitmap> cache;
	
	public Picture(Future<PersistenceService> service, URL url) {
		this.service = service;
		this.url = url;
	}
	
	URL getURL() {
		return url;
		
	}
	
	public void load(DataListener<Bitmap> listener) {
		Bitmap cachedBmp = null;
		if(cache != null && (cachedBmp = cache.get()) != null) {
			listener.onData(cachedBmp);
			return;
		}

		try {
			service.get().asyncHttpGet(url, new Trasformer<InputStream, Bitmap>() {

				@Override
				public Bitmap trasform(InputStream input) throws Exception {
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
}
