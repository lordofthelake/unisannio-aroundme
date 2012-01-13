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
 * Implementazione lato server di {@link User}.
 * Utilizza le annotazioni necessarie per la persistenza sul Datastore
 * 
 * @see User
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
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

	/**
	 * Crea un nuovo {@link User}.
	 * &Egrave dichiarato protected per evitare che
	 * non venga creato tramite {@link ModelFactory}. 
	 * @param l'id univoco dell'User
	 * @param name il nome dell'User
	 */
	protected UserImpl(long id, String name) {
		this.id = id;
		this.name = name;
		position = null;
		interests = new ArrayList<Long>();
		interestsChache = new ArrayList<Interest>();
	}
	
	/**
	 * Costruttore senza argomenti necessario per la persistenza dell'User sul Datastore
	 */
	protected UserImpl(){
		interests = new ArrayList<Long>();
		interestsChache = new ArrayList<Interest>();
	}
	
	/**
	 * {@inheritDoc}	
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Aggiunge un {@link Interest} all'User
	 * @param interest L'Interest da aggiungere all?user
	 */
	public void addInterest(Interest interest) {
		interests.add(interest.getId());
		interestsChache.add(interest);
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}	
	 */
	@Override
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Restituisce il token di registrazione di Facebook
	 * @return Il token di registrazione di Facebook
	 */
	public String getAuthToken() {
		return authToken;
	}
	
	/**
	 * Associa all'User il token di registrazione di Facebook
	 * @param authToken Il token di registrazione di Facebook
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public Collection<Interest> getInterests() {
		if(interestsChache.size()==interests.size())
			return interestsChache;
		Objectify ofy = ObjectifyService.begin();
		interestsChache = new ArrayList<Interest>(ofy.get(InterestImpl.class, interests).values());
		return interestsChache;
	}
	
	/**
	 * Restituisce una collezione contenente le chiavi degli interessi dell'User
	 * @return una collezione contenente le chiavi degli interessi dell'User
	 */
	protected Collection<Long> getInterestKeys(){
		return interests;
	}
	
	/**
	 * {@inheritDoc}	
	 */
	public void setPosition(Position position) {
		this.position = (PositionImpl) position;
	}
	
	/**
	 * Restituisce le {@link Preferences} associate all'User
	 * @return le {@link Preferences} associate all'User
	 */
	public Preferences getPreferences() {
		return preferences;
	}
	
	/**
	 * Associa {@link Preferences} all'User
	 * @param preferences le {@link Preferences} da associare all'User
	 */
	public void setPreferences(Preferences preferences) {
		this.preferences = (PreferencesImpl) preferences;
	}
	
	/**
	 * Esegue le operazioni da effettuare prima di persistere l'User sul Datastore
	 */
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
