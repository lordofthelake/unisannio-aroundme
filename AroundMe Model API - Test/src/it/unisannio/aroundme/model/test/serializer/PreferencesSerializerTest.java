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
package it.unisannio.aroundme.model.test.serializer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesSerializerTest extends TestCase {
	private Serializer<Preferences> serializer;
	
	private Preferences mockPreferences;
	private ModelFactory mockFactory;
	
	private String testXml;
	
	public void setUp() {
		serializer = Preferences.SERIALIZER;

		mockPreferences = createMock(Preferences.class);
		
		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createPreferences()).andReturn(mockPreferences);
		replay(mockFactory);
		ModelFactory.setInstance(mockFactory);
		
		testXml = "<preferences>"
				+ "<stringKey type=\"string\">Lorem ipsum dolor</stringKey>"
				+ "<floatKey type=\"float\">-0.9876</floatKey>"
				+ "<doubleKey type=\"double\">1234.5678</doubleKey>"
				+ "<booleanKey type=\"boolean\">true</booleanKey>"
				+ "<intKey type=\"int\">1234</intKey>"
				+ "<longKey type=\"long\">-123456789</longKey>"
				+ "</preferences>";
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			mockPreferences.put("stringKey", "Lorem ipsum dolor");
			mockPreferences.put("floatKey", -0.9876f);
			mockPreferences.put("doubleKey", 1234.5678);
			mockPreferences.put("booleanKey", true);
			mockPreferences.put("intKey", 1234);
			mockPreferences.put("longKey", -123456789L);
			replay(mockPreferences);
			
			serializer.fromString(testXml);
			
			verify(mockFactory);
			verify(mockPreferences);
			
		} catch (IllegalArgumentException rEx) {
			fail("Deserializzazione fallita: " + rEx);
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		Map<String, Object> mockEntries = new HashMap<String, Object>();
		mockEntries.put("stringKey", "Lorem ipsum dolor");
		mockEntries.put("floatKey", -0.9876f);
		mockEntries.put("doubleKey", 1234.5678);
		mockEntries.put("booleanKey", true);
		mockEntries.put("intKey", 1234);
		mockEntries.put("longKey", -123456789L);
		
		expect(mockPreferences.getAll()).andReturn(mockEntries);
		replay(mockPreferences);
		assertXMLEqual(serializer.toString(mockPreferences), testXml);
	}
}