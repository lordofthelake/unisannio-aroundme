package it.unisannio.aroundme.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Callable;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

public class Identity extends User {
	private static final long serialVersionUID = 1L;
	private static final URL endpoint = null; // FIXME
	
	private static Identity instance = null;
	
	public static Callable<Identity> register(User u, String auth) {
		return new HttpTask<Identity>("PUT", endpoint) {

			@Override
			protected Identity read(InputStream in) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected void write(OutputStream out) throws Exception {
				// TODO Auto-generated method stub
				super.write(out);
			}
		};
	}
	
	public static Callable<Identity> login(final long id, final String auth) {
		return new Callable<Identity>() {

			@Override
			public Identity call() throws Exception {
				// FIXME Verify auth
				User u = UserQuery.single(id).call();
				instance = new Identity(u, auth);
				
				return instance;
			}
			
		};
	}
	
	public static Identity get() {
		return instance;
	}
	
	private User self;
	private String facebookAuth;
	
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
