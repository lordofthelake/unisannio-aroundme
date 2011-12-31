package it.unisannio.aroundme.client.async;


import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.os.Handler;
import android.os.Looper;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <V>
 */
public class ListenableFuture<V> extends FutureTask<V> {
	private FutureListener<V> listener = null;
	private final Handler handler;
	
	public ListenableFuture(Callable<V> callable, FutureListener<V> listener, Looper looper) {
		super(callable);
		this.listener = listener;
		this.handler = new Handler(looper);
	}
	
	public ListenableFuture(Callable<V> callable) {
		this(callable, null, null);
	}
	
	public synchronized void setListener(FutureListener<V> listener) {
		this.listener = listener;
		if(isDone()) notifyListener();
	}
	
	public FutureListener<V> getListener() {
		return listener;
	}
	
	private void notifyListener() {
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
	
	@Override
	protected synchronized void done() {
		notifyListener();
	}
}
