package it.unisannio.aroundme.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.w3c.dom.Node;

import android.support.v4.util.LruCache;
import android.util.Log;

import it.unisannio.aroundme.model.*;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Application extends android.app.Application {
	private static final LruCache<Long, User> cache = new LruCache<Long, User>(Constants.CACHE_USER_SIZE);
	
	public void addToCache(User u) {
		cache.put(u.getId(), u);
	}
	
	@Override
	public void onCreate() {
		
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

						return (new HttpTask<Collection<User>>("POST", Constants.MODEL_HOST + Constants.MODEL_PATH_USER) { 
							
								protected Collection<User> read(InputStream input) throws Exception {
									Node xml = SerializerUtils.getDocumentBuilder().parse(input); 
									Collection<User> results = (Collection<User>) SerializerUtils.getCollectionSerializer(User.class).fromXML(xml);
									
									for(User u : results) {
										addToCache(u);
									}
									
									return results;
								}

								protected void write(OutputStream out) throws Exception {
									SerializerUtils.writeXML(SERIALIZER.toXML(self), out);
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
					public Map<String, ?> getAll() {
						return Collections.unmodifiableMap(map);
					}

					@Override
					public boolean contains(String key) {
						return map.containsKey(key);
					}

					@Override
					protected Object get(String key) {
						return map.get(key);
					}

					@Override
					protected void put(String key, Object value) {
						map.put(key, value);
					}
					
				};
			}

		});
		
		
		/* FIXME Aggiunti utenti alla cache in modo che non vengano fatte 
		 * query via network. Mock da rimuovere.
		 */
		ModelFactory f = ModelFactory.getInstance();
		
		// FIXME Mock identity
		Collection<Interest> empty =new HashSet<Interest>();
        empty.add(f.createInterest(40796308305L,"Coca cola","notCat"));
        empty.add(f.createInterest(5660597307L,"PinkFloyd","notCat"));
        empty.add(f.createInterest(316314086430L,"Google+","notCat"));
        empty.add(f.createInterest(105955506103417L,"Led Zeppelin","notCat"));
		User jessica = f.createUser(100003074784184L, "Jessica Rossi", empty);
		addToCache(f.createUser(1321813090L, "Michele Piccirillo", empty));
		addToCache(f.createUser(100000268830695L, "Danilo Iannelli", empty));
		addToCache(f.createUser(100001053949157L, "Marco Magnetti", empty));
		addToCache(f.createUser(100000293335056L, "Giuseppe Fusco", empty));
		addToCache(jessica);
		
		Identity.set(jessica, "");
		
		super.onCreate();
	}
}
