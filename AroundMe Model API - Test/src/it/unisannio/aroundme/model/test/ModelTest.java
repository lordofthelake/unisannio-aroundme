package it.unisannio.aroundme.model.test;


import java.lang.reflect.Field;

import junit.framework.TestCase;

import it.unisannio.aroundme.model.*;


/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class ModelTest extends TestCase {
	
	public void testCompatibilitySerializerPrecence() {
		testSerializerFieldPresence(Compatibility.class);
	}
	
	public void testInterestSerializerPresence() {
		testSerializerFieldPresence(Interest.class);
	}
	
	public void testNeighbourhoodSerializerPresence() {
		testSerializerFieldPresence(Neighbourhood.class);
	}
	
	public void testPositionSerializerPresence() {
		testSerializerFieldPresence(Position.class);
	}
	
	public void testPreferencesSerializerPresence() {
		testSerializerFieldPresence(Preferences.class);
	}
	
	public void testUserSerializerPresence() {
		testSerializerFieldPresence(User.class);
	}
	
	public void testUserQuerySerializerPresence() {
		testSerializerFieldPresence(UserQuery.class);
	}
	
	protected void testSerializerFieldPresence(Class<? extends Model> clazz) {
		assertNotNull(clazz);
		
		try {
			Field f = clazz.getField("SERIALIZER");
			assertNotNull(f);
			Object o = f.get(null);
			assertTrue(Serializer.class.isInstance(o));
		} catch (NullPointerException e) {
			fail("Field is not static");
		} catch (IllegalAccessException e) {
			fail("Field is not public");
		} catch (SecurityException e) {
			fail("Access not permitted");
		} catch (NoSuchFieldException e) {
			fail("SERIALIZER field not present");
		}
		
	}
}
