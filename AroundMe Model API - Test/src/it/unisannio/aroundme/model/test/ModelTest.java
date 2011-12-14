package it.unisannio.aroundme.model.test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import it.unisannio.aroundme.model.Model;
import it.unisannio.aroundme.model.Serializer;

import org.junit.Test;

public abstract class ModelTest {
	protected Class<? extends Model> clazz;
	
	protected ModelTest(Class<? extends Model> clazz) {
		this.clazz = clazz;
	}
	
	@Test
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
