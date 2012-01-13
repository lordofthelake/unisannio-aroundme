package it.unisannio.aroundme.server.task;

import it.unisannio.aroundme.model.Compatibility;
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

	/**
	 * L'URI utilizzata per poter raggiungere e  quindi eseguire il {@link PositionQueryTask}
	 */
	public static final String URI = "/task/positionquery";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try {
			log.info("Excecuting PositionQuryTask");
			long userId = Long.parseLong(req.getParameter("userId"));
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, userId);
			Position position = user.getPosition();
			Preferences userPreferences = user.getPreferences();
			Neighbourhood neighbourhood = new Neighbourhood(position, userPreferences.get(PREF_QUERY_RADIUS, 100));	
			Compatibility compatibility = new Compatibility(user.getId(), userPreferences.get(PREF_QUERY_RANK, 0.6f));

			UserQuery query = new UserQueryImpl();
			query.setNeighbourhood(neighbourhood);
			query.setCompatibility(compatibility);
			Collection<User> users = query.call();
			for(User u: users){
				if(userPreferences.get("notification.active", true))
					C2DMNotificationSender.sendWithRetry(userPreferences.get(PREF_REGISTRATION_ID, null), u.getId());
				if (isQueriedUserNotificable(u, user));
					C2DMNotificationSender.sendWithRetry(((UserImpl)u).getPreferences().get(PREF_REGISTRATION_ID, null), userId);
				
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
	private boolean isQueriedUserNotificable(User queriedUser, User myUser){
		Preferences queriedUserPrefs = ((UserImpl) queriedUser).getPreferences();
		if(queriedUserPrefs.get("notification.active", true)){
			if(queriedUser.getCompatibilityRank(myUser) <  queriedUserPrefs .get(PREF_QUERY_RANK, 0.6f))
				return false;
			if(queriedUser.getDistance(myUser) < queriedUserPrefs.get(PREF_QUERY_RADIUS, 100))
				return false;
			return true;
		}
		return false;
	}
	
}
