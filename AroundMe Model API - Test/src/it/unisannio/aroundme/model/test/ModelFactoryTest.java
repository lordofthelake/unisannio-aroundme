package it.unisannio.aroundme.model.test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class ModelFactoryTest {
	private ModelFactory factory;
	@Before
	public void setUp() throws Exception {
		factory = ModelFactory.getInstance();
	}

	@Test
	public void testGetInstance() {
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
		fail();
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
	public void testCreateInterestQuery() {
		fail();
	}

	@Test
	public void testCreateUserQuery() {
		fail();
	}

}
