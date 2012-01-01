package it.unisannio.aroundme.client.async;


import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Looper;


/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class AsyncQueue {
	
	/**
	 * Executor con un pool di thread di dimensioni massime fissate e possibilit&agrave; di mettere in 
	 * pausa i task.
	 * 
	 * Per diminuire il costo di creazione di nuovi thread, se sono liberi da task, i vecchi vengono
	 * riciclati. Thread senza task in esecuzione vengono mantenuti attivi per un certo tempo configurabile (Keep-Alive)
	 * prima di essere terminati (dove la piattaforma lo supporti).
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor
	 */
	protected static class PausableExecutor extends ThreadPoolExecutor {
		private boolean isPaused;
		private ReentrantLock pauseLock = new ReentrantLock();
		private Condition unpaused = pauseLock.newCondition();

		public PausableExecutor(int poolSize, int keepAlive) { 
			super(poolSize, poolSize, keepAlive, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()); 
			if(keepAlive > 0) {
				try {
					// Disponibile da API >= 9
					getClass().getMethod("allowCoreThreadTimeOut").invoke(this, true);
				} catch (Exception e) {}
			}
			
			// TODO Set background priority
		}

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			pauseLock.lock();
			try {
				while (isPaused) unpaused.await();
			} catch (InterruptedException ie) {
				t.interrupt();
			} finally {
				pauseLock.unlock();
			}
		}

		public void pause() {
			pauseLock.lock();
			try {
				isPaused = true;
			} finally {
				pauseLock.unlock();
			}
		}

		public void resume() {
			pauseLock.lock();
			try {
				isPaused = false;
				unpaused.signalAll();
			} finally {
				pauseLock.unlock();
			}
		}
	}

	private PausableExecutor pool;
	private Looper looper;
	
	public AsyncQueue(int poolSize, int keepAlive) {
		this.pool = new PausableExecutor(poolSize, keepAlive);
		this.looper = Looper.myLooper();
	}
	
	public AsyncQueue(int poolSize) {
		this(poolSize, 0);
	}
	
	public AsyncQueue() {
		this(1, 0);
	}
	
	public <V> void exec(ListenableFuture<V> task) {
		pool.execute(task);
	}
	
	public <V> ListenableFuture<V> exec(Callable<V> action) {
		return exec(action, null);
	}
	
	public <V> ListenableFuture<V> exec(final Callable<V> action, FutureListener<V> listener) {
		ListenableFuture<V> task = new ListenableFuture<V>(action, listener, looper);
		this.exec(task);
		return task;
	}
	
	public void pause() {
		pool.pause();
	}
	
	public void resume() {
		pool.resume();
	}
	
	public void shutdown() {
		pool.shutdownNow();
	}
}
