package it.unisannio.server.test.servlet;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;
import it.unisannio.aroundme.server.servlet.PositionServlet;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * {@link TestCase} che verifica che la servlet {@link PositionServlet} restituisca risposte
 * coerenti alle richiste che vi vengono indirizzate
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class PositionServletTest extends TestCase {
	private LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig().setNoStorage(false).setBackingStoreLocation("local_db.ini"));
	private int port;
	private Server server;
	private String fbAccessToken;
	private User userNullPosition, userNotNullPosition, user, unsavedUser;

	public static Test suite() {//Pattern per eseguire delle configurazioni una tantum
		return new TestSetup(new TestSuite(PreferencesServletTest.class)) {
			
			protected void setUp() throws Exception {
				ModelFactory.setInstance(new ServerModelFactory());
				ObjectifyService.register(UserImpl.class);
				ObjectifyService.register(InterestImpl.class);
				ObjectifyService.register(C2DMConfig.class);
			}
		};
	}
	
	@Override
	public void setUp() {
		try {
			helper.setUp();

			/*
			 * Viene utilizzato Jetty v7.5.4 come Servlet Container
			 * http://www.eclipse.org/jetty/
			 */
			server = new Server(0); 
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			context.addFilter(new FilterHolder(new FilterForTesting()), "/*", 0);
			context.addServlet(new ServletHolder(new PositionServlet()), "/preferences/*");

			server.start();
			port = server.getConnectors()[0].getLocalPort();

			//User utilizzato col solo scopo di ottenere un X-AccessToken valido per effettuare le richieste
			UserImpl user1 = (UserImpl) ModelFactory.getInstance().createUser(12345, "Michele Piccirillo", null);
			fbAccessToken = "accessToken";
			user1.setAuthToken(fbAccessToken);
			//User al quale verrà aggiunta un oggetto Position
			user = ModelFactory.getInstance().createUser(123, "Danilo Iannelli", null);
			//User con Position null utilizzato per testare il 404 del doGet
			userNullPosition = ModelFactory.getInstance().createUser(125, "Marco Magnetti", null);
			//User con Preferences non nulle utilizzato per testare il doGet
			userNotNullPosition = ModelFactory.getInstance().createUser(126, "Giuseppe Fusco", null);
			Preferences preferences = ModelFactory.getInstance().createPreferences();
			preferences.put("Key", "value");
			((UserImpl)userNotNullPosition).setPreferences(preferences); 
			ObjectifyService.begin().put(user1, user , userNullPosition, userNotNullPosition);
			
			//User non presente sul datastore utilizzato per testare il 404 del doGet
			unsavedUser = ModelFactory.getInstance().createUser(567, "Giovanni", null);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		helper.tearDown();
		server.stop();
	}
	
	// POST TESTING//

	/**
	 * Testa che una richiesta POST ben formata, restituisca una
	 * risposta con statuscode 200
	 */
	public void testPostSuccess(){
		try {
			Position position = ModelFactory.getInstance().createPosition(14.4, 41.5);
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/123").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			Position.SERIALIZER.write(position, out);
			out.flush();
			out.close();
			assertEquals(200, conn.getResponseCode());
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
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/123").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.getOutputStream().close();

			//Non viene inserito nel body l'xml che descrive la position da impostare

			assertEquals(500, conn.getResponseCode());

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Testa che una richiesta POST con user id non valido, restituisca una
	 * risposta con statuscode 401
	 */
	public void testPostInvalidId(){
		try {
			Position position = ModelFactory.getInstance().createPosition(14.4, 41.5);
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/1f2a3").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			Position.SERIALIZER.write(position, out);
			out.flush();
			out.close();
			assertEquals(401, conn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	/**
	 * Testa che una richiesta POST con id di un utente non presente
	 * sul Datastore, abbia risposta con statuscode 404
	 */
	public void testPostUserNotFound(){
		try {
			Position position = ModelFactory.getInstance().createPosition(14.4, 41.5);
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/987123").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			Position.SERIALIZER.write(position, out);
			out.flush();
			out.close();
			assertEquals(404, conn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	// GET TESTING//


	/**
	 * Testa che una richiesta GET ben formata, restituisca una
	 * risposta con statuscode 200 e, nel body, un Position serializzato
	 */
	public void testGetSuccess(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/"+user.getId()).openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(false);
			conn.setRequestMethod("GET");

			assertEquals(200, conn.getResponseCode());

			Exception ex = null;
			try {
				Position.SERIALIZER.read(conn.getInputStream());
			} catch (Exception e) {
				ex = e;
			}
			assertNull(ex);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa che una richiesta GET con user id non valido, restituisca una
	 * risposta con statuscode 401
	 */
	public void testGetInvalidId(){

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/1f2df3").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(false);
			conn.setRequestMethod("GET");

			assertEquals(401, conn.getResponseCode());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa che una richiesta GET con user id di un User non trovato,
	 * restituisca una risposta con statuscode 404
	 */
	public void testGetUserNotFound(){

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/"+unsavedUser.getId()).openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(false);
			conn.setRequestMethod("GET");

			assertEquals(404, conn.getResponseCode());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa che una richiesta GET con userid di un User non con Position <code>null</code>,
	 * restituisca una risposta con statuscode 404
	 */
	public void testGetPositionNotFound(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/"+userNullPosition.getId()).openConnection();
			conn.setRequestProperty("X-AccessToken", "desf");
			conn.setUseCaches(false);
			conn.setDoOutput(false);
			conn.setRequestMethod("GET");

			assertEquals(404, conn.getResponseCode());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
