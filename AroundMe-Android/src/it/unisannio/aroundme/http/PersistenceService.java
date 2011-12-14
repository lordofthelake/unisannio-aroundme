package it.unisannio.aroundme.http;

import it.unisannio.aroundme.model.DataListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class PersistenceService extends Service {
	private class PersistenceBinder extends Binder {
		public PersistenceService getService() {
			return PersistenceService.this;
		}
	}
	
	private static Map<Context, ServiceConnection> bindings = new HashMap<Context, ServiceConnection>();
	
	public static Future<PersistenceService> bind(final Context ctx) {
		return new Future<PersistenceService>() {
			private PersistenceService value = null;
			
			{
				ServiceConnection con = new ServiceConnection() {
	
					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						synchronized(value) {
							value = ((PersistenceService.PersistenceBinder)service).getService();
							notify();
						}
					}
	
					@Override
					public void onServiceDisconnected(ComponentName name) {
						synchronized(value) {
							value = null;
						}
					}
					
				};
				
				ctx.bindService(new Intent(ctx, PersistenceService.class), con, Context.BIND_AUTO_CREATE);
				bindings.put(ctx, con);
			}
			
			@Override
			public boolean cancel(boolean arg0) {
				return false;
			}

			@Override
			public PersistenceService get() throws InterruptedException,
					ExecutionException {
				synchronized(value) {
					while(value == null) wait();
					
					return value;
				}
			}

			@Override
			public PersistenceService get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return get(); // FIXME timeout ignored
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return value != null;
			}
			
		};
		
	}
	
	public static void unbind(Context ctx) {
		ServiceConnection con = bindings.get(ctx);
		ctx.unbindService(con);
	}
	
	private ThreadPoolExecutor pool;
		
	@Override
	public void onCreate() {
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		pool = new ThreadPoolExecutor(0, 4, 10, TimeUnit.SECONDS, queue);
		super.onCreate();
	}
	
	public <T> Future<T> asyncDo(final Callable<T> action, final DataListener<T> listener) {
		final Handler handler = new Handler();
		FutureTask<T> task = new FutureTask<T>(new Callable<T>() {
			@Override
			public T call() throws Exception {
				try {
					final T result = action.call();
					if(listener != null) {
						handler.post(new Runnable() { 
							public void run() {
								listener.onData(result); 
							}
						});
					}
					return result;
				} catch (final Exception e) {
					if(listener != null) {
						handler.post(new Runnable() { 
							public void run() {
								listener.onError(e); 
							}
						});
					} else throw e;
				}
				
				return null;
			}
			
		});
		
		pool.execute(task);
		
		return task;
	}

	public <T, U> Future<T> asyncHttpRequest(final URL url, final String method, final Trasformer<InputStream, T> dataReader, final Trasformer<OutputStream, Void> dataWriter, DataListener<T> listener) {
		return asyncDo(new Callable<T>() {

			@Override
			public T call() throws Exception {
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod(method);
				if(!method.equalsIgnoreCase("get")) {
					urlConnection.setDoOutput(true);
				    urlConnection.setChunkedStreamingMode(1024); // FIXME 1024 bytes? Is it optimal?

				    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
				    dataWriter.trasform(out);
				    
				}
				// FIXME handle 4xx, 5xx error status codes
			    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				return dataReader.trasform(in);	
			}
			
		}, listener);
	}
	
	public <T> void asyncHttpGet(URL url, Trasformer<InputStream, T> dataReader, DataListener<T> listener) {
		asyncHttpRequest(url, "GET", dataReader, null, listener);
	}
	
	@Override
	public void onDestroy() {
		// FIXME destroy thread pool
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new PersistenceBinder();
	}

}
