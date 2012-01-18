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
		testConfig.setStoreDelayMs(10);
		helper = new LocalServiceTestHelper(testConfig);
		super.init(arg0);
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {
		helper.setUp();
		super.doFilter(req, res, chain);
	}

}
