package it.unisannio.server.test.servlet;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.googlecode.objectify.ObjectifyService;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PositionServletTest extends TestCase {
	private int port = 8888;


	public static Test suite() {
		return new TestSetup(new TestSuite(PositionServletTest.class)) {

			protected void setUp() throws Exception {
				ModelFactory.setInstance(new ServerModelFactory());
				ObjectifyService.register(UserImpl.class);
				ObjectifyService.register(InterestImpl.class);
				ObjectifyService.register(C2DMConfig.class);
			}
		};
	}
	
	//FIXME Bruttezza a palate
	/** 
	 * Esegue una richiesta put all'UserServlet per la crezione di un User con un
	 * dato X-AccessToken. Restituisce lo status code della risposta.
	 * @param user l'User da persistere sul server
	 * @param accessToken l'X-AccessToken di facebook
	 * @return Lo status code della risposta.
	 */
	public int doPutUser(User user, String accessToken){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", accessToken);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			OutputStream out = conn.getOutputStream();
			User.SERIALIZER.write(user, out);
			out.flush();
			out.close();
			return conn.getResponseCode();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	protected void setUp() throws Exception {
		User user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user2 = ModelFactory.getInstance().createUser(125, "Michele", null);
		doPutUser(user1, "abc");
		doPutUser(user2, "def");
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
			conn.setRequestProperty("X-AccessToken", "abc");
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
			conn.setRequestProperty("X-AccessToken", "abc");
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
			conn.setRequestProperty("X-AccessToken", "abc");
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
			conn.setRequestProperty("X-AccessToken", "abc");
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
			testPostSuccess(); //FIXME Non è bello.
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/123").openConnection();
			conn.setRequestProperty("X-AccessToken", "abc");
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
			testPostSuccess(); 
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/1f2df3").openConnection();
			conn.setRequestProperty("X-AccessToken", "abc");
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
			testPostSuccess(); 
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/6578").openConnection();
			conn.setRequestProperty("X-AccessToken", "abc");
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
		User user2 = ModelFactory.getInstance().createUser(125, "Marco", null);
		doPutUser(user2, "desf");

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/position/125").openConnection();
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
