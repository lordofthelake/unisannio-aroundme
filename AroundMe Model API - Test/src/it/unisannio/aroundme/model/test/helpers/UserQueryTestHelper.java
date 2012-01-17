package it.unisannio.aroundme.model.test.helpers;

import java.util.Arrays;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.UserQuery;

import static junit.framework.TestCase.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserQueryTestHelper {
	public void testById() {
		UserQuery byId = UserQuery.byId(123L, 456L);
		UserQuery byId2 = UserQuery.byId(Arrays.asList(123L, 456L));
		assertEquals(byId, byId2);
		
		assertNull(byId.getCompatibility());
		assertNull(byId.getNeighbourhood());
		assertTrue(byId.getInterestIds().isEmpty());
		assertEquals(byId.getIds().size(), 2);
		assertTrue(byId.getIds().contains(123L));
		assertTrue(byId.getIds().contains(456L));
	}
	
	public void testIds() {
		UserQuery test = ModelFactory.getInstance().createUserQuery();
		test.addId(123L);
		test.addId(456L);
		assertEquals(test.getIds().size(), 2);
		assertTrue(test.getIds().contains(123L));
		assertTrue(test.getIds().contains(456L));
		test.removeId(456L);
		assertFalse(test.getIds().contains(456L));
	}
	
	public void testInterestIds() {
		UserQuery test = ModelFactory.getInstance().createUserQuery();
		test.addInterestId(123L);
		test.addInterestId(456L);
		assertEquals(test.getInterestIds().size(), 2);
		assertTrue(test.getInterestIds().contains(123L));
		assertTrue(test.getInterestIds().contains(456L));
		test.removeInterestId(456L);
		assertFalse(test.getInterestIds().contains(456L));
	}
	
	public void testCompatibility() {
		UserQuery test = ModelFactory.getInstance().createUserQuery();
		Compatibility c = new Compatibility(123, 0.98f);
		test.setCompatibility(c);
		assertEquals(test.getCompatibility(), c);
	}
	
	public void testNeighbourhood() {
		ModelFactory f = ModelFactory.getInstance();
		UserQuery test = f.createUserQuery();
		Neighbourhood n = new Neighbourhood(f.createPosition(23.45, -54.321), 987);
		test.setNeighbourhood(n);
		assertEquals(test.getNeighbourhood(), n);
	}
	
	public void testEquals() {
		ModelFactory f = ModelFactory.getInstance();
		
		Compatibility c1 = new Compatibility(123, 0.9f);
		Compatibility c2 = new Compatibility(456, 0.5f);
		Neighbourhood n1 = new Neighbourhood(f.createPosition(56.32, -89.012), 456);
		Neighbourhood n2 = new Neighbourhood(f.createPosition(-23.654, 21.0987), 321);
		
		UserQuery test = f.createUserQuery()
				.setCompatibility(c1)
				.setNeighbourhood(n1)
				.addInterestId(123, 456)
				.addId(987, 654);
		UserQuery equal = f.createUserQuery()
				.setCompatibility(c1)
				.setNeighbourhood(n1)
				.addInterestId(123, 456)
				.addId(987, 654);
		UserQuery differentCompatibility = f.createUserQuery()
				.setCompatibility(c2)
				.setNeighbourhood(n1)
				.addInterestId(123, 456)
				.addId(987, 654);
		UserQuery differentNeighbourhood = f.createUserQuery()
				.setCompatibility(c1)
				.setNeighbourhood(n2)
				.addInterestId(123, 456)
				.addId(987, 654);
		UserQuery differentInterestIds = f.createUserQuery()
				.setCompatibility(c1)
				.setNeighbourhood(n1)
				.addInterestId(792, 548)
				.addId(987, 654);
		UserQuery differentIds = f.createUserQuery()
				.setCompatibility(c1)
				.setNeighbourhood(n1)
				.addInterestId(123, 456)
				.addId(546, 987);
		
		assertTrue(test.equals(equal));
		assertFalse(test.equals(differentCompatibility));
		assertFalse(test.equals(differentNeighbourhood));
		assertFalse(test.equals(differentIds));
		assertFalse(test.equals(differentInterestIds));
	}
}
