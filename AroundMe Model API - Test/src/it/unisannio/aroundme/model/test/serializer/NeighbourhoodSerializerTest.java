package it.unisannio.aroundme.model.test.serializer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.easymock.EasyMock.*;

import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.model.test.MockHelper;

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
