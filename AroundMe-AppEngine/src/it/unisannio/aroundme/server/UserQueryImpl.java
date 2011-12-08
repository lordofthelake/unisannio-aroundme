package it.unisannio.aroundme.server;

import java.util.Collection;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;


/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserQueryImpl extends UserQuery{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override 
	public void perform(DataListener<Collection<User>> l) {
		try{
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
			
			Objectify ofy = ObjectifyService.begin();
			Collection<User> c = ofy.query(User.class).filter("position.getLongitude >=", lon1)
													.filter("position.getLongitude <=", lon2)
													.filter("position.getLatitude >=", lat1)
													.filter("position.getLatitude <=", lat2)
													.list();
			
			// FIXME Filtra anche per id e per interessi
			
			/*
			 * Riguardo alla query precedente, non credo che se si possa accedere alla latitudine
			 * e alla longitudine di una posizione tramite medoto get. Gli esempi sulla documentazione
			 * vi accedono con position.latitude o position.longitude dato che dichiarano le variabili,
			 * come protected oltre che flaggate con annotazione @Embedded.
			 */
			
			l.onData(c);
		}catch(Exception e){
			l.onError(e);
		}
	}

}
