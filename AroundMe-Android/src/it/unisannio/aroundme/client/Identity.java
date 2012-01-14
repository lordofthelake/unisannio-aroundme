package it.unisannio.aroundme.client;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.facebook.android.Facebook;

import it.unisannio.aroundme.model.*;

/**
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Identity extends User {
	private static final long serialVersionUID = 1L;
	
	private static Identity instance = null;
	
	public static Callable<Identity> login(final Facebook fb) {
		return new Callable<Identity>() {

			@Override
			public Identity call() throws Exception {
				JSONObject me = (JSONObject) new JSONTokener(fb.request("me")).nextValue();
				long id = me.getLong("id");
				String accessToken = fb.getAccessToken();
				synchronized(Identity.class) {
					instance = new Identity(null, accessToken); // Settiamo l'AccessToken
					try {
						instance = new Identity(UserQuery.single(id).call(), accessToken);
					} catch (Exception e) {
						instance = null;
						throw e;
					}
				}
				
				return instance;
			}
			
		};
	}

	
	public static synchronized Identity get() {
		return instance;
	}

	public synchronized static void set(Identity identity) {
		instance = identity;
	}

	
	private final User self;
	private final String accessToken;
	
	protected Identity(User self, String accessToken) {
		this.self = self;
		this.accessToken = accessToken;
	}
	
	public String getAccessToken() {
		return accessToken;
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
		self.setPosition(p);
	}

	@Override
	public Collection<Interest> getInterests() {
		return self.getInterests();
	}
	
	@Override
	public boolean equals(Object obj) {
		return self.equals(obj);
	}

}
