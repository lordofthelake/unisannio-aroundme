package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.task.PositionQueryTask;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class PositionServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PositionServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			String userId = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')+1);
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, Long.parseLong(userId));
			Position position = Position.SERIALIZER.read(req.getInputStream());
			log.info(Position.SERIALIZER.toString(position));
			user.setPosition(position);
			ofy.put(user);
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions url = TaskOptions.Builder.withUrl(PositionQueryTask.URI)
					.param("userId", userId)
					.method(Method.POST);
			queue.add(url);
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try {
			String userId = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')+1);
			Objectify ofy = ObjectifyService.begin();
			UserImpl user = ofy.get(UserImpl.class, Long.parseLong(userId));
			resp.setContentType("text/xml");
			Position.SERIALIZER.write(user.getPosition(), resp.getOutputStream());
		}catch (NumberFormatException e){
			resp.sendError(401);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
}
