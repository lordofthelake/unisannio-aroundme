package it.unisannio.aroundme.server;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.SerializerUtils;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.c2dm.C2DMConfigLoader;
import it.unisannio.aroundme.server.c2dm.C2DMNotificationSender;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class PositionReceiverServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		try {
			final UserImpl user = new UserImpl();//TODO Recuperare User tramite id indicato nel path della request
			Position position = Position.SERIALIZER.fromXML(SerializerUtils.getDocumentBuilder().parse(req.getInputStream()));
			user.setPosition(position);

			Neighbourhood neighbourhood = new Neighbourhood();
			neighbourhood.setPosition(position);
			neighbourhood.setRadius(100); //TODO Retrieve radius
			Compatibility compatibility = new Compatibility(user.getId(), 60);//TODO Retrieve compatibility

			UserQuery query = new UserQueryImpl();
			query.setNeighbourhood(neighbourhood);
			query.setCompatibility(compatibility);
			Collection<? extends User> users = query.call();

			//Creazione dell'oggetto che gestisce l'invio delle notifiche ai device
			C2DMNotificationSender notificationSender = new C2DMNotificationSender(new C2DMConfigLoader());

			for(User u: users){
				notificationSender.sendNotification("registrationId di u", user.getId());
				notificationSender.sendNotification("registrationId di user", u.getId());
				//TODO ottenere i registrationId per poter effettuare l'invio
			}

		} catch (Exception e) {
			try {
				resp.sendError(500);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
	}
}
