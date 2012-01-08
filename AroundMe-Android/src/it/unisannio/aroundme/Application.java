package it.unisannio.aroundme;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.w3c.dom.Node;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.widget.Toast;

import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.*;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Application extends android.app.Application {
	private static final LruCache<Long, User> cache = new LruCache<Long, User>(Setup.USER_CACHE_SIZE);
	
	public void addToCache(User u) {
		cache.put(u.getId(), u);
		
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		cache.evictAll();
		Picture.flushCache();
	}
	
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

						return (new HttpTask<Collection<User>>("POST", Setup.BACKEND_USER_URL) { 
							
								protected Collection<User> read(InputStream input) throws Exception {
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
