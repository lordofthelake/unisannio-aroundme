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
package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;


/**
 * {@link ServletContextListener} utilizzato per definire, in fase di caricamento del {@link ServletContext},
 * le impostazioni necessarie per l'utilizzo del Datastore.
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class ServletContextListenerImpl implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// Registro le classi User, Interest e C2DMConfig come entity per il Datastore
		ObjectifyService.register(UserImpl.class);
		ObjectifyService.register(InterestImpl.class);
		ObjectifyService.register(C2DMConfig.class);
		
		// Imposto il ModelFactory in modo tale da produrre oggetti con l'implementazione lato server
		ModelFactory.setInstance(new ServerModelFactory());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {}

}
