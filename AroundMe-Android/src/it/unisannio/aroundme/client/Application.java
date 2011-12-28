package it.unisannio.aroundme.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.*;

import org.w3c.dom.Node;

import it.unisannio.aroundme.model.*;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Application extends android.app.Application {
	private static final Map<Long, SoftReference<User>> cache = new HashMap<Long, SoftReference<User>>();
	
	public void addToCache(User u) {
		cache.put(u.getId(), new SoftReference<User>(u));
	}
	
	@Override
	public void onCreate() {
		/* FIXME Aggiunti utenti alla cache in modo che non vengano fatte 
		 * query via network. Mock da rimuovere.
		 */
		ModelFactory f = ModelFactory.getInstance();
		Collection<Interest> empty = Collections.<Interest>emptySet();
		addToCache(f.createUser(1321813090L, "Michele Piccirillo", empty));
		addToCache(f.createUser(100000268830695L, "Danilo Iannelli", empty));
		addToCache(f.createUser(100001053949157L, "Marco Magnetti", empty));
		addToCache(f.createUser(100000293335056L, "Giuseppe Fusco", empty));
		
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
					private URL endpoint = null; // FIXME
					private final UserQuery self = this;

					@Override
					public Collection<User> call() throws Exception {
						if(getNeighbourhood() == null 
								&& getCompatibility() == null 
								&& getInterestIds().isEmpty()) {
							// Query by Id: ottimizziamo utilizzando la cache
							Collection<Long> ids = getIds();
							Collection<User> cached = new HashSet<User>();
							for(long id : ids) {
								if(!cache.containsKey(id)) continue;
								User u = cache.get(id).get();
								if(u != null) {
									cached.add(u);
									ids.remove(id);
								}
							}
							
							if(ids.isEmpty()) {
								return cached;
							} else {
								Collection<User> results = UserQuery.byId(ids).call();
								for(User u : cached)
									results.add(u);
								return results;
							}
						}

						return (new HttpTask<Collection<User>>("POST", endpoint) { 
							
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

		});
		super.onCreate();
	}
}
