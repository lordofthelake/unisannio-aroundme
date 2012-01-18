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

import java.io.IOException;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import it.unisannio.aroundme.model.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class CompatibilitySerializerTest extends TestCase {
	private Serializer<Compatibility> serializer;
	
	public void setUp() {
		serializer = Compatibility.SERIALIZER;
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			Compatibility obj = serializer.fromString("<compatibility userid=\"10\" rank=\"0.9\" />");
			
			assertEquals(obj.getRank(), 0.9f, 0);
			assertEquals(obj.getUserId(), 10);
		} catch (RuntimeException rEx) {
			fail("Deserializzazione fallita: " + rEx);
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		Compatibility c = new Compatibility(10, 0.9f);
		assertXMLEqual(serializer.toString(c), "<compatibility userid=\"10\" rank=\"0.9\" />");
	}
}
