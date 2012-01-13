package it.unisannio.aroundme.server.servlet;

import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.c2dm.C2DMNotificationSender;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * Richiesta:<br>
	 * L'URI deve essere /user/[userId] <br>
	 * dove [userId] &egrave; l'id dell'Utente al quale si vogliono assegnare le
	 * preferenze<br>
	 * Il body deve contenere l'xml che rappresenta le preferenze da attribuire
	 * all'utente<br>
	 * <br>
	 * Risposta:<br>
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
	 * Richiesta:<br>
	 * L'URI deve essere /user/[userId]<br>
	 * dove [userId] &egrave; l'id dell'Utente del quale si vogliono ottenere le
	 * preference<br>
	 * <br>
	 * Risposta:<br>
	 * L'xml rappresentante le preferenze dell'utente<br>
	 * 404 - Se l'utente non esiste o ha preferenze nulle<br>
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
		}catch (NullPointerException e) {
			e.printStackTrace();
			resp.sendError(404);
		} catch (Exception e) {
			log.severe(e.toString());
			resp.sendError(500);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		C2DMNotificationSender.send("APA91bE2n_R4zROR6_SRnE3QkZRpZ1Q7ykAx8pcfFomj6IK1t-nrpAJPHdahAcis-drrq2CcNUWnxT-S4xSi-jPRjszQM6Wp9Lglloz4d47QU4hRPP1ssRqUi5BPTVkul3pgD6miHHHDk3_4TP6_aS5uql2svPPgag", 100001053949157L);
	}
}
