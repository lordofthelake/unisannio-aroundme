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
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.test.helpers.MockHelper;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PositionSerializerTest extends TestCase {
	private Serializer<Position> serializer;
	private ModelFactory mockFactory;
	private Position mockPosition;
	
	public void setUp() {
		serializer = Position.SERIALIZER;
		mockPosition = MockHelper.createMockPosition(0.9, -0.9);
		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createPosition(0.9, -0.9)).andReturn(mockPosition);
		replay(mockFactory);
		ModelFactory.setInstance(mockFactory);
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			serializer.fromString("<position lat=\"0.9\" lon=\"-0.9\" />");
			verify(mockFactory);
			
		} catch (RuntimeException rEx) {
			fail("Deserializzazione fallita: " + rEx);
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		
		assertXMLEqual(serializer.toString(mockPosition), "<position lat=\"0.9\" lon=\"-0.9\" />");
	}
}
