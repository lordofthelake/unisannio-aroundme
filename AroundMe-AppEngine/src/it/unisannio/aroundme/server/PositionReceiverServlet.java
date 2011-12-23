package it.unisannio.aroundme.server;

import it.unisannio.aroundme.model.DataListener;
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

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		try {
			final User user = new UserImpl();//TODO Recuperare User tramite id indicato nel path della request
			Position position = Position.SERIALIZER.fromXML(SerializerUtils.getDocumentBuilder().parse(req.getInputStream()));
			user.setPosition(position);

			Neighbourhood neighbourhood = new Neighbourhood();
			neighbourhood.setPosition(position);
			neighbourhood.setRadius(100); //TODO Retrieve radius

			UserQuery query = new UserQueryImpl();
			query.setNeighbourhood(neighbourhood);
			query.perform(new DataListener<Collection<? extends User>>() {

				@Override
				public void onData(Collection<? extends User> users) {
					//Creazione dell'oggetto che gestisce l'invio delle notifiche ai device
					C2DMNotificationSender notificationSender = new C2DMNotificationSender(new C2DMConfigLoader());
					try {
						for(User u: users){
							notificationSender.sendNotification("registrationId di u", user.getId());
							notificationSender.sendNotification("registrationId di user", u.getId());
							//TODO ottenere i registrationId per poter effettuare l'invio
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Exception e) {
					try {
						resp.sendError(500);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
