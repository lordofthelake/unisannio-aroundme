package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
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
		Query<UserImpl> query = ofy.query(UserImpl.class);		
		
		/*
		 * Query che ristituisce gli utenti con gli id forniti
		 */
		if(this.getIds() != null && !this.getIds().isEmpty()){
			query.filter("id in", this.getIds());
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
			/*
			 * Il Datastore del Google AppEngine non supporta l'utilizzo di più filtri di disuguaglianza  (<, <=, >=, >, !=)
			 * per una stessa query...
			query.filter("position.longitude >=", myPosition.getLongitude() - radius/ aLongitudeDegree2Meters)
					.filter("position.longitude <=", myPosition.getLongitude() + radius/ aLongitudeDegree2Meters)
					.filter("position.latitude >=", myPosition.getLatitude() - radius/110000)
					.filter("position.latitude <=", myPosition.getLatitude() + radius/110000);
			*/
			BoundingBox boundingBox = new BoundingBox(myPosition.getLatitude() + radius/110000, myPosition.getLongitude() + radius/ aLongitudeDegree2Meters,
													  myPosition.getLatitude() - radius/110000, myPosition.getLongitude() - radius/ aLongitudeDegree2Meters);
			
			List<String> cells = GeocellManager.bestBboxSearchCells(boundingBox, null);
			query.filter("position.cells in", cells);
		}
		
		/*
		 * Query che restituisce gli utenti che hanno tutti gli interessi dati
		 */
		if(this.getInterestIds() != null && !this.getInterestIds().isEmpty()){
			Collection<InterestImpl> requiredInterests = ofy.get(InterestImpl.class, this.getInterestIds()).values();
			query.filter("interests in", requiredInterests);		
		}
		
		Collection<UserImpl> queriedUsers = query.list();
		
		/*
		 * Query che restituisce gli utenti che hanno un certo grado di compatibilit&agrave;,
		 * basata sul numero di interessi in comune, con un utente dato.
		 */
		if(this.getCompatibility() != null){
			float requiredRank = getCompatibility().getRank();
			UserImpl myUser = ofy.get(UserImpl.class, this.getCompatibility().getUserId());
			
			for(UserImpl u: queriedUsers){
				if(myUser.getCompatibilityRank(u) < requiredRank)
					queriedUsers.remove(u);
			}
		}
		
		Collection<User> results = new ArrayList<User>();
		results.addAll(queriedUsers);
		
		return results;
	
	}

}