package it.unisannio.server.test.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.TransformerException;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;
import it.unisannio.aroundme.server.servlet.ServletFilter;
import it.unisannio.aroundme.server.servlet.UserServlet;
import junit.framework.TestCase;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

/**
 * {@link TestCase} Che verifica che il filter {@link ServletFilter} neghi o consenta
 * l'accesso alle servlet in base alla richiesta
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class ServletFilterTest extends TestCase{
	private LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig().setNoStorage(false).setBackingStoreLocation("local_db.ini").setStoreDelayMs(10));
	private Server server;
	private int port;
	private String fbAccessToken;

	@Override
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
			context.addFilter(new FilterHolder(new ServletFilter()), "/*", 0);
			context.addServlet(new ServletHolder(new MockServlet()), "/*");

			server.start();
			port = server.getConnectors()[0].getLocalPort();

			//User utilizzato col solo scopo di ottenere un X-AccessToken valido per effettuare le richieste
			UserImpl user1 = (UserImpl) ModelFactory.getInstance().createUser(12345, "Michele Piccirillo", null);
			fbAccessToken = "accessToken";
			user1.setAuthToken(fbAccessToken);	
			ObjectifyService.begin().put(user1);

			Thread.sleep(15); //Ulizzato per assicurare che il tempo necessario per la persistenza sul Datatore sia passato

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		helper.tearDown();
		server.stop();
	}
	
	/**
	 * Testa che il filtro non blocchi le richieste verso /task/*
	 */
	public void testAccessGainedTask(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/task/mocktask").openConnection();
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			assertEquals(200, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	//PUT//
	
	/**
	 * Testa che il filtro non blocchi le richieste PUT verso /user/*
	 * che contengono tra gli header un X-AccessToken 
	 */
	public void testAccessGainedPutUser(){
		try {
			User user = ModelFactory.getInstance().createUser(123, "Danilo Iannelli", null);
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setRequestProperty("X-AccessToken", "anAccessToken");
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
	 * Testa che il filtro blocchi le richieste PUT verso /user/*
	 * che NON contengono tra gli header un X-AccessToken 
	 */
	public void testAccessDeniedPutUser(){
		try {
			User user = ModelFactory.getInstance().createUser(123, "Danilo Iannelli", null);
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/user/").openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			OutputStream out = conn.getOutputStream();
			User.SERIALIZER.write(user, out);
			out.flush();
			out.close();
			assertEquals(403, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Testa che il filtro non blocchi le richieste PUT verso una servlet
	 * qualsiasi (ma diversa da {@link UserServlet} mappata su /user/* 
	 * e dalle task mappate su /task/*) che contengono tra gli header
	 * un X-AccessToken registrato sul Datastore 
	 */
	public void testAccessGainedPut(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setRequestMethod("PUT");
		
			assertEquals(200, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Testa che il filtro blocchi le richieste PUT verso una servlet
	 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
	 * tra gli header un X-AccessToken
	 */
	public void testAccessDeniedPutNoAccessToken(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
			conn.setUseCaches(false);
			conn.setRequestMethod("PUT");

			assertEquals(403, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Testa che il filtro blocchi le richieste PUT verso una servlet
	 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
	 * tra gli header un X-AccessToken che sia registrato sul Datastore 
	 */
	public void testAccessDeniedPutInvalidAccessToken(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet").openConnection();
			conn.setRequestProperty("X-AccessToken", "aninvalidtoken");
			conn.setUseCaches(false);
			conn.setRequestMethod("PUT");
		
			assertEquals(403, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	//POST//
	/**
	 * Testa che il filtro non blocchi le richieste POST verso una servlet
	 * qualsiasi  che contengono tra gli header un X-AccessToken registrato sul Datastore 
	 */
	public void testAccessGainedPost(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
			conn.setRequestProperty("X-AccessToken", fbAccessToken);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
		
			assertEquals(200, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Testa che il filtro blocchi le richieste POST verso una servlet
	 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
	 * tra gli header un X-AccessToken
	 */
	public void testAccessDeniedPostNoAccessToken(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");

			assertEquals(403, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Testa che il filtro blocchi le richieste POST verso una servlet
	 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
	 * tra gli header un X-AccessToken che sia registrato sul Datastore 
	 */
	public void testAccessDeniedPostInvalidAccessToken(){
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet").openConnection();
			conn.setRequestProperty("X-AccessToken", "aninvalidtoken");
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
		
			assertEquals(403, conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	//GET//
		/**
		 * Testa che il filtro non blocchi le richieste GET verso una servlet
		 * qualsiasi  che contengono tra gli header un X-AccessToken registrato sul Datastore 
		 */
		public void testAccessGainedGet(){
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
				conn.setRequestProperty("X-AccessToken", fbAccessToken);
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
			
				assertEquals(200, conn.getResponseCode());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		/**
		 * Testa che il filtro blocchi le richieste GET verso una servlet
		 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
		 * tra gli header un X-AccessToken
		 */
		public void testAccessDeniedGetNoAccessToken(){
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");

				assertEquals(403, conn.getResponseCode());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		/**
		 * Testa che il filtro blocchi le richieste GET verso una servlet
		 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
		 * tra gli header un X-AccessToken che sia registrato sul Datastore 
		 */
		public void testAccessDeniedGetInvalidAccessToken(){
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet").openConnection();
				conn.setRequestProperty("X-AccessToken", "aninvalidtoken");
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
			
				assertEquals(403, conn.getResponseCode());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		
		//DELETE//
		/**
		 * Testa che il filtro non blocchi le richieste DELETE verso una servlet
		 * qualsiasi  che contengono tra gli header un X-AccessToken registrato sul Datastore 
		 */
		public void testAccessGainedDelete(){
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
				conn.setRequestProperty("X-AccessToken", fbAccessToken);
				conn.setUseCaches(false);
				conn.setRequestMethod("DELETE");
			
				assertEquals(200, conn.getResponseCode());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		/**
		 * Testa che il filtro blocchi le richieste DELETE verso una servlet
		 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
		 * tra gli header un X-AccessToken
		 */
		public void testAccessDeniedDeleteNoAccessToken(){
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet/").openConnection();
				conn.setRequestMethod("DELETE");

				assertEquals(403, conn.getResponseCode());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		/**
		 * Testa che il filtro blocchi le richieste DELETE verso una servlet
		 * qualsiasi (ma diversa dalle task mappate su /task/*) che NON contengono
		 * tra gli header un X-AccessToken che sia registrato sul Datastore 
		 */
		public void testAccessDeniedDeleteInvalidAccessToken(){
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("HTTP","127.0.0.1", port , "/aServlet").openConnection();
				conn.setRequestProperty("X-AccessToken", "aninvalidtoken");
				conn.setUseCaches(false);
				conn.setRequestMethod("DELETE");
			
				assertEquals(403, conn.getResponseCode());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}



}
