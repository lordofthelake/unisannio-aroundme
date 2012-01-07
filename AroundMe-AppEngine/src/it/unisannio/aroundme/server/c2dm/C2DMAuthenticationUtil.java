package it.unisannio.aroundme.server.c2dm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility utilizzata per richiedere il token di registrazione per l'utilizzo
 * del Server Cloud To Device Messaging Framework.
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMAuthenticationUtil {
	
	private static final String EMAIL = "aroundmeproject@gmail.com";
	private static final String PASSWORD = "AroundMe!";
	private static final String GOOGLE_CLIENTLOGIN_URL = "https://www.google.com/accounts/ClientLogin";
	private static final String PROJECT_NAME = "unisannio-AroundMeProject";
	
	private C2DMAuthenticationUtil(){}
	
	/**
	 * Ottiene il token di registrazione per il server C2DM contattando
	 * il server ClientLogin di Google
	 * 
	 * @return Una String contentente il token di registrazione
	 * @throws IOException
	 */
	public static String getToken()	throws IOException {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Email=").append(EMAIL);
		builder.append("&Passwd=").append(PASSWORD);
		builder.append("&accountType=GOOGLE");
		builder.append("&source=").append(PROJECT_NAME);
		builder.append("&service=ac2dm");

		byte[] data = builder.toString().getBytes();
		URL url = new URL(GOOGLE_CLIENTLOGIN_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Length", data.length+"");

		OutputStream output = con.getOutputStream();
		output.write(data);
		output.close();

		// Lettura della risposta
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = null;
		String auth_key = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("Auth=")) {
				auth_key = line.substring(5);
			}
		}

		return auth_key;
	}
}