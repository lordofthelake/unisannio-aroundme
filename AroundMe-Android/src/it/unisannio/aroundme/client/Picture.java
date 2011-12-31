package it.unisannio.aroundme.client;


import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class Picture implements Callable<Bitmap> {
	private static Map<Long, Picture> instances = new HashMap<Long, Picture>();
	
	public static Picture get(long id) {
		if(!instances.containsKey(id)) 
			instances.put(id, new Picture(id));
		
		return instances.get(id);
	}
	
	private String url;
	private SoftReference<Bitmap> cache;
	
	public Picture(String url) {
		this.url = url;
	}
	
	private Picture(long id) {
		this(String.format(Setup.PICTURE_URL, id));
	}
	
	public String getURL() {
		return url;
	}
	
	public Bitmap call() throws Exception {
		Bitmap cachedBmp = null;
		if(cache != null && (cachedBmp = cache.get()) != null) {
			return cachedBmp;
		}
		Log.d("Picture", url);
		return (new HttpTask<Bitmap>("GET", url, null) {

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
