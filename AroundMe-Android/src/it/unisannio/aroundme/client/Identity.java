package it.unisannio.aroundme.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.facebook.android.Facebook;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.SerializerUtils;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

/**
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Identity extends User {
	private static final long serialVersionUID = 1L;
	
	private static Identity instance = null;
	
	public static Callable<Identity> register(final User u, final String auth) {
		HttpTask<Identity> task = new HttpTask<Identity>(null, "PUT", Setup.BACKEND_HOST + Setup.BACKEND_USER_PATH) {
			
			@Override
			protected Identity read(InputStream in) throws Exception {
				return new Identity(u, auth);
			}
			
			@Override
			protected void write(OutputStream out) throws Exception {
				SerializerUtils.writeXML(SerializerUtils.toXML(u), out);
			}
		};

		task.setHeader(Setup.BACKEND_AUTH_HEADER, auth);
		return task;
	}
	
	public static Callable<Identity> login(final long id, final String auth) {
		return new Callable<Identity>() {

			@Override
			public Identity call() throws Exception {
				synchronized(Identity.class) {
					instance = new Identity(null, auth); // Settiamo l'AccessToken
					try {
						instance = new Identity(UserQuery.single(id).call(), auth);
					} catch (Exception e) {
						instance = null;
						throw e;
					}
				}
				
				return instance;
			}
			
		};
	}
	
	public static Callable<User> create(final Facebook fb) {
		return new Callable<User>() {

			@Override
			public User call() throws Exception {
				ModelFactory f = ModelFactory.getInstance();
				
				
				JSONObject likes = (JSONObject) new JSONTokener(fb.request("me/likes")).nextValue();
				JSONArray data = likes.getJSONArray("data");
				
				Collection<Interest> interests = new HashSet<Interest>();
				for(int i = 0, len = data.length(); i < len; ++i) {
					JSONObject like = data.getJSONObject(i);
					Interest interest = f.createInterest(like.getLong("id"), like.getString("name"), like.getString("category"));
					interests.add(interest);
				}
				
				JSONObject me = (JSONObject) new JSONTokener(fb.request("me")).nextValue();
				
				return f.createUser(me.getLong("id"), me.getString("name"), interests);
			}
			
		};
	}
	
	public static synchronized Identity get() {
		return instance;
	}
	
	public static synchronized void set(User u, String auth) {
		instance = new Identity(u, auth);
	}
	
	private final User self;
	private final String facebookAuth;
	
	protected Identity(User self, String facebookAuth) {
		this.self = self;
		this.facebookAuth = facebookAuth;
	}
	
	public String getAccessToken() {
		return facebookAuth;
	}
	
	@Override
	public long getId() {
		return self.getId();
	}

	@Override
	public String getName() {
		return self.getName();
	}

	@Override
	public Position getPosition() {
		return self.getPosition();
	}

	public void setPosition(Position p) {
		// FIXME update network position
	}

	@Override
	public Collection<Interest> getInterests() {
		return self.getInterests();
	}

}
