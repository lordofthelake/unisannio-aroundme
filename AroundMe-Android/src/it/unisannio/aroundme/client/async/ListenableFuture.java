package it.unisannio.aroundme.client.async;


import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.os.Handler;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <V>
 */
public class ListenableFuture<V> extends FutureTask<V> {
	private FutureListener<V> listener = null;
	private final Handler handler;
	
	public ListenableFuture(Callable<V> callable, FutureListener<V> listener, Handler h) {
		super(callable);
		this.listener = listener;
		this.handler = h;
	}
	
	public ListenableFuture(Callable<V> callable, FutureListener<V> listener) {
		this(callable, listener, new Handler());
	}
	
	public ListenableFuture(Callable<V> callable) {
		this(callable, null);
	}
	
	public void setListener(FutureListener<V> listener) {
		this.listener = listener;
	}
	
	public FutureListener<V> getListener() {
		return listener;
	}
	
	@Override
	protected void done() {
		if(listener != null && !isCancelled()) {
			try {
				final V result = get();
				handler.post(new Runnable() {

					@Override
					public void run() {
						listener.onSuccess(result);
					}
					
				});
			} catch (final Exception e) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						listener.onError(e);
					}
					
				});
			}
		}
	}
}
