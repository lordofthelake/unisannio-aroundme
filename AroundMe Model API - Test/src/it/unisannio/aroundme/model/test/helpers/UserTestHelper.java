package it.unisannio.aroundme.model.test.helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;

import static junit.framework.TestCase.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserTestHelper {
	public void testEquals() {
		ModelFactory f = ModelFactory.getInstance();
		Collection<Interest> interestSet1 = Arrays.asList(
				f.createInterest(1, "Guitar", "Music"),
				f.createInterest(2, "Beatles", "Bands")
				
				);
		
		Collection<Interest> interestSet2 = Arrays.asList(
				f.createInterest(3, "Yoko Ono", "Public figures")
				);
		
		User test = f.createUser(123L, "John Lennon", interestSet1);
		User equal = f.createUser(123L, "John Lennon", interestSet1);
		User differentId = f.createUser(321, "John Lennon", interestSet1);
		User differentName = f.createUser(123L, "Lennon John", interestSet1);
		User differentInterests = f.createUser(123L, "John Lennon", interestSet2);
		
		assertTrue(test.equals(equal));
		assertFalse(test.equals(differentId));
		assertFalse(test.equals(differentName));
		assertFalse(test.equals(differentInterests));
	}
	
	public void testPosition() {
		ModelFactory f = ModelFactory.getInstance();
		User test = f.createUser(1, "John Tester", new HashSet<Interest>());
		Position pos = f.createPosition(41.1315992, 14.7779900);
		test.setPosition(pos);
		assertEquals(test.getPosition(), pos);
	}
	
	public void testCompatibilityRank() {
		ModelFactory f = ModelFactory.getInstance();
		Interest guitar = f.createInterest(1, "Guitar", "Music");
		Interest beatles = f.createInterest(2, "Beatles", "Bands");
		Interest drums = f.createInterest(3, "Drums", "Music");
		Interest beliebers = f.createInterest(4, "Beliebers", "Fan Club");
		
		User johnLennon = f.createUser(5, "John Lennon", Arrays.asList(guitar, beatles));
		User paulMcCartney = f.createUser(6, "Paul McCartney", Arrays.asList(guitar, beatles));
		User ringoStarr = f.createUser(7, "Ringo Starr", Arrays.asList(drums, beatles));
		User justinBieber = f.createUser(8, "Justin Bieber", Arrays.asList(beliebers));
		
		assertEquals(johnLennon.getCompatibilityRank(johnLennon), 1.0f);
		assertEquals(johnLennon.getCompatibilityRank(paulMcCartney), 1.0f);
		assertEquals(johnLennon.getCompatibilityRank(ringoStarr), 0.5f);
		assertEquals(johnLennon.getCompatibilityRank(justinBieber), 0.0f);
	}
}
