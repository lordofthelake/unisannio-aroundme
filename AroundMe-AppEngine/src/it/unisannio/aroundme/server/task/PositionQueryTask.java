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
package it.unisannio.aroundme.server.task;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.UserQueryImpl;
import it.unisannio.aroundme.server.c2dm.C2DMNotificationSender;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Task per la Google TaskQueue che viene utilizata per effettuare la query che filtra gli utenti in base
 * a posizione e compatibilit&agrave. Gli {@link User} restituiti dalla query, vengono notificati tramite
 * {@link C2DMNotificationSender}.
 *  
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class PositionQueryTask extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PositionQueryTask.class.getName());

	private final String PREF_REGISTRATION_ID = "c2dmRegistrationId";
	private final String PREF_QUERY_RADIUS = "query.radius";
	private final String PREF_QUERY_RANK = "query.rank";
	private final String PREF_NOTIFICATION = "notification.active";
	
	/**
	 * L'URI utilizzata per poter raggiungere e  quindi eseguire il {@link PositionQueryTask}
	 */
	public static final String URI = "/task/positionquery";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try {
			log.info("Excecuting PositionQueryTask");
			long userId = Long.parseLong(req.getParameter("userId"));
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, userId);
			double lat = Double.parseDouble(req.getParameter("lat"));
			double lon = Double.parseDouble(req.getParameter("lon"));
			Position position = ModelFactory.getInstance().createPosition(lat, lon);
			Preferences userPreferences = user.getPreferences();
			if(userPreferences == null) //Vengono ceate nuove preference vuote per fare in modo che vengano usati i valori di Default
				userPreferences = ModelFactory.getInstance().createPreferences(); 
			Neighbourhood neighbourhood = new Neighbourhood(position, userPreferences.get(PREF_QUERY_RADIUS, 500));	
			Compatibility compatibility = new Compatibility(user.getId(), userPreferences.get(PREF_QUERY_RANK, 0.6f));

			UserQuery query = new UserQueryImpl();
			query.setNeighbourhood(neighbourhood);
			query.setCompatibility(compatibility);
			Collection<User> users = query.call();
			for(User u: users){
				if(userPreferences.get(PREF_NOTIFICATION, true))
					C2DMNotificationSender.sendWithRetry(userPreferences.get(PREF_REGISTRATION_ID, null), u.getId());
				if (isQueriedUserNotifiable(u, user)){
					C2DMNotificationSender.sendWithRetry(((UserImpl)u).getPreferences().get(PREF_REGISTRATION_ID, null), userId);
				}
			}

		} catch (Exception e) {
			resp.setStatus(200); //Ritornare un 200 serve per non forzare il retry del task
			log.severe(e.toString());
			resp.getOutputStream().write(("Non-retriable error:" + e.toString()).getBytes());
		}

	}

	/**
	 * Controlla che un utente risultante dalla query, sia notificabile.
	 * Viene controllato se il queried user abbia disattivato le notifiche
	 * e se risulta notificabile in base alle proprie preferenze
	 * 
	 * @param queriedUser L'utente, ottenuto dalla query, del quale si vuole
	 * verificare la "notificabilit&agrave;"
	 * @param myUser l'utente per cui &egrave; stata effettuata la query
	 * @return true se queriedUser &egrave; notificabile
	 */
	private boolean isQueriedUserNotifiable(User queriedUser, User myUser){
		Preferences queriedUserPrefs = ((UserImpl) queriedUser).getPreferences();
		if(queriedUserPrefs != null && queriedUserPrefs.get(PREF_NOTIFICATION, true)){
			if(queriedUser.getCompatibilityRank(myUser) <  queriedUserPrefs .get(PREF_QUERY_RANK, 0.6f))
				return false;
			if(queriedUser.getDistance(myUser) > queriedUserPrefs.get(PREF_QUERY_RADIUS, 500))
				return false;
			return true;
		}
		return false;
	}

}
