package it.unisannio.aroundme.client;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.client.async.AsyncQueue;
import it.unisannio.aroundme.client.async.FutureListener;
import it.unisannio.aroundme.client.async.ListenableFuture;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class Picture implements Callable<Bitmap> {
	
	/** 
	 * Workaround per vecchie versioni di BitmapFactory.
	 *  
	 * <blockquote>
	 * <p>A bug in the previous versions of {@link BitmapFactory#decodeStream(InputStream)} may prevent the code 
	 * from working over a slow connection. Decode a {@code new FlushedInputStream(inputStream)} instead to fix 
	 * the problem.</p>
	 * 
	 * <p>This ensures that skip() actually skips the provided number of bytes, unless we reach the end of file.</p>
	 * </blockquote>
	 * 
	 * @see http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
	 */
	private static class FlushedInputStream extends FilterInputStream {
	    public FlushedInputStream(InputStream inputStream) {
	        super(inputStream);
	    }

	    @Override
	    public long skip(long n) throws IOException {
	        long totalBytesSkipped = 0L;
	        while (totalBytesSkipped < n) {
	            long bytesSkipped = in.skip(n - totalBytesSkipped);
	            if (bytesSkipped == 0L) {
	                  int b = read();
	                  if (b < 0) {
	                      break;  // we reached EOF
	                  } else {
	                      bytesSkipped = 1; // we read one byte
	                  }
	           }
	            totalBytesSkipped += bytesSkipped;
	        }
	        return totalBytesSkipped;
	    }
	}
	
	private static Map<Long, Picture> instances = new HashMap<Long, Picture>();
	
	public static Picture get(long id) {
		if(!instances.containsKey(id)) 
			instances.put(id, new Picture(id));
		
		return instances.get(id);
	}
	
	private long id;
	private SoftReference<Bitmap> cache;
		
	private Picture(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public Bitmap call() throws Exception {
		Bitmap cachedBmp = null;
		if(cache != null && (cachedBmp = cache.get()) != null) {
			return cachedBmp;
		}

		return (new HttpTask<Bitmap>((Identity) null, "GET", Setup.PICTURE_URL, id) {

			@Override
			protected Bitmap read(InputStream in) throws Exception {
				Bitmap bmp = BitmapFactory.decodeStream(new FlushedInputStream(in));
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
	
	public void asyncUpdate(AsyncQueue async, final ImageView view, int defaultRes, final int errorRes) {
		Long pictureId = (Long) view.getTag(R.id.tag_pictureid);
		if(pictureId != null) {
			if(pictureId.equals(getId())) 
				return;
			
			view.setTag(R.id.tag_pictureid, null);
			view.setImageResource(defaultRes);
			
			Object taskObj = view.getTag(R.id.tag_task);
			if(taskObj != null && taskObj instanceof ListenableFuture<?>) {
				((ListenableFuture<?>) taskObj).cancel(false);
				view.setTag(R.id.tag_task, null);
			}
		}
		
		ListenableFuture<Bitmap> task = async.exec(this);
		
		view.setTag(R.id.tag_pictureid, getId());
		view.setTag(R.id.tag_task, task);
		
		task.setListener(new FutureListener<Bitmap>() {
			@Override
			public void onSuccess(Bitmap object) {
				view.setImageBitmap(object);
				view.setTag(R.id.tag_task, null);
			}

			@Override
			public void onError(Exception e) {
				view.setImageResource(errorRes);
				view.setTag(R.id.tag_task, null);
			}
		});
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Picture))
			return false;
		
		return ((Picture) o).getId() == getId();
	}
}
