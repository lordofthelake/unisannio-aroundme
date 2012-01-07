package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.*;

/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
@Entity(name="User")
@Indexed
public class UserImpl extends User {
	private static final long serialVersionUID = 1270045595803902572L;
	
	@Id
	private long id;	
	@Unindexed
	private String name;
	@Embedded
	private PositionImpl position;
	@Embedded @Unindexed
	private PreferencesImpl preferences;
	private ArrayList<Long> interests;
	@Transient private ArrayList<Interest> interestsChache;
	@Indexed
	private String authToken;

	public UserImpl(long id, String name) {
		this.id = id;
		this.name = name;
		position = null;
		interests = new ArrayList<Long>();
		interestsChache = new ArrayList<Interest>();
	}
	
	public UserImpl(){
		interests = new ArrayList<Long>();
		interestsChache = new ArrayList<Interest>();
	}
		
	@Override
	public long getId() {
		return id;
	}


	public void addInterest(Interest interest) {
		interests.add(interest.getId());
		interestsChache.add(interest);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Position getPosition() {
		return position;
	}
	
	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	@Override
	public Collection<Interest> getInterests() {
		if(interestsChache.size()==interests.size())
			return interestsChache;
		Objectify ofy = ObjectifyService.begin();
		interestsChache = new ArrayList<Interest>(ofy.get(InterestImpl.class, interests).values());
		return interestsChache;
	}
	
	protected Collection<Long> getInterestKeys(){
		return interests;
	}

	public void setPosition(Position p) {
		if (p instanceof PositionImpl) {
			position = (PositionImpl) p;
		}
		this.position = (PositionImpl) ModelFactory.getInstance().createPosition(p.getLatitude(), p.getLongitude());
	}
	
	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = (PreferencesImpl) preferences;
	}

	@SuppressWarnings("unused")
	@PrePersist
	private void prePerist(){
		if(!interestsChache.isEmpty()){
			Objectify ofy = ObjectifyService.begin();
			ofy.put(interestsChache);
		}
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
