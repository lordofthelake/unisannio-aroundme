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

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;



/**
 * Servlet utilizzata per la gestione degli {@link User}
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(UserServlet.class.getName());
	
	/**
	 * Uilizzato per effettuare una query sugli {@link User}
	 * <br><br>
	 * @param req laichiesta:<br>
	 * L'URI deve essere /user/<br>
	 * Il body deve contenere l'xml che descrive la query da effettuare.
		<br>
	 * @param resp la risposta:<br>
	 * Restituisce nel body l'xml che descrive una collezione degli
	 * Utenti ottenuti dalla query;<br>
	 * 500 - In caso di errori
	 */
	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp)	throws ServletException, IOException {
		try {
			UserQuery query = UserQuery.SERIALIZER.read(req.getInputStream());
			log.info(UserQuery.SERIALIZER.toString(query));
			Collection<User> users = query.call();
			resp.setContentType("text/xml");
			Serializer.ofCollection(User.class).write(users, resp.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(e.toString());
			resp.sendError(500);
		}				
	}

	/**
	 * Utilizzato per la creazione di un User<br>
	 * <br>
	 * @param req la richiesta:<br>
	 * L'URI deve essere /user/<br>
	 * Il body deve contenere lo xml che descrive l'user da creare.<br>
	 * <br>
	 * @param resp la risposta:<br>
	 * 200 - Se l'operazione &egrave; andata a buon fine<br>
	 * 500 - In caso di errori<br>
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			UserImpl user = (UserImpl) User.SERIALIZER.read(req.getInputStream());
			log.info(User.SERIALIZER.toString(user));
			Objectify ofy = ObjectifyService.begin();
			user.setAuthToken(req.getHeader("X-AccessToken"));
			ofy.put(user);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
	
	/**
	 * Utilizzato per eliminare un {@link User}
	 * <br><br>
	 * @param req la richiesta:<br>
	 * L'URI deve essere /user/[userId] <br>
	 * dove [userId] &egrave; l'id dell'utente da eliminare
	 * <br><br>
	 * 
	 * @param resp la risposta:<br>
	 * 200 - Se l'operazione &egrave; andata a buon fine<br>
	 * 401 - Se l'Id non &egrave; valido<br>
	 * 404 - Se l'utente non &egrave; stato trovato<br>
	 * 500 - In caso di errori
	 */
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
		} catch(NotFoundException e){
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
}

