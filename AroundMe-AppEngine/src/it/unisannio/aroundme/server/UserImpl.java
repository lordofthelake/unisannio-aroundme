package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Embedded;
import javax.persistence.Id;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.*;

/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
@Indexed
class UserImpl extends User {
	private static final long serialVersionUID = 1270045595803902572L;

	@Id
	private long id;	
	
	@Unindexed
	private String name;
	
	
	@Embedded
	private Position position;	
	
	private ArrayList<Key<Interest>> interestsKeys; 

	@Override
	public long getId() {
		return id;
	}

	/* FIXME la parte di creazione degli interessi dovr� essere spostata nella factory.
	 * 
	 */
	/*
	@Override
	public void addInterest(Interest interest) {
		Objectify ofy = ObjectifyService.begin();
		if(ofy.get(Interest.class, interest.getId()) == null)			
			ofy.put(interest);
		interestsKeys.add(new Key<Interest>(Interest.class, interest.getId()));
	}*/

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Position getPosition() {
		return position;
	}


	@Override
	public Collection<Interest> getInterests() {
		Objectify ofy = ObjectifyService.begin();
		return ofy.get(interestsKeys).values();
	}
	
	protected Collection<Key<Interest>> getInterestKeys(){
		return interestsKeys;
	}

	public void setPosition(Position p) {
		this.position = p;
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * Versione ottimizzata per l'uso sul DataStore: se entrambi gli utenti hanno la stessa implementazione basta il confronto sulle chiavi, evitando la query sugli interessi.
	 */
	@Override
	public float getCompatibilityRank(User u) {
		return (u instanceof UserImpl) ? getCompatibilityRank(getInterestKeys(), ((UserImpl) u).getInterestKeys()) : super.getCompatibilityRank(u);
	}
}
