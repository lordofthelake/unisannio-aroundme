package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.UserImpl;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;



/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp)	throws ServletException, IOException {
		try {
			UserQuery query = UserQuery.SERIALIZER.read(req.getInputStream());
			Collection<? extends User> users = query.call();
			resp.setContentType("text/xml");
			Serializer.ofCollection(User.class).write(users, resp.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(500);
		}				
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			UserImpl user = (UserImpl) User.SERIALIZER.read(req.getInputStream());
			Objectify ofy = ObjectifyService.begin();
			user.setAuthToken(req.getHeader("X-AccessToken"));
			ofy.put(user);
		} catch (SAXException e) {
			e.printStackTrace();
			resp.sendError(500);
		}
	}
}

