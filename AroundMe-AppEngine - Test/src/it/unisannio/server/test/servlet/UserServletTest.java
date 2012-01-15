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

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;
import it.unisannio.aroundme.server.servlet.UserServlet;
import junit.extensions.TestSetup;
import junit.framework.*;

import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;

/**
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserServletTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private int port = 8888; //Togliere l'assegnazione per usare Jetty
	Server server;

	public static Test suite() {
		return new TestSetup(new TestSuite(UserServletTest.class)) {

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
			/* Decommentare per usare Jetty
			server = new Server(0);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			context.addFilter(new FilterHolder(new ServletFilterForTesting()), "/*", 0);
			context.addServlet(new ServletHolder(new UserServlet()), "/user/*");

			server.start();
			port = server.getConnectors()[0].getLocalPort();
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tearDown(){
		try {
			helper.tearDown();
//			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// PUT TESTING//

	/**
	 * Testa che una richiesta PUT ben formata, restituisca una
	 * risposta con statuscode 200
	 */
	public void testPutSuccess() {
		User user = ModelFactory.getInstance().createUser(123, "Danilo", null);
		String accessToken = "abc";
		int responseStatusCode = doPut(user, accessToken);
		assertEquals(200, responseStatusCode);
	}

	/**
	 * Esegue una richiesta put all'UserServlet per la crezione di un User con un
	 * dato X-AccessToken. Restituisce lo status code della risposta.
	 * @param user l'User da persistere sul server
	 * @param accessToken l'X-AccessToken di facebook
	 * @return Lo status code della risposta.
	 */
	public int doPut(User user, String accessToken){
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

	/**
	 * Testa che una richiesta PUT mal formata, restituisca una
	 * risposta con statuscode 500
	 */
	public void testPutFail(){
		try {

			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", "abc");
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
			User user = ModelFactory.getInstance().createUser(123, "Danilo", null);
			if(doPut(user, "abc")==200){
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/"+user.getId()).openConnection();
				conn.setRequestProperty("X-AccessToken", "abc");
				conn.setUseCaches(false);
				conn.setRequestMethod("DELETE");

				int statusCode = conn.getResponseCode();
				assertEquals(200, statusCode);
			}
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
			User user = ModelFactory.getInstance().createUser(123, "Danilo", null);
			if(doPut(user, "abc")==200){
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/abecedario").openConnection();
				conn.setRequestProperty("X-AccessToken", "abc");
				conn.setUseCaches(false);
				conn.setRequestMethod("DELETE");
				int statusCode = conn.getResponseCode();
				assertEquals(401, statusCode);
			}
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
			User user = ModelFactory.getInstance().createUser(123, "Danilo", null);
			if(doPut(user, "abc")==200){
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/345").openConnection();
				conn.setRequestProperty("X-AccessToken", "abc");
				conn.setUseCaches(false);
				conn.setRequestMethod("DELETE");
				int statusCode = conn.getResponseCode();
				assertEquals(404, statusCode);
			}
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
			User user = ModelFactory.getInstance().createUser(123, "Danilo", null);
			if(doPut(user, "abc")==200){
				UserQuery query = ModelFactory.getInstance().createUserQuery();
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
				conn.setRequestProperty("X-AccessToken", "abc");
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
			}


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
			User user = ModelFactory.getInstance().createUser(123, "Danilo", null);
			if(doPut(user, "abc")==200){
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
				conn.setRequestProperty("X-AccessToken", "abc");
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.getOutputStream().close();
				
				//Non viene inserito nel body l'xml che descrive la query da eseguire
				
				assertEquals(500, conn.getResponseCode());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	

}
