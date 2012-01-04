package it.unisannio.aroundme.model.test;


import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;


/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class AbstractModelFactoryTest extends TestCase {
	private ModelFactory factory;
	
	protected AbstractModelFactoryTest(ModelFactory f) {
		this.factory = f;
	}

	public void testInstance() {
		assertNotNull(factory);
	}

	public void testCreateUser() {
		long id = Math.round(Math.random()*10000);
		String name = String.valueOf(Math.random()*1000000);
		Collection<Interest> interests = new HashSet<Interest>();
		
		int num = Math.round(Math.round(Math.random()*20));
		while(num--> 0) {
			long iid = Math.round(Math.random() * 10000);
			interests.add(factory.createInterest(iid, "Interest #" + iid, "Category #" + iid));
		}
		
		User user = factory.createUser(id, name, interests);
		
		assertNotNull(user);
		assertEquals(user.getId(), id);
		assertEquals(user.getName(), name);
		assertEquals(user.getInterests(), interests);
	}

	public void testCreateInterest() {
		long id = Math.round(Math.random()*10000);
		String name = String.valueOf(Math.random()*1000000);
		String category = String.valueOf(Math.random()*1000000);
		
		Interest i = factory.createInterest(id, name, category);
		
		assertNotNull(i);
		assertEquals(i.getId(), id);
		assertEquals(i.getName(), name);
		assertEquals(i.getCategory(), category);
	}

	public void testCreatePosition() {
		double latitude = Math.random() * 10;
		double longitude = Math.random() * 10;
		
		Position p = factory.createPosition(latitude, longitude);
		
		assertNotNull(p);
		
		assertEquals(p.getLatitude(), latitude, 0);
		assertEquals(p.getLongitude(), longitude, 0);
	}

	public void testCreateUserQuery() {
		UserQuery query = factory.createUserQuery();
		
		assertNotNull(query);
	}
	
	public void testCreatePreferences() {
		Preferences preferences = factory.createPreferences();
		
		assertNotNull(preferences);
	}

}
