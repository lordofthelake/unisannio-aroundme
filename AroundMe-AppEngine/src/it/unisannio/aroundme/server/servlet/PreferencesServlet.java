package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.server.UserImpl;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Servlet utilizzata per la gestione delle {@link Preferences} degli Utenti
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class PreferencesServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PreferencesServlet.class.getName());

	/**
	 * Utilizzato per assegnare le preferenze ad un utente <br>
	 * <br>
	 * @param req laichiesta:<br>
	 * L'URI deve essere /user/[userId] <br>
	 * dove [userId] &egrave; l'id dell'Utente al quale si vogliono assegnare le
	 * preferenze<br>
	 * Il body deve contenere l'xml che rappresenta le preferenze da attribuire
	 * all'utente<br>
	 * <br>
	 * @param resp la risposta:<br>
	 * 200 - Se l'operazione va a buon fine<br>
	 * 404 - Se l'utente non esiste<br>
	 * 401 - Se l'userId non &egrave; valido<br>
	 * 500 - In caso di errori <br>
	 */
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
		}catch (NotFoundException e){
			resp.sendError(404);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
	
	/**
	 * Utilizzato per ottenere le preferenze di un User<br>
	 * <br>
	 * @param req la richiesta:<br>
	 * L'URI deve essere /user/[userId]<br>
	 * dove [userId] &egrave; l'id dell'Utente del quale si vogliono ottenere le
	 * preference<br>
	 * <br>
	 * @param resp la risposta:<br>
	 * L'xml rappresentante le preferenze dell'utente<br>
	 * 404 - Se l'utente non esiste o ha perenze nulle<br>
	 * 401 - Se l'userId non &egrave; valido<br>
	 * 500 - In caso di errori <br>
	 */
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
		}catch (NotFoundException e){
			resp.sendError(404);
		}catch (NullPointerException e) {
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
	
}
