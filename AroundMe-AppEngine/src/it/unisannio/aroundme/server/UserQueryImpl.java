package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.objectify.Key;
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
			Query<User> query = ofy.query(User.class);		
			
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
			
			Collection<User> queriedUsers = query.list();
			
			/*
			 * Query che restituisce gli utenti che hanno tutti gli interessi dati
			 * 
			 * TODO Query sul DataStore invece che in memory
			 */
			if(this.getInterestIds() != null){
				
				ArrayList<Long> requiredInterestsKeys = new ArrayList<Long>(this.getInterestIds());
				
				for(User u: queriedUsers){
					ArrayList<Key<Interest>> uInterestsKeys = new ArrayList<Key<Interest>>(((UserImpl)u).getInterestKeys());
					boolean found = true;
					for(int i = 0; i < requiredInterestsKeys.size() && found; i++){
						found = false;
						for (int j = 0; j < uInterestsKeys.size() && !found; j++)
							if (requiredInterestsKeys.get(i).longValue() == uInterestsKeys.get(j).getId())
								found = true;
					}
					if (!found)
						queriedUsers.remove(u);
				}
				
			}
			
			/*
			 * Query che restituisce gli utenti che hanno un certo grado di compatibilit�,
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
			
			
			l.onData(queriedUsers);
		} catch(Exception e){
			l.onError(e);
		}
	}

}
