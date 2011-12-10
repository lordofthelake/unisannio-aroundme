package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

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
			//Inizializza queriedUsers aggiungendovi tutti gli User presenti sul Datastore
			Collection<User> queriedUsers = ofy.get(User.class).values();			
			
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
				double aLongitudeDegree2Meters = (Math.abs(Math.cos(PositionImpl.deg2rad(myPosition.getLatitude()))*110000));
				double lon1 = myPosition.getLongitude() - radius/ aLongitudeDegree2Meters;
				double lon2 = myPosition.getLongitude() + radius/ aLongitudeDegree2Meters;
				double lat1 = myPosition.getLatitude() - radius/110000;
				double lat2 = myPosition.getLatitude() + radius/110000;

				ArrayList<User> thisqueryResults = new ArrayList<User>();
				for(User u: queriedUsers){
					Position position = u.getPosition();
					if(position.getLongitude() >= lon1 && position.getLongitude() <= lon2 &&
						position.getLatitude() >= lat1 && position.getLatitude() <= lat2)
						thisqueryResults.add(u);
				}
				
				queriedUsers = thisqueryResults;
				
				/*
				Query<User> queriedUsers = ofy.query(User.class); 
				queriedUsers = queriedUsers.filter("position.longitude >=", lon1)
						.filter("position.longitude <=", lon2)
						.filter("position.latitude >=", lat1)
						.filter("position.latitude <=", lat2);
						*/
			}
			
			/*
			 * Query che restituisce gli utenti che hanno tutti gli interessi dati
			 */
			if(this.getInterestIds() != null){
				ArrayList<Long> requiredInterestsKeys = new ArrayList<Long>(this.getInterestIds());
				ArrayList<User> thisqueryResults = new ArrayList<User>();
				
				for(User u: queriedUsers){
					ArrayList<Key<Interest>> uInterestsKeys = new ArrayList<Key<Interest>>(((UserImpl)u).getInterestsKey());
					boolean found = true;
					for(int i = 0; i < requiredInterestsKeys.size() && found; i++){
						found = false;
						for (int j = 0; j < uInterestsKeys.size() && !found; j++)
							if (requiredInterestsKeys.get(i).longValue() == uInterestsKeys.get(j).getId())
								found = true;
					}
					if (found)
						thisqueryResults.add(u);
				}
				
				queriedUsers = thisqueryResults;
			}
			
			/*
			 * Query che restituisce gli utenti che hanno un certo grado di compatibilità,
			 * basata sul numero di interessi in comune, con un utente dato.
			 */
			if(this.getCompatibility() != null){
				UserImpl myUser = (UserImpl) ofy.get(User.class, this.getCompatibility().getUserId());
				ArrayList<Key<Interest>> myInterestsKey = new ArrayList<Key<Interest>>(myUser.getInterestsKey());
				
				/*
				 * Considerando che il rank è espresso con un float da 0,1 a 1, 
				 * requestedRank indica il numero di interessi in comune che si devono avere
				 * per essere considerati compatibili
				 */
				double requiredRank = myInterestsKey.size() * this.getCompatibility().getRank();
				ArrayList<User> thisqueryResults = new ArrayList<User>();
				for(User u: queriedUsers){
					ArrayList<Key<Interest>> uInterestsKeys = new ArrayList<Key<Interest>>(((UserImpl)u).getInterestsKey());
					int foundInterests = 0;
					for(int i = 0; i < myInterestsKey.size() && foundInterests < requiredRank; i++){
						boolean found = false;
						for (int j = 0; j < uInterestsKeys.size() && !found; j++)
							if (myInterestsKey.get(i).equals(uInterestsKeys.get(j)))
								found = true;
						if(found)
							foundInterests++;
					}
					if (foundInterests >= requiredRank)
						thisqueryResults.add(u);
					/*
					 In alternativa, ma con funzionamento non garantito:
					uInterestsKeys.retainAll(myInterestsKey); //Lascia in uInterests solo gli interessi contenuti anche in myInterests
					if(uInterestsKeys.size() >= requiredRank)
						thisqueryResults.add(u);
						*/
				}
				queriedUsers = thisqueryResults;
			}
			
			
			l.onData(queriedUsers);
		}catch(Exception e){
			l.onError(e);
		}
	}

}
