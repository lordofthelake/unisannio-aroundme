package it.unisannio.aroundme.model.test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.easymock.EasyMock.*;

import it.unisannio.aroundme.model.*;

public class NeighbourhoodSerializerTest extends TestCase {
	private Serializer<Neighbourhood> serializer;
	private Position mockPosition;
	private Neighbourhood testNeighbourhood;
	private ModelFactory mockFactory;
	
	private String xmlFormat = "<neighbourhood radius=\"987.654321\"><position lat=\"1.23456\" lon=\"-6.54321\" /></neighbourhood>";
	
	@Before
	public void setUp() {
		serializer = Neighbourhood.SERIALIZER;
		
		mockPosition = createMock(Position.class);
		expect(mockPosition.getLatitude()).andReturn(1.23456);
		expect(mockPosition.getLongitude()).andReturn(-6.54321);
		replay(mockPosition);
		
		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createPosition(1.23456, -6.54321)).andReturn(mockPosition);
		replay(mockFactory);
		ModelFactory.setInstance(mockFactory);
		
		testNeighbourhood = new Neighbourhood(mockPosition, 987.654321);
	}
	
	@Test
	public void testDeserialization() throws SAXException, IOException {
		try {
			Neighbourhood obj = serializer.fromString(xmlFormat);
			
			assertEquals(obj.getPosition(), mockPosition);
			assertEquals(obj.getRadius(), 987.654321);
		} catch (IllegalArgumentException rEx) {
			fail(rEx.getMessage());
		}
	}
	
	@Test
	public void testSerialization() throws TransformerException, IOException, SAXException {
		assertXMLEqual(serializer.toString(testNeighbourhood), xmlFormat);
	}
}
