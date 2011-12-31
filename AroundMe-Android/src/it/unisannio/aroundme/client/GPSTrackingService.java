package it.unisannio.aroundme.client;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.SerializerUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Node;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 
 * Servizio di tracciamento.
 * 	Il servizio, che viene eseguito in modalit&agrave; background, rileva la posizione dell'utente 
 * solo se egli si sposta di 5 metri rispetto alla posizione corrente e solo ogni 5 secondi
 * 
 * Il vincolo della distanza &egrave; stato imposto per risparmiare risorse sul device
 * 
 * Chiaramente il vincolo non vale (per la sua totalit&egrave;) nel caso l'utente si spostasse ad una velocit&agrave;
 * elevata (es: 30 Km/h), in quanto in un tempo relativamente piccolo (5 secondi) l'utente effettua uno
 * spostamento di molti metri (es: 10m) e, di conseguenza, non avrebbe l'informazione temporale precisa
 * degli spostamenti degli altri utenti, ma con una approsimazione.
 * 
 * una volta rilevata la posizione secondo i criteri sopra elencati, il servizio invier&agrave; la posizione dell'utente
 * all server Google App Engine mediante messaggi HTTP
 * 
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 */
public class GPSTrackingService extends IntentService {

	public GPSTrackingService() {
		super("GPSTrackingservice");
	}

	/**
	 * La distanza (in metri) del minimo spostamento affinch&egrave; il servizio possa rilevare la posizione
	 */
	private final int distance = 5;

	private LocationManager loc_manager;
	private LocationListener loc_listener; 

	@Override
	public void onCreate() {
		super.onCreate();
		loc_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		loc_listener = new LocationListener() {

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}

			public void onProviderEnabled(String arg0) {
				Toast.makeText(getApplicationContext(), "Il GPS � abilitato", Toast.LENGTH_SHORT).show();
			}

			public void onProviderDisabled(String arg0) {
				Toast.makeText(getApplicationContext(), "Il GPS � disabilitato", Toast.LENGTH_SHORT).show();
				try{
					loc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, list);
				}
				catch (Exception e) {
					stopSelf();
				}
			}

			public void onLocationChanged(Location arg0) {
				URL url = new URL("http://www.aroundme.appengine.com/user/" +idUtenteDispositivo +"/position");
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				Position p = ModelFactory.getInstance().createPosition(arg0.getLatitude(), arg0.getLongitude());
				Node xml = Position.SERIALIZER.toXML(p) ;
				SerializerUtils.writeXML(xml, conn.getOutputStream());
				out.close();   
			}
		};

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		loc_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, distance, loc_listener);
		return super.onStartCommand(intent, 0, IntentService.START_STICKY);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		loc_manager = null;
		list_listener = null;
	}
}
