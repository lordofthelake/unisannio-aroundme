package it.unisannio.aroundme.model;

import java.util.Collection;


/**
 * Classe factory per la creazione dei {@link Model} che hanno un'implementazione dipendente dalla piattaforma su cui vengono utilizzati.
 * 
 * <p>Le implementazioni dovrebbero estendere questa classe e impostare la singola istanza che verr&agrave; usata sulla piattaforma tramite 
 * il metodo {@link #setInstance(ModelFactory)}. Oltre che dal codice utente, viene utilizzata dai {@link Serializer} dei singoli modelli per
 * istanziare l'implementazione corretta del Model per la piattaforma in uso.</p>
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class ModelFactory {
	private static ModelFactory instance;
	
	/**
	 * Imposta l'istanza che verr&agrave; usata successivamente.
	 * 
	 * @param factory l'istanza che verr&agrave; utilizzata
	 * @see #getInstance()
	 */
	public static void setInstance(ModelFactory factory) {
		instance = factory;
	}
	
	/**
	 * Restituisce l'istanza della factory correntemente in uso.
	 * 
	 * @return l'implementazione in uso della factory
	 * @throws IllegalStateException se non &egrave; stata impostata precedentemente un'istanza
	 * 
	 * @see #setInstance(ModelFactory)
	 */
	public static ModelFactory getInstance() {
		if(instance == null)
			throw new IllegalStateException("No concrete factory set.");
		
		return instance;
	}
	
	/**
	 * Crea uno {@link User} con le propriet&agrave; specificate e posizione sconosciuta.
	 * 
	 * @param id l'id dell'utente (corrispondente all'ID assegnato nelle Facebook Graph API)
	 * @param name il nome dell'utente
	 * @return un'istanza della classe User con le propriet&agrave; specificate
	 * 
	 * @see User
	 */
	public abstract User createUser(long id, String name, Collection<Interest> interests);
	
	/**
	 * Crea un {@link Interest} con le propriet&agrave; specificate.
	 * 
	 * @param id l'id dell'interesse (corrispondente all'ID assegnato nelle Facebook Graph API)
	 * @param name il nome dell'interesse
	 * @param category la categoria a cui l'interesse appartiene
	 * 
	 * @return un'istanza della classe Interest con le propriet&agrave; specificate
	 * 
	 * @see Interest
	 */
	public abstract Interest createInterest(long id, String name, String category);
	
	/**
	 * Crea una {@link Position} con le propriet&agrave; specificate.
	 * 
	 * @param lat la latitudine, espressa in gradi decimali
	 * @param lon la longitudine, espressa in gradi decimali 
	 * @return un'istanza della classe Position con le propriet&agrave; specificate
	 * 
	 * @see Position
	 */
	public abstract Position createPosition(double lat, double lon);
	
	/**
	 * Crea una {@link UserQuery} vuota.
	 * 
	 * La query non deve avere nessun parametro settato dopo l'istanziazione.
	 * 
	 * @return una nuova istanza di UserQuery
	 * @see UserQuery
	 */
	public abstract UserQuery createUserQuery();
	
	/**
	 * Crea un'istanza di {@link Preferences} vuota.
	 * 
	 * Le preferenze alla creazione non devono avere chiavi impostate.
	 * 
	 * @return una nuova istanza di Preferences
	 * @see Preferences
	 */
	public abstract Preferences createPreferences();
	
}
