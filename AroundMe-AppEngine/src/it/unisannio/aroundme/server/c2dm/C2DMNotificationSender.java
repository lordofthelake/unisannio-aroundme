package it.unisannio.aroundme.server.c2dm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;


/**
 * Utilizzato per inviare una notifica push ad un device tramite
 * Android Cloud to Device Messaging Framework di Google
 * 
 * @see http://code.google.com/android/c2dm/
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMNotificationSender {
	
	private C2DMConfigLoader configLoader;

	/*
	 * Definizione dei nomi dei parametri da includere nei PostData
	 * necessari per l'utilizzo del C2DM
	 */
	private static final String UPDATE_CLIENT_AUTH = "Update-Client-Auth";
	public static final String PARAM_REGISTRATION_ID = "registration_id";
	public static final String PARAM_DELAY_WHILE_IDLE = "delay_while_idle";
	public static final String PARAM_COLLAPSE_KEY = "collapse_key";
	
	private static final Logger log = Logger.getLogger(C2DMNotificationSender.class.getName());

	/**
	 * Crea un {@link C2DMNotificationSender} che utilizza le impostazioni
	 * gestite da un {@link C2DMConfigLoader}
	 */
	public C2DMNotificationSender(C2DMConfigLoader c2dmConfigLoader){
		this.configLoader = c2dmConfigLoader;
	}

	/**
	 * Invia, tramite C2DM, un messaggio al device indicato dal registrationId, notificando la presenza,
	 * nella sua zona, di un utente con userId specificato
	 * 
	 * @param registrationId L'id del device dell'utente che deve ricevere la notifica
	 * @param userId L'id dell'utente la cui presenza deve essere segnalata
	 * 
	 * @throws IOException Se si sono verificati errori durante l'invio del messaggio
	 */
	public void sendNotification(String registrationId, long userId) throws IOException {
		//StringBuilder utilizzato per la creazione dei PostData
		StringBuilder postDataBuilder = new StringBuilder();
		
		//Aggiunta del parametro specificante il registrationId
		postDataBuilder.append(PARAM_REGISTRATION_ID +"="+registrationId);

		postDataBuilder.append("&" + PARAM_COLLAPSE_KEY + "=" + 0);

		//Aggiunta del parametro che specifica l'userId dell'utente di cui notificare
		postDataBuilder.append("&data.userId="+userId);

		byte[] postData = postDataBuilder.toString().getBytes("UTF-8");

		//Url utilizzato da Google per l'invio di task al C2DM
		URL url = new URL(configLoader.getC2dmUrl());

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",	"application/x-www-form-urlencoded;charset=UTF-8");
		conn.setRequestProperty("Content-Length", postData.length+"");
		//Viene indicata la chiave di registrazione al C2DM fornitaci da Google
		conn.setRequestProperty("Authorization", "GoogleLogin auth="+ configLoader.getAuthKey());

		OutputStream out = conn.getOutputStream();
		out.write(postData);
		out.close();

		int responseCode = conn.getResponseCode();


		log.info(String.valueOf(responseCode));

		if (responseCode == 401 || responseCode == 403) {
			log.severe("Unauthorized - need token");
			throw new IOException("Unauthorized token");
		}

		/*
		 * Periodicamente, Google fornisce un nuovo token di autenticazione che ci viene recapitato tramite
		 * l'header "UPDATE_CLIENT_AUTH". L'eventuale nuovo valore viene salvato tramite configLoader;
		 */
		String updatedAuthToken = conn.getHeaderField(UPDATE_CLIENT_AUTH);
		if (updatedAuthToken != null && !configLoader.getAuthKey().equals(updatedAuthToken)) {
			log.info("Got updated auth token from datamessaging servers: "
					+ updatedAuthToken);
			configLoader.setAuthKey(updatedAuthToken);
		}

		/*
		 * La parte seguente è utile solo ai fini del controllo della risposta da 
		 * parte del server C2DM e ai fini della generazione del log
		 */
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

		if (responseParts[0].equals("id")) 
			log.info("Successfully sent data message to device: "+ responseLine);

		if (responseParts[0].equals("Error")) {
			String err = responseParts[1];
			log.warning("Got error response from Google datamessaging endpoint: "+ err);
			throw new IOException(err);
		}
		
	}
}

