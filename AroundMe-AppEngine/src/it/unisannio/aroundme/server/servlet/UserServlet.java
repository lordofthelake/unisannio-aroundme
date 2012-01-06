package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.UserImpl;

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
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(UserServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp)	throws ServletException, IOException {
		try {
			UserQuery query = UserQuery.SERIALIZER.read(req.getInputStream());
			log.info(UserQuery.SERIALIZER.toString(query));
			Collection<User> users = query.call();
			resp.setContentType("text/xml");
			Serializer.ofCollection(User.class).write(users, resp.getOutputStream());
		} catch (Exception e) {
			log.severe(e.getMessage());
			resp.sendError(500);
		}				
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			UserImpl user = (UserImpl) User.SERIALIZER.read(req.getInputStream());
			log.info(User.SERIALIZER.toString(user));
			Objectify ofy = ObjectifyService.begin();
			user.setAuthToken(req.getHeader("X-AccessToken"));
			ofy.put(user);
		} catch (Exception e) {
			log.severe(e.getMessage());
			resp.sendError(500);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			String userId = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')+1);
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, Long.parseLong(userId));
			ofy.delete(user);
		}catch (NumberFormatException e){
			resp.sendError(401);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.getMessage());
			resp.sendError(500);
		}
	}
}

