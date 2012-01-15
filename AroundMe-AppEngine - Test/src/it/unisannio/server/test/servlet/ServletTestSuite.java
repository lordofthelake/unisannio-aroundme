package it.unisannio.server.test.servlet;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;
import it.unisannio.aroundme.server.servlet.UserServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.googlecode.objectify.ObjectifyService;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ServletTestSuite {
	protected static int port;
	private static TestSuite testSuite;
	
	
	//Questà à una prova, da  non cancellare//
	public static Test suite(){
//		try {
			testSuite = new TestSuite("ServletTestSuite");
		
			ModelFactory.setInstance(new ServerModelFactory());
			ObjectifyService.register(UserImpl.class);
			ObjectifyService.register(InterestImpl.class);
			ObjectifyService.register(C2DMConfig.class);
//			helper.setUp();
			

			/*
			 * Viene utilizzato Jetty v7.5.4 come Servlet Container
			 * http://www.eclipse.org/jetty/
			 */
			Server server = new Server(8888);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			context.addFilter(new FilterHolder(new ServletFilterForTesting()), "/*", 0);
			context.addServlet(new ServletHolder(new UserServlet()), "/user/*");

			try {
				server.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			port = server.getConnectors()[0].getLocalPort();
			
			
			testSuite.addTestSuite(UserServletTest.class);


//		} catch (Exception e) {
//			e.printStackTrace();
//		}
			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return testSuite;
	}
}
