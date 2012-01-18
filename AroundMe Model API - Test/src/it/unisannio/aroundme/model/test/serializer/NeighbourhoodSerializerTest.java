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

import static org.easymock.EasyMock.*;

import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.model.test.helpers.MockHelper;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class NeighbourhoodSerializerTest extends TestCase {
	private Serializer<Neighbourhood> serializer;
	private Position mockPosition;
	private Neighbourhood testNeighbourhood;
	private ModelFactory mockFactory;
	
	private String xmlFormat = "<neighbourhood radius=\"987\"><position lat=\"1.23456\" lon=\"-6.54321\" /></neighbourhood>";
	
	public void setUp() {
		serializer = Neighbourhood.SERIALIZER;
		
		mockPosition = MockHelper.createMockPosition(1.23456, -6.54321);
		
		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createPosition(1.23456, -6.54321)).andReturn(mockPosition);
		replay(mockFactory);
		ModelFactory.setInstance(mockFactory);
		
		testNeighbourhood = new Neighbourhood(mockPosition, 987);
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			Neighbourhood obj = serializer.fromString(xmlFormat);
			
			assertEquals(obj.getPosition(), mockPosition);
			assertEquals(obj.getRadius(), 987);
		} catch (IllegalArgumentException rEx) {
			fail(rEx.getMessage());
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		assertXMLEqual(serializer.toString(testNeighbourhood), xmlFormat);
	}
}
