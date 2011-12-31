package it.unisannio.aroundme.server;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.c2dm.C2DMNotificationSender;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class PositionQueryTask extends HttpServlet{
	private static final long serialVersionUID = 1L;
		
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
			long userId = Long.parseLong(req.getParameter("userId"));
			Objectify ofy = ObjectifyService.begin();
			User user = ofy.get(UserImpl.class, userId);
			Position position = user.getPosition();
			Neighbourhood neighbourhood = new Neighbourhood();		
			neighbourhood.setPosition(position);
			neighbourhood.setRadius(100);
			Compatibility compatibility = new Compatibility(user.getId(), 0.6f);

			UserQuery query = new UserQueryImpl();
			query.setNeighbourhood(neighbourhood);
			query.setCompatibility(compatibility);
			try {
				Collection<? extends User> users = query.call();
				for(User u: users){
					C2DMNotificationSender.sendWithRetry("registrationId di u", userId);
					C2DMNotificationSender.sendWithRetry("registrationId di user", u.getId());
				}
								
			} catch (Exception e) {
				resp.sendError(520);
			}
			
			
			
			
	}
}
