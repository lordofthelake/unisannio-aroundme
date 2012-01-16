package it.unisannio.server.test.servlet;


import it.unisannio.aroundme.server.servlet.ServletFilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * {@link Filter} che estende {@link ServletFilter} aggiungendo
 * alcune configurazioni necessarie per il testing locale dell'Appengine
 * quali l'accesso al Datastore locale e alla TaskQueue Locale
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class FilterForTesting extends ServletFilter {
	LocalServiceTestHelper helper;
	boolean clicked = false;
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		LocalDatastoreServiceTestConfig testConfig = new LocalDatastoreServiceTestConfig();
		testConfig.setNoStorage(false);
		testConfig.setBackingStoreLocation("local_db.ini");
		helper = new LocalServiceTestHelper(testConfig);
		super.init(arg0);
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {
		helper.setUp();
		super.doFilter(req, res, chain);
	}

}
