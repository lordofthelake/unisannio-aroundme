package it.unisannio.aroundme.server;

import java.util.Collection;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

import it.unisannio.aroundme.model.*;


/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserQueryImpl extends UserQuery {

	private static final long serialVersionUID = 1L;


	@Override
	public Collection<User> call() throws Exception {
	
		Objectify ofy = ObjectifyService.begin();
		Query<User> query = ofy.query(User.class);		
		
		/*
		 * Query che ristituisce gli utenti con gli id forniti
		 */
		if(this.getIds() != null){
			for(Long id: this.getIds())
				query.filter("id", id.longValue());
			/*XXX In questo modo si continua la lavorare su un oggetto Query<User>.
			 * Se ciò non è necessario (se non conta l'ordine in cui questa query viene eseguita
			 * rispetto alle altre) si può utilizzare il più cionveniente get:
			 * Collection<User> users = ofy.get(User.class, this.getIds()); 		
			 */
		}
		
		/*
		 * Query che restituisce gli utenti nelle vicinaze di una
		 * data posizione entro un certo raggio.
		 */
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
			double aLongitudeDegree2Meters = (Math.abs(Math.cos(Math.toRadians(myPosition.getLatitude()))*110000));

			query.filter("position.longitude >=", myPosition.getLongitude() - radius/ aLongitudeDegree2Meters)
					.filter("position.longitude <=", myPosition.getLongitude() + radius/ aLongitudeDegree2Meters)
					.filter("position.latitude >=", myPosition.getLatitude() - radius/110000)
					.filter("position.latitude <=", myPosition.getLatitude() + radius/110000);
					
		}
		
		/*
		 * Query che restituisce gli utenti che hanno tutti gli interessi dati
		 */
		if(this.getInterestIds() != null){
			Collection<Interest> requiredInterests = ofy.get(Interest.class, this.getInterestIds()).values();
			for(Interest interest: requiredInterests){
				query.filter("interests", interest);
			}
		
		}
		
		Collection<User> queriedUsers = query.list();
		
		/*
		 * Query che restituisce gli utenti che hanno un certo grado di compatibilit&agrave;,
		 * basata sul numero di interessi in comune, con un utente dato.
		 */
		if(this.getCompatibility() != null){
			float requiredRank = getCompatibility().getRank();
			User myUser = (User) ofy.get(User.class, this.getCompatibility().getUserId());
			
			for(User u: queriedUsers){
				if(myUser.getCompatibilityRank(u) < requiredRank)
					queriedUsers.remove(u);
			}
		}
		
		return queriedUsers;
	
	}

}
