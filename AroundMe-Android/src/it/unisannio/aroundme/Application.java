package it.unisannio.aroundme;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import android.support.v4.util.LruCache;


import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.*;

/**
 * Classe che mantiene uno stato globale dell'applicazione.
 * 
 * Essendo la prima classe che viene istanziata all'avvio dell'applicazione, viene 
 * utilizzata per effettuare operazioni di setup iniziale. Viene utilizzata inoltre per segnalare a tutte le attivitˆ avviate
 * quando dovrebbero terminare se stesse (es. per un logout o cancellazione dell'account).
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Application extends android.app.Application {
	private static final LruCache<Long, User> cache = new LruCache<Long, User>(Setup.USER_CACHE_SIZE);
	
	private boolean terminated = false;
	
	/**
	 * Imposta un flag per segnalare a tutte le componenti avviate che dovrebbero terminare.
	 * 
	 * @see #isTerminated()
	 */
	public void terminate() {
		terminated = true;
		cache.evictAll();
		Picture.flushCache();
	}
	
	/**
	 * Restituisce un flag indicante se le componenti avviate dovrebbero esaurire il loro ciclo di vita.
	 * 
	 * @return {@code true} se le componenti dovrebbero terminare la loro vita, {@code false} altrimenti
	 */
	public boolean isTerminated() {
		return terminated;
	}
	
	private void addToCache(User u) {
		cache.put(u.getId(), u);	
	}
	
	/**
	 * Richiamato quando il sistema ha poca memoria a disposizione.
	 * 
	 * Una chiamata a questo metodo risulta nello svuotamento delle cache dell'applicazione in memoria
	 * 
	 * @see Picture#flushCache()
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		cache.evictAll();
		Picture.flushCache();
	}
	
	/**
	 * Effettua operazioni di setup all'avvio dell'applicazione.
	 * 
	 * In questa sede viene istanziata un'opportuna implementazione della {@link ModelFactory}, i cui {@link Model} sono gi&agrave;
	 * configurati per interagire con il server di backend.
	 * 
	 * <p>Per migliorare l'efficienza, sono utilizzati meccanismi di caching: per API Level 13 e superiori viene installata una cache
	 * che usa la memoria interna del dispositivo, principalmente usata per un caching pi&ugrave; duraturo delle immagini. Per tutte
	 * le versioni invece viene aggiunta una cache degli utenti, in grado di risolvere (parzialmente o completamente) in locale UserQuery
	 * che utilizzino solo gli ID utente come criteri di ricerca.</p>
	 * 
	 * @see android.net.http.HttpResponseCache
	 * @see UserQuery#byId(long...)
	 * @see UserQuery#single(long)
	 */
	@Override
	public void onCreate() {
		
		/* Cache HTTP con storage sulla memoria interna.
		 * 
		 * Disponibile solo per API Level >= 13.
		 * 
		 * @see http://android-developers.blogspot.com/2011/09/androids-http-clients.html
		 * @see android.net.http.HttpResponseCache
		 */
		try {
			Class.forName("android.net.http.HttpResponseCache")
			.getMethod("install", File.class, long.class)
			.invoke(null, new File(getCacheDir(), "http"), Setup.NETWORK_CACHE_SIZE);
		} catch (Exception e) {}



		ModelFactory.setInstance(new ModelFactory() {
			
			@Override
			public User createUser(final long id, final String name, final Collection<Interest> interests) {
				final Collection<Interest> collection = Collections.unmodifiableCollection(interests);
				
				return new User () {
					private static final long serialVersionUID = 1L;
					
					private Position position;

					@Override
					public long getId() {
						return id;
					}

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
						return collection;
					}

					@Override
					public void setPosition(Position position) {
						this.position = position;
					}

				};
			}

			@Override
			public Interest createInterest(final long id, final String name, final String category) {
				return new Interest() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getName() {
						return name;
					}

					@Override
					public String getCategory() {
						return category;
					}

					@Override
					public long getId() {
						return id;
					}

				};
			}

			@Override
			public Position createPosition(final double latitude, final double longitude) {
				return new Position() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public double getLatitude() {
						return latitude;
					}

					@Override
					public double getLongitude() {
						return longitude;
					}

				};
			}


			@Override
			public UserQuery createUserQuery() {
				return new UserQuery() {
					private static final long serialVersionUID = 1L;
					
					private final UserQuery self = this;
					@Override
					public Collection<User> call() throws Exception {
						
						if(getNeighbourhood() == null 
								&& getCompatibility() == null 
								&& getInterestIds().isEmpty()) {
							
							
							// Query by Id: ottimizziamo utilizzando la cache
							Collection<Long> ids = getIds();
							Collection<User> cached = new HashSet<User>();
							for(Iterator<Long> i = ids.iterator(); i.hasNext();) {
								User u = cache.get(i.next());
								if(u != null) {
									cached.add(u);
									i.remove();
								}
							}

							if(ids.isEmpty()) {
								return cached;
							} 
							
							if(!cached.isEmpty()){
								Collection<User> results = UserQuery.byId(ids).call();
								for(User u : cached)
									results.add(u);
								return results;
							}
						}

						return (new HttpTask<Collection<User>>("POST", Setup.BACKEND_USER_URL_SIMPLE) { 
							
								protected Collection<User> read(InputStream input) throws Exception {
									@SuppressWarnings("unchecked")
									Collection<User> results = (Collection<User>) Serializer.ofCollection(User.class).read(input);
									
									for(User u : results) {
										addToCache(u);
									}
									
									return results;
								}

								protected void write(OutputStream out) throws Exception {
									SERIALIZER.write(self, out);
								}

							}).call();
					}

				};
			}

			@Override
			public Preferences createPreferences() {
				return new Preferences() {
					private static final long serialVersionUID = 1L;
					
					private final Map<String, Object> map = new HashMap<String, Object>();

					@Override
					public Map<String, Object> getAll() {
						return Collections.unmodifiableMap(map);
					}

					@Override
					public boolean contains(String key) {
						return map.containsKey(key);
					}

					@Override
					protected Object getObject(String key) {
						return map.get(key);
					}

					@Override
					protected void putObject(String key, Object value) {
						map.put(key, value);
					}
					
				};
			}

		});		
		super.onCreate();
	}
}
