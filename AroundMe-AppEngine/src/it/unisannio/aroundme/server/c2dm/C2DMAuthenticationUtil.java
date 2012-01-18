/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.server.c2dm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility utilizzata per richiedere, contatando il server ClientLogin di Google,
 * il token di registrazione per l'utilizzo del server Cloud To Device Messaging Framework.
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
	 * @throws IOException se occore un errore di I/O
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