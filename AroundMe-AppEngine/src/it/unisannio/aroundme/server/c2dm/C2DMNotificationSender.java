package it.unisannio.aroundme.server.c2dm;

import it.unisannio.aroundme.server.task.C2DMSenderTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * Utilizzato per inviare una notifica push ad un device tramite
 * Android Cloud to Device Messaging Framework di Google
 * 
 * @see http://code.google.com/android/c2dm/
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMNotificationSender {
	
	public static final String C2DM_URL = "https://android.clients.google.com/c2dm/send";
	
	/*
	 * Definizione dei nomi dei parametri da includere nei PostData
	 * necessari per l'utilizzo del C2DM
	 */
	private static final String UPDATE_CLIENT_AUTH = "Update-Client-Auth";
	private static final String PARAM_REGISTRATION_ID = "registration_id";
//	private static final String PARAM_DELAY_WHILE_IDLE = "delay_while_idle";
	private static final String PARAM_COLLAPSE_KEY = "collapse_key";
	
	/* Massimo intervallo di tempo (in millisecondi) tra i vari
	 * tentativi per la ripetizione di un Task della Google TaskQueue
	 */
	private static final int DATAMESSAGING_MAX_COUNTDOWN_MSEC = 3000;
	
	private static final Logger log = Logger.getLogger(C2DMNotificationSender.class.getName());
	
	
	/**
	 * Invia, tramite C2DM, un messaggio al device indicato dal registrationId,
	 * notificando la presenza, nella sua zona, di un utente con userId specificato
	 * 
	 * @param registrationId
	 *            L'id del device dell'utente che deve ricevere la notifica
	 * @param userId
	 *            L'id dell'utente la cui presenza deve essere segnalata
	 * 
	 * @return <code>true</code> se l'invio è andato a buon fine;
	 *         <code>false</code> se si è verificato un errore che potrebbe
	 *         essere risolto riprovando l'invio
	 * 
	 * @throws IOException
	 *             Se si sono verificati errori irreversibili nell'invio del
	 *             messaggio
	 */
	public static boolean send(String registrationId, long userId) throws IOException {
		//StringBuilder utilizzato per la creazione dei PostData
		StringBuilder postDataBuilder = new StringBuilder();
		
		//Aggiunta del parametro specificante il registrationId
		postDataBuilder.append(PARAM_REGISTRATION_ID +"="+registrationId);

		postDataBuilder.append("&" + PARAM_COLLAPSE_KEY + "=" + 0);

		//Aggiunta del parametro che specifica l'userId dell'utente di cui notificare
		postDataBuilder.append("&data.userId="+userId);

		byte[] postData = postDataBuilder.toString().getBytes("UTF-8");

		//Url utilizzato da Google per l'invio di task al C2DM
		URL url = new URL(C2DM_URL);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",	"application/x-www-form-urlencoded;charset=UTF-8");
		conn.setRequestProperty("Content-Length", postData.length+"");
		//Viene indicata l'ultima chiave di autenticazione per il server C2DM fornitaci da Google
		conn.setRequestProperty("Authorization", "GoogleLogin auth="+ C2DMConfigLoader.getInstance().getAuthKey());

		OutputStream out = conn.getOutputStream();
		out.write(postData);
		out.close();

		int responseCode = conn.getResponseCode();


		log.info(String.valueOf(responseCode));

		if (responseCode == 401 || responseCode == 403) {
			log.severe("Unauthorized - need token");
			C2DMConfigLoader.getInstance().invalidateAuthToken();
			return false;
			/*
			 * Un errore di autenticazione potrebbe verificarsi quando, in memoria,
			 * si ha un token obsoleto mentre sul Datastore è presente il valore
			 * corretto. Viene dunque forzata la lettura del token da Datastore
			 * e l'invio deve essere riprovato. 
			 */
		}

		/*
		 * Periodicamente, Google fornisce un nuovo token di autenticazione che ci viene recapitato tramite
		 * l'header "UPDATE_CLIENT_AUTH". L'eventuale nuovo valore viene salvato tramite C2DMConfigLoader;
		 */
		String updatedAuthToken = conn.getHeaderField(UPDATE_CLIENT_AUTH);
		if (updatedAuthToken != null && !C2DMConfigLoader.getInstance().getAuthKey().equals(updatedAuthToken)) {
			log.info("Got updated auth token from datamessaging servers: " + updatedAuthToken);
			C2DMConfigLoader.getInstance().setAuthKey(updatedAuthToken);
		}

		String responseLine = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();

		if (responseLine == null || responseLine.equals("")) {
			log.info("Got " + responseCode	+ " response from Google AC2DM endpoint.");
			throw new IOException("Got empty response from Google AC2DM endpoint.");
		}

		String[] responseParts = responseLine.split("=", 2);
		if (responseParts.length != 2) {
			log.warning("Invalid message from google: " + responseCode	+ " " + responseLine);
			throw new IOException("Invalid response from Google "+ responseCode + " " + responseLine);
		}

		if (responseParts[0].equals("id")){ 
			log.info("Successfully sent data message to device: "+ responseLine);
			return true;
		}

		if (responseParts[0].equals("Error")) {
			String err = responseParts[1];
			log.warning("Got error response from Google datamessaging endpoint: "+ err);
			throw new IOException(err);
		}else {
			/*
			 * Si verifica in seguito ad un errore 500 o a qualche altro errore del server C2DM
			 * Riprovare l'invio potrebbe risolvere il problema
		     */
		      log.warning("Invalid response from google " + responseLine + " " + responseCode);
		      return false;
		    }
	}
	
	/**
	 * Avvia la Task utilizzata per l'invio di notifiche tramite C2DM.
	 * La task verrà riprovata in caso di errori recuperabili
	 * 
	 * @param registrationId
	 *            L'id del device dell'utente che deve ricevere la notifica
	 * @param userId
	 *            L'id dell'utente la cui presenza deve essere segnalata
	 */
	public static void sendWithRetry(String registrationId, long userId){
		if(registrationId != null){
			Queue queue = QueueFactory.getQueue("c2dm");
			TaskOptions url = TaskOptions.Builder.withUrl(C2DMSenderTask.URI)
												.param("registrationId", registrationId)
												.param("userId", userId+"")
												.method(Method.POST);
			/*
		 	* Viene definito a random il tempo tra un retry e un altro.
		 	* I retry sono eventualmente necessari in caso di alcuni
		 	* server error del C2DM per i quali riprovare ad eseguire la task,
		 	* può risolvere il problema.
		 	*/
			long countdownMillis = (int) Math.random() * DATAMESSAGING_MAX_COUNTDOWN_MSEC;
			url.countdownMillis(countdownMillis);
			queue.add(url);	
		}else{
			log.warning("Warning: invalid device registrationId. Message won't be sent.");
		}
		
	}
}

