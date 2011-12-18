package it.unisannio.aroundme.client;

import it.unisannio.aroundme.client.model.ClientModelFactory;
import it.unisannio.aroundme.model.*;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class DataService extends Service {
	
	private static class ServiceBinder extends Binder {
		
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
	
	public static ServiceConnection bind(Context ctx, final Callback<DataService> cbk) {
		ServiceConnection con = new ServiceConnection() {
	
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				cbk.handle(((ServiceBinder)service).getService());
			}
	
			@Override
			public void onServiceDisconnected(ComponentName name) {}
			
		};
						
		ctx.bindService(new Intent(ctx, DataService.class), con, Context.BIND_AUTO_CREATE);
		return con;
	}
	
	private ServiceBinder binder = new ServiceBinder(this);
	
	private ThreadPoolExecutor pool;
	private PictureStore pictureStore;
		
	@Override
	public void onCreate() {
		// TODO Setup cache
		
		pictureStore = new PictureStore(this);
		ModelFactory.setInstance(new ClientModelFactory(this));
		
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		pool = new ThreadPoolExecutor(0, 4, 10, TimeUnit.SECONDS, queue);
		
		super.onCreate();
	}
	
	public <T> Future<T> asyncDo(Callable<T> action) {
		return asyncDo(action, null);
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

	public <T, U> Future<T> asyncHttpRequest(final URL url, final String method, final Transformer<InputStream, T> dataReader, final Callback<OutputStream> dataWriter, DataListener<T> listener) {
		return asyncDo(new Callable<T>() {

			@Override
			public T call() throws Exception {
				HttpURLConnection urlConnection = null;
				try {
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod(method);
					if(!method.equalsIgnoreCase("get")) {
						urlConnection.setDoOutput(true);
					    urlConnection.setChunkedStreamingMode(1024); // FIXME 1024 bytes? Is it optimal?
	
					    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream(), 1024);
					    try {
					    	dataWriter.handle(out);
					    } finally {
					    	out.close();
					    }
					    
					}
					// FIXME handle 4xx, 5xx error status codes
				    InputStream in = new BufferedInputStream(urlConnection.getInputStream(), 1024);
				    
				    try {
				    	return dataReader.transform(in);
				    } finally {
				    	in.close();
				    }
				} finally {
					urlConnection.disconnect();
				}
			}
			
		}, listener);
	}
	
	public <T> void asyncHttpGet(URL url, Transformer<InputStream, T> dataReader, DataListener<T> listener) {
		asyncHttpRequest(url, "GET", dataReader, null, listener);
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

	public PictureStore getPictureStore() {
		return pictureStore;
	}

	public User getMe() {
		// FIXME Mock method
		return ModelFactory.getInstance().createUser(1000, "Me", Collections.<Interest>emptySet());
	}

}
