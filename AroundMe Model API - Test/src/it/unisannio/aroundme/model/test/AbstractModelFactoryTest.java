package it.unisannio.aroundme.model.test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import org.junit.Before;
import org.junit.Test;

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

	@Test
	public void testInstance() {
		assertNotNull(factory);
	}

	@Test
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

	@Test
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

	@Test
	public void testCreatePosition() {
		double latitude = Math.random() * 10;
		double longitude = Math.random() * 10;
		
		Position p = factory.createPosition(latitude, longitude);
		
		assertNotNull(p);
		
		assertEquals(p.getLatitude(), latitude, 0);
		assertEquals(p.getLongitude(), longitude, 0);
	}

	@Test
	public void testCreateUserQuery() {
		UserQuery query = factory.createUserQuery();
		
		assertNotNull(query);
	}
	
	@Test
	public void testCreatePreferences() {
		Preferences preferences = factory.createPreferences();
		
		assertNotNull(preferences);
	}

}
