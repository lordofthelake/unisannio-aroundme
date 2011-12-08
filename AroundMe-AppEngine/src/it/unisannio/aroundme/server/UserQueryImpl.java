package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;


/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserQueryImpl extends UserQuery{

	private static final long serialVersionUID = 1L;

	@Override 
	public void perform(DataListener<Collection<User>> l) {
		try{
			Objectify ofy = ObjectifyService.begin();
			Query<User> queriedUsers = ofy.query(User.class);
			if(getNeighbourhood() != null){
				/* Creazione di un quadrato di un certo "raggio"
				 * a partire da una posizione; utilizzato per definire
				 * l'area all'interno della quale altri utenti sono 
				 * considerati vicini.
				 * Si basa sul fatto che 1° di latitudine corrisponde a circa 110000 metri
				 * e un 1° di longitudine corrisponde a circa cos(latitudine in radianti)*110000 metri
				 */
				Position myPosition = this.getNeighbourhood().getPosition();
				double radius = this.getNeighbourhood().getRadius();
				double aLongitudeDegree2Meters = (Math.abs(Math.cos(PositionImpl.deg2rad(myPosition.getLatitude()))*110000));
				double lon1 = myPosition.getLongitude() - radius/ aLongitudeDegree2Meters;
				double lon2 = myPosition.getLongitude() + radius/ aLongitudeDegree2Meters;
				double lat1 = myPosition.getLatitude() - radius/110000;
				double lat2 = myPosition.getLatitude() + radius/110000;

				queriedUsers = queriedUsers.filter("position.longitude >=", lon1)
						.filter("position.longitude <=", lon2)
						.filter("position.latitude >=", lat1)
						.filter("position.latitude <=", lat2);
			}
			if(this.getCompatibility() != null){
				User myUser = ofy.get(User.class, this.getCompatibility().getUserId());
				ArrayList<Interest> myInterests = new ArrayList<Interest>(myUser.getInterests());
				
				/*
				 * Considerando che il rank è espresso con un float da 0,1 a 1, 
				 * requestedRank indica il numero di interessi in comune che si devono avere
				 * per essere considerati compatibili
				 */
				double requestedRank = myInterests.size() * this.getCompatibility().getRank();
				for(User u:queriedUsers.list()){
					ArrayList<Interest> uInterests = new ArrayList<Interest>(u.getInterests());
					uInterests.retainAll(myInterests); //Lascia in uInterests solo gli interessi contenuti anche in myInterests
					if(uInterests.size() < requestedRank)
						queriedUsers.filter("id !=", u.getId()); //Rimuove questo utente dal risultato della query
				}		
			}
			if(this.getInterestIds() != null){
				//TODO
			}
			l.onData(queriedUsers.list());
		}catch(Exception e){
			l.onError(e);
		}
	}

}
