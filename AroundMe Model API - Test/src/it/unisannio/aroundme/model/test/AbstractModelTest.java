package it.unisannio.aroundme.model.test;


import java.lang.reflect.Field;

import junit.framework.TestCase;

import it.unisannio.aroundme.model.Model;
import it.unisannio.aroundme.model.Serializer;


/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class AbstractModelTest<T extends Model>  extends TestCase {
	protected Class<T> clazz;
	
	protected AbstractModelTest(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	
	public void testSerializerFieldPresence() {
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
