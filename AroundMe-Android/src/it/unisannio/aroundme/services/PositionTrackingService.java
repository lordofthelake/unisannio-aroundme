package it.unisannio.aroundme.services;

import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

/**
 * Servizio di tracciamento della posizione.
 * 
 * <p>Il servizio, eseguito in background, rileva la posizione dell'utente solo per spostamenti di un'entit&agrave;
 * minima definita in {@link Setup#TRACKING_MIN_DISTANCE} e ad intervalli regolari di tempo (la quantit&agrave; esatta
 * &egrave; definita in {@link Setup#TRACKING_MIN_TIME}). Rilevazioni pi&ugrave; distanziate nel tempo
 * e nello spazio comportano un miglioramento della durata della batteria, a discapito di una minore
 * accuratezza.</p>
 * 
 * <p>Vengono utilizzati, se disponibili, sia il GPS che l'Android Network Location provider e viene fatta
 * una stima tra le loro misurazioni per scegliere quelle ritenute pi&ugrave; accurate.</p>
 * 
 * <p>Ad ogni rilevazione ritenuta affidabile, il servizio provvede ad aggiornare la posizione dell'utente
 * sul server di backend.</p>
 * 
 * @author Giuseppe Fusco?? <gfeldiablo@gmail.com>
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */

// TODO Il servizio dovr� essere fermato quando l'utente fa il logout
public class PositionTrackingService extends Service {

	/**
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	protected static class TrackingLocationListener implements LocationListener {
		/**
		 * Ultima posizione rilevata.
		 */
		private Location last = null;

		/**
		 * {@inheritDoc}
		 */
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

		/**
		 * {@inheritDoc}
		 */
		public void onProviderEnabled(String arg0) {}

		/**
		 * {@inheritDoc}
		 */
		public void onProviderDisabled(String arg0) {}

		/**
		 * Riceve la notifica quando &egrave; disponibile una nuova posizione.
		 * 
		 * Se &egrave; migliore della precedente, aggiorna il server.
		 * 
		 * @param Location la nuova posizione rilevata
		 */
		public void onLocationChanged(Location location) {
			if(isBetterLocation(location, last)) {
				last = location;

				final Position position = ModelFactory.getInstance().createPosition(
						location.getLatitude(), location.getLongitude());

				long id = Identity.get().getId();

				try {
					(new HttpTask<Void>("POST", Setup.BACKEND_POSITION_URL, id) {

						@Override
						protected Void read(InputStream in) throws Exception {
							return null;
						}

						@Override
						protected void write(OutputStream out) throws Exception {
							Position.SERIALIZER.write(position, out);
						}
					}).call();
				} catch (Exception e) {
					Log.d("PositionTrackingService", "Http Error", e);
				}
			}
		}
	};

	/** 
	 * Determina se la nuova posizione rilevata &egrave; migliore dell'ultima. 
	 * 
	 * L'implementazione segue l'algoritmo descritto nella documentazione Android per stimare la
	 * posizione quando le rilevazioni provengono da pi&ugrave; provider con accuratezze diverse.
	 * 
	 * @param location la nuova posizione da valutare
	 * @param last l'ultima posizione rilevata
	 * 
	 * @see http://developer.android.com/guide/topics/location/obtaining-user-location.html#BestEstimate
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	protected static boolean isBetterLocation(Location location, Location last) {
		if (last == null) 
			return true;

		long timeDelta = location.getTime() - last.getTime();

		// Se la rilevazione è stata presa molto più di recente, viene considerata migliore
		if (timeDelta > Setup.TRACKING_TIME_WINDOW) 
			return true;

		// Se è molto più vecchia dell'ultima, viene scartata
		if (timeDelta < -Setup.TRACKING_TIME_WINDOW) 
			return false;

		// Differenza di accuratezza tra vecchia e nuova posizione
		int accuracyDelta = (int) (location.getAccuracy() - last.getAccuracy());


		// L'ultima posizione è più accurata
		if (accuracyDelta < 0) 
			return true;

		// La posizione ha la stessa accuratezza ma è più recente
		if (timeDelta > 0 && accuracyDelta == 0) 
			return true;

		// Controlla se la vecchia e la nuova posizione provengono dallo stesso provider
		String provider1 = location.getProvider();
		String provider2 = location.getProvider();
		boolean isFromSameProvider = (provider1 == null && provider2 == null) || (provider1.equals(provider2));

		// La posizione è più recente e viene dallo stesso provider
		// L'accuratezza è inferiore ma entro un range accettabile
		if (timeDelta > 0 && accuracyDelta <= 200 && isFromSameProvider) 
			return true;

		return false;
	}

	private HandlerThread worker;
	private LocationManager locationManager;
	private LocationListener locationListener;

	@Override
	public void onCreate() {
		super.onCreate();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);	
		locationListener = new TrackingLocationListener();
	}

	/**
	 * Se non &egrave; gi&agrave; avvenuto in una chiamata precedente, avvia un thread in background e 
	 * registra il servizio per ricever&agrave; gli update della posizione.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// Un servizio può essere avviato più volte. 
		// Avviamo il worker thread e ci registriamo per le posizioni solo la prima volta.
		if(worker == null) {
			worker = new HandlerThread("PositionTrackingService", Process.THREAD_PRIORITY_BACKGROUND);
			worker.start();

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					Setup.TRACKING_MIN_TIME, 
					Setup.TRACKING_MIN_DISTANCE, 
					locationListener,
					worker.getLooper());

			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					Setup.TRACKING_MIN_TIME, 
					Setup.TRACKING_MIN_DISTANCE, 
					locationListener,
					worker.getLooper());
		}


		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		worker.stop();
		worker = null;

		locationManager.removeUpdates(locationListener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
