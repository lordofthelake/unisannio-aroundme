package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.server.UserImpl;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class PreferencesServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PreferencesServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String userId = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')+1);
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, Long.parseLong(userId));
			Preferences preferences = Preferences.SERIALIZER.read(req.getInputStream());
			log.info(Preferences.SERIALIZER.toString(preferences));
			user.setPreferences(preferences);
			ofy.put(user);
		}catch (NumberFormatException e){
			resp.sendError(401);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String userId = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')+1);
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, Long.parseLong(userId));
			resp.setContentType("text/xml");
			Preferences.SERIALIZER.write(user.getPreferences(), resp.getOutputStream());
		}catch (NumberFormatException e){
			resp.sendError(401);
		}catch (NullPointerException e) {
			e.printStackTrace();
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}

}
