package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.CostFunction;
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
		Position myPosition = null;
		double radius = 0;
		if(getNeighbourhood() != null){

			/**
			 * Il Datastore del Google AppEngine non supporta l'utilizzo di pi&ugrave; filtri di disuguaglianza  (<, <=, >=, >, !=)
			 * per una stessa query.
			 * Per ottenere, dunque, gli utenti compresi in una certa area, bisogna ricorrere ad una tecnica simile al Geohashing
			 * detta Geomodelling.
			 * Questa si basa sul concetto di Geocell. Una Geocell è un'area rettangolare identificata tramite un algoritmo di
			 * hashing. Ogni Geocell contiene, ricorsivamente, altre Geocell identificate tramite un hash che presenta lo stesso prefisso
			 * del Geocell che le contiene. In questo modo è possibile identificare univocamente una posizione grazie alla cella che la
			 * contiene.
			 * 				
			 * @see http://code.google.com/apis/maps/articles/geospatial.html
			 */
			/*
			 * Creazione di un quadrato di un certo "raggio" a partire da una posizione; utilizzato per definire
			 * l'area all'interno della quale altri utenti sono considerati vicini.
			 * Si basa sul fatto che 1° di latitudine corrisponde a circa 110000 metri
			 * e un 1° di longitudine corrisponde a circa cos(latitudine in radianti)*110000 metri
			 */
			myPosition = this.getNeighbourhood().getPosition();
			radius = this.getNeighbourhood().getRadius();
			double aLongitudeDegree2Meters = (Math.abs(Math.cos(Math.toRadians(myPosition.getLatitude()))*110000));
			
			BoundingBox boundingBox = new BoundingBox(myPosition.getLatitude() + radius/110000, myPosition.getLongitude() + radius/ aLongitudeDegree2Meters,
													  myPosition.getLatitude() - radius/110000, myPosition.getLongitude() - radius/ aLongitudeDegree2Meters);
			
			
			/*
			 * Restituisce gli hash delle celle contenute nel quadrato appena creato.
			 */
			List<String> cells = GeocellManager.bestBboxSearchCells(boundingBox, new CostFunction() {				@Override
				public double defaultCostFunction(int numCells, int resolution) {
					return numCells > 100 ? Double.MAX_VALUE : 0;
				}
			});

			/*
			 * La query filtra le posizioni presenti in una delle celle contenute nel quadrato. 
			 */
			query.filter("position.cells in", cells);
			
		}
		
		/*
		 * Query che restituisce gli utenti che hanno tutti gli interessi dati
		 */
		if(this.getInterestIds() != null && !this.getInterestIds().isEmpty()){
			for(Long id : this.getInterestIds())
				query.filter("interests", id);
		}
		
		Collection<UserImpl> queriedUsers = query.list();
		
		if(this.getNeighbourhood()!= null){
			for(Iterator<UserImpl> i = queriedUsers.iterator(); i.hasNext(); ){
				UserImpl u = i.next();
				if(myPosition.getDistance(u.getPosition()) > radius)
					i.remove();
			}
		}
		
		/*
		 * Query che restituisce gli utenti che hanno un certo grado di compatibilit&agrave;,
		 * basata sul numero di interessi in comune, con un utente dato.
		 */
		if(this.getCompatibility() != null){
			float requiredRank = getCompatibility().getRank();
			UserImpl myUser = ofy.get(UserImpl.class, this.getCompatibility().getUserId());
			
			for(Iterator<UserImpl> i = queriedUsers.iterator(); i.hasNext(); ){
				UserImpl u = i.next();
				if(u.getId() == myUser.getId() ){
					i.remove();
					continue;
				}
				if(myUser.getCompatibilityRank(u) < requiredRank)
					i.remove();
			}
		}
		
		Collection<User> results = new ArrayList<User>();
		results.addAll(queriedUsers);
		
		return results;
	
	}

}