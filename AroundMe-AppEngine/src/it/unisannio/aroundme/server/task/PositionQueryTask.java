package it.unisannio.aroundme.server.task;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
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

	private final String REGISTRATION_ID = "c2dmRegistrationId";

	/**
	 * L'URI utilizzata per poter raggiungere e  quindi eseguire il {@link PositionQueryTask}
	 */
	public static final String URI = "/task/positionquery";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try {
			long userId = Long.parseLong(req.getParameter("userId"));
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, userId);
			Position position = user.getPosition();
			Neighbourhood neighbourhood = new Neighbourhood(position, 100);	
			Compatibility compatibility = new Compatibility(user.getId(), 0.6f);

			UserQuery query = new UserQueryImpl();
			query.setNeighbourhood(neighbourhood);
			query.setCompatibility(compatibility);

			Collection<User> users = query.call();
			for(User u: users){
				C2DMNotificationSender.sendWithRetry(((UserImpl)u).getPreferences().get(REGISTRATION_ID, null), userId);
				C2DMNotificationSender.sendWithRetry(user.getPreferences().get(REGISTRATION_ID, null), u.getId());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			resp.sendError(520);
		}




	}
}
