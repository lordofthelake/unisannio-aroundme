package it.unisannio.aroundme.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.model.SerializerUtils;
import it.unisannio.aroundme.model.User;

public class Application extends android.app.Application {
	private static final Map<Long, SoftReference<User>> cache = new HashMap<Long, SoftReference<User>>();
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
					public void setPosition(Position p) {
						position = p;
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
					public Collection<? extends User> call() throws Exception {
						// TODO Implement caching
						return (new HttpTask<Collection<? extends User>>("POST", endpoint) { 
							
								protected Collection<? extends User> read(InputStream input) throws Exception {
									Node xml = SerializerUtils.getDocumentBuilder().parse(input); 
									return SerializerUtils.getCollectionSerializer(User.class).fromXML(xml);
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
