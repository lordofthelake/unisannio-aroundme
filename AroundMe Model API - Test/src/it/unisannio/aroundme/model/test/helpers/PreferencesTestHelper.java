/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.model.test.helpers;

import java.util.HashMap;
import java.util.Map;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Preferences;

import static junit.framework.TestCase.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesTestHelper {
	
	
	public void testContains() {
		Preferences test = ModelFactory.getInstance().createPreferences();
		test.put("testKey", "testValue");
		assertTrue(test.contains("testKey"));
		assertFalse(test.contains("nonExistingKey"));
	}
	
	public void testString() {
		Preferences test = ModelFactory.getInstance().createPreferences();
		test.put("testKey", "testValue");
		assertEquals(test.get("testKey", "aDefaultValue"), "testValue");
		assertEquals(test.get("nonExistingKey", "defaultValue"), "defaultValue");
		try {
			test.get("testKey", 0);
			fail("Should result in a ClassCastException");
		} catch (ClassCastException e) {}
		
		try {
			test.get("testKey", false);
			fail("Should result in a ClassCastException");
		} catch (ClassCastException e) {}
		
	}
	
	public void testBoolean() {
		Preferences test = ModelFactory.getInstance().createPreferences();
		test.put("testKey", true);
		assertEquals(test.get("testKey", false), true);
		assertEquals(test.get("nonExistingKey", false), false);
		try {
			test.get("testKey", 0);
			fail("Should result in a ClassCastException");
		} catch (ClassCastException e) {}
		
		try {
			test.get("testKey", "defaultValue");
			fail("Should result in a ClassCastException");
		} catch (ClassCastException e) {}
	}
	
	public void testNumber() {
		Preferences test = ModelFactory.getInstance().createPreferences();
		test.put("testKey", 123.456);

		assertEquals(test.get("nonExistingKey", 0), 0);
		assertEquals(test.get("testKey", 0), 123);
		assertEquals(test.get("testKey", 0.0), 123.456);
		try {
			test.get("testKey", "defaultValue");
			fail("Should result in a ClassCastException");
		} catch (ClassCastException e) {}
		
		try {
			test.get("testKey", false);
			fail("Should result in a ClassCastException");
		} catch (ClassCastException e) {}
	}
	
	public void testGetAll() {
		Preferences test = ModelFactory.getInstance().createPreferences();
		test.put("stringKey", "stringValue");
		test.put("longKey", 123L);
		test.put("floatKey", 123.456f);
		test.put("intKey", 456);
		test.put("doubleKey", 456.123);
		test.put("booleanKey", true);
		
		Map<String, Object> all = test.getAll();
		assertEquals(all.size(), 6);
		assertEquals(all.get("stringKey"), "stringValue");
		assertEquals(all.get("longKey"), 123L);
		assertEquals(all.get("floatKey"), 123.456f);
		assertEquals(all.get("intKey"), 456);
		assertEquals(all.get("doubleKey"), 456.123);
		assertEquals(all.get("booleanKey"), true);
	}
	
	public void testPutAll() {
		Preferences test = ModelFactory.getInstance().createPreferences();
		Map<String, Object> all = new HashMap<String, Object>();
		all.put("stringKey", "stringValue");
		all.put("longKey", 123L);
		all.put("floatKey", 123.456f);
		all.put("intKey", 456);
		all.put("doubleKey", 456.123);
		all.put("booleanKey", true);
		
		test.putAll(all);
		assertEquals(test.get("stringKey", null), "stringValue");
		assertEquals(test.get("longKey", 0L), 123L);
		assertEquals(test.get("floatKey", 0.0f), 123.456f);
		assertEquals(test.get("intKey", 0), 456);
		assertEquals(test.get("doubleKey", 0.0), 456.123);
		assertEquals(test.get("booleanKey", false), true);
		
	}
	
}
