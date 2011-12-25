package it.unisannio.aroundme.client;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * 
 */
public class Picture implements Callable<Bitmap> {
	private static Map<Long, Picture> instances = new HashMap<Long, Picture>();
	
	public static Picture get(long id) {
		if(!instances.containsKey(id)) 
			instances.put(id, new Picture(id));
		
		return instances.get(id);
	}
	
	private URL url;
	private SoftReference<Bitmap> cache;
	
	public Picture(URL url) {
		this.url = url;
	}
	
	private Picture(long id) {
		try {
			this.url = new URL("https://graph.facebook.com/" + id + "/picture");
		} catch (MalformedURLException e) {}
	}
	
	public URL getURL() {
		return url;
		
	}
	
	public Bitmap call() throws Exception {
		Bitmap cachedBmp = null;
		if(cache != null && (cachedBmp = cache.get()) != null) {
			return cachedBmp;
		}

		return (new HttpTask<Bitmap>("GET", url) {

			@Override
			protected Bitmap read(InputStream in) throws Exception {
				Bitmap bmp = BitmapFactory.decodeStream(in);
				if(bmp == null) 
					throw new RuntimeException("Cannot decode image");
				
				cache = new SoftReference<Bitmap>(bmp);
				
				return bmp;
			}
			
		}).call();
	}
	
	public void reset() {
		this.cache = null;
	}
}
