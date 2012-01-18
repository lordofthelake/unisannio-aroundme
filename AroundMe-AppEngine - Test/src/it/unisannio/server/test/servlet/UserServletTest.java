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
package it.unisannio.server.test.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;

import javax.xml.transform.TransformerException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import junit.framework.TestCase;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;
import it.unisannio.aroundme.server.servlet.UserServlet;

import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;

/**
 * {@link TestCase} che verifica che la servlet {@link UserServlet} restituisca risposte
 * coerenti alle richiste che vi vengono indirizzate
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserServletTest extends TestCase{
	private LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig().setNoStorage(false).setBackingStoreLocation("local_db.ini").setStoreDelayMs(10));
	private int port;
	private Server server;
	private String fbAccessToken;
	private User userToDelete, unsavedUser;

	public void setUp() {
		try {
			helper.setUp();

			ModelFactory.setInstance(new ServerModelFactory());
			try{
				ObjectifyService.register(UserImpl.class);
				ObjectifyService.register(InterestImpl.class);
				ObjectifyService.register(C2DMConfig.class);
			}catch(IllegalArgumentException e){}
			
			
			/*
			 * Viene utilizzato Jetty v7.5.4 come Servlet Container
			 * http://www.eclipse.org/jetty/
			 */
			server = new Server(0); 
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			context.addFilter(new FilterHolder(new FilterForTesting()), "/*", 0);
			context.addServlet(new ServletHolder(new UserServlet()), "/user/*");

			server.start();
			port = server.getConnectors()[0].getLocalPort();

			//User utilizzato col solo scopo di ottenere un X-AccessToken valido per effettuare le richieste 
			UserImpl user1 = (UserImpl) ModelFactory.getInstance().createUser(12345, "Michele Piccirillo", null);
			fbAccessToken = "accessToken";
			user1.setAuthToken(fbAccessToken);
			
			//User presente sul datastore utilizzato per testare il doDelete
			userToDelete = ModelFactory.getInstance().createUser(444, "Giuseppe Fusco", null);
			ObjectifyService.begin().put(user1, userToDelete);

			//User non presente sul datastore utilizzato per testare il 404 del doDelete
			unsavedUser = ModelFactory.getInstance().createUser(567, "Giovanni", null);
			
			Thread.sleep(15); //Ulizzato per assicurare che il tempo necessario per la persistenza sul Datatore sia passato
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tearDown() throws Exception {
		helper.tearDown();
		server.stop();
	}

	// PUT TESTING //

	/**
	 *	Testa che una richiesta PUT ben formata, restituisca una
	 * isposta con statuscode 200
	 */
	public void testPutSuccess(){
		try {
			User user = ModelFactory.getInstance().createUser(123, "Danilo Iannelli", null);
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", "userAccessToken");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			OutputStream out = conn.getOutputStream();
			User.SERIALIZER.write(user, out);
			out.flush();
			out.close();
			assertEquals(200, conn.getResponseCode());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa che una richiesta PUT mal formata, restituisca una
	 * risposta con statuscode 500
	 */
	public void testPutFail(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.getOutputStream().close();

			//Non viene inserito nel body l'xml che descrive l'utente da creare

			int statusCode = conn.getResponseCode();
			assertEquals(500, statusCode);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	
	// DELETE  TESTING//

	/**
	 * Testa che una richiesta DELETE ben formata, restituisca una
	 * risposta con statuscode 200
	 */
	public void testDeleteSuccess(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/"+userToDelete.getId()).openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setRequestMethod("DELETE");

			int statusCode = conn.getResponseCode();
			assertEquals(200, statusCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa che una richiesta DELETE con id non valido, restituisca una
	 * risposta con statuscode 401
	 */
	public void testDeleteInvalidId(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/abecedario").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setRequestMethod("DELETE");
			int statusCode = conn.getResponseCode();
			assertEquals(401, statusCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa che una richiesta DELETE con id di un utente non trovato, restituisca una
	 * risposta con statuscode 404
	 */
	public void testDeleteNotFound(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/"+unsavedUser.getId()).openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setRequestMethod("DELETE");
			int statusCode = conn.getResponseCode();
			assertEquals(404, statusCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// POST TESTING//

	/**
	 * Testa che una richiesta POST ben formata, restituisca una
	 * risposta con statuscode 200 e, nel body, una collezione di User serializzata
	 */
	public void testPostSuccess(){
		try {
			UserQuery query = ModelFactory.getInstance().createUserQuery();
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			UserQuery.SERIALIZER.write(query, out);
			out.flush();
			out.close();
			assertEquals(200, conn.getResponseCode());

			Exception ex = null;
			try {
				Serializer.ofCollection(User.class).read(conn.getInputStream());
			} catch (Exception e) {
				ex = e;
			}
			assertNull(ex);

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Testa che una richiesta POST mal formata, restituisca una
	 * risposta con statuscode 500
	 */
	public void testPostFail(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.getOutputStream().close();

			//Non viene inserito nel body l'xml che descrive la query da eseguire

			assertEquals(500, conn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
