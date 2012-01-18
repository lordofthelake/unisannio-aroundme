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

import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
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
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Servlet utilizzata per la gestione delle posizioni degli {@link User}
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class PositionServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PositionServlet.class.getName());

	/**
	 * Utilizzato per attribuire una {@link Position} ad un User. Viene inoltre
	 * avviata la Task che notifica la presenza utenti compatibili nelle
	 * vicinanze <br>
	 * <br>
	 * @param req laichiesta:<br>
	 * L'URI deve essere /user/[userId] dove [userId]<br>
	 * &egrave l'id dell'Utente del quale si aggiornare la posizione<br>
	 * Il body deve contentere l'xml che rappresenta la posizione da attribuire
	 * all'utente<br><br>
	 * @param resp la risposta:<br>
	 * 200 - Se la psozione &egrave stata agiornata e la task "PositionQueryTask" &egrave
	 * stata avviata correttanente<br>
	 * 401 - Se l'userId non &egrave valido<br>
	 * 404 - Se l'utente non esiste<br>
	 * 500 - In caso di errori <br>
	 */
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
					.param("lat", position.getLatitude()+"")
					.param("lon", position.getLongitude()+"")
					.method(Method.POST);
			queue.add(url);
		}catch (NumberFormatException e){
			resp.sendError(401);
		}catch(NotFoundException e){
			resp.sendError(404);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}

	}

	/**
	 * Usato per ottenere la posizione di un {@link User}<br>
	 * <br>
	 * @param req la richiesta:<br>
	 * L'URI deve essere /user/[userId]<br>
	 * dove [userId] &egrave l'id dell'Utente del quale si vuole conoscere la
	 * posizione<br>
	 * <br>
	 * @param resp la risposta:<br>
	 * L'xml rappresentante la posizione dell'utente<br>
	 * 404 - Se l'utente non esiste o ha posizione nulla<br>
	 * 401 - Se l'userId non &egrave valido<br>
	 * 500 - In caso di errori <br>
	 */
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
		}catch(NotFoundException e){
			resp.sendError(404);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
}
