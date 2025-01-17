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
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.model.test.helpers.MockHelper;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserQueySerializerTest extends TestCase {
	private Serializer<UserQuery> serializer;
	
	private ModelFactory mockFactory;
	private UserQuery mockUserQuery;
	private Neighbourhood mockNeighbourhood;
	private Position mockPosition;
	private Compatibility mockCompatibility;
	
	private Collection<Long> testIds;
	private Collection<Long> testInterestIds;
	
	private String testXml;
	
	public void setUp() {
		serializer = UserQuery.SERIALIZER;
		
		mockPosition = MockHelper.createMockPosition(19.9, -17.2);
		mockNeighbourhood = new Neighbourhood(mockPosition, 123456);
		mockCompatibility = new Compatibility(12345, 0.6f);
		
		testIds = Arrays.asList(1L, 2L, 3L);
		testInterestIds = Arrays.asList(4L, 5L, 6L);
		
		mockUserQuery = createMockBuilder(UserQuery.class)
				.addMockedMethod("call")
				.withConstructor()
				.createMock();
		replay(mockUserQuery);
		
		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createPosition(19.9, -17.2)).andReturn(mockPosition);
		expect(mockFactory.createUserQuery()).andReturn(mockUserQuery);
		replay(mockFactory);
		
		ModelFactory.setInstance(mockFactory);
		
		testXml = "<query>"
				+ "<compatibility userid=\"12345\" rank=\"0.6\" />"
				+ "<neighbourhood radius=\"123456\">"
				+ "<position lat=\"19.9\" lon=\"-17.2\" />"
				+ "</neighbourhood>"
				+ "<interest-ids>"
				+ "<id>4</id><id>5</id><id>6</id>"
				+ "</interest-ids>"
				+ "<ids>"
				+ "<id>1</id><id>2</id><id>3</id>"
				+ "</ids>"
				+ "</query>";
		
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			UserQuery obj = serializer.fromString(testXml);
			verify(mockFactory);
			
			assertNotNull(obj);
			
			assertNotNull(obj.getCompatibility());
			assertEquals(obj.getCompatibility(),mockCompatibility);
			
			assertNotNull(obj.getNeighbourhood());
			assertEquals(obj.getNeighbourhood(), mockNeighbourhood);
			
			assertNotNull(obj.getIds());
			assertEquals(obj.getIds().size(), testIds.size());
			assertTrue(obj.getIds().containsAll(testIds));
			
			assertNotNull(obj.getInterestIds());
			assertEquals(obj.getInterestIds().size(), testInterestIds.size());
			assertTrue(obj.getInterestIds().containsAll(testInterestIds));
			
		} catch (IllegalArgumentException rEx) {
			fail("Deserializzazione fallita: " + rEx);
			rEx.printStackTrace();
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		mockUserQuery.addId(testIds);
		mockUserQuery.addInterestId(testInterestIds);
		mockUserQuery.setCompatibility(mockCompatibility);
		mockUserQuery.setNeighbourhood(mockNeighbourhood);
		
		assertXMLEqual(serializer.toString(mockUserQuery), testXml);
	}
}
