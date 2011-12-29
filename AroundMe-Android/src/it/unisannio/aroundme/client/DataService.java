package it.unisannio.aroundme.client;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class DataService extends Service {
	
	public static class ServiceBinder extends Binder {
		
		/*
		 * Service leakage prevention.
		 * @see http://code.google.com/p/android/issues/detail?id=6426
		 * @see http://www.ozdroid.com/#!BLOG/2010/12/19/How_to_make_a_local_Service_and_bind_to_it_in_Android
		 */
		private final WeakReference<DataService> ref;
		
		public ServiceBinder(DataService s) {
			this.ref = new WeakReference<DataService>(s);
		}
		
		public DataService getService() {
			return ref.get();
		}
	}
	
	private ServiceBinder binder = new ServiceBinder(this);
	
	private ThreadPoolExecutor pool;
		
	@Override
	public void onCreate() {
		// TODO Add cache
		
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		pool = new ThreadPoolExecutor(0, 4, 10, TimeUnit.SECONDS, queue);
		
		super.onCreate();
	}
	
	public <T> Future<T> asyncDo(Callable<T> action) {
		FutureTask<T> task = new FutureTask<T>(action);
		pool.execute(task);
		return task;
	}
	
	public <T> Future<T> asyncDo(final Callable<T> action, final DataListener<T> listener) {
		final Handler handler = new Handler();
		return this.asyncDo(new Callable<T>() {
			@Override
			public T call() throws Exception {
				try {
					final T result = action.call();
					// FIXME Handle cancellation
					handler.post(new Runnable() { 
						public void run() {
							listener.onData(result); 
						}
					});

					return result;
				} catch (final Exception e) {
					handler.post(new Runnable() { 
						public void run() {
							listener.onError(e); 
						}
					});
				}
				
				return null;
			}
			
		});
	}
	
	@Override
	public void onDestroy() {
		pool.shutdownNow();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
}
