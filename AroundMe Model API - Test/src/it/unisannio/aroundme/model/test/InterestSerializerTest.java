package it.unisannio.aroundme.model.test;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import it.unisannio.aroundme.model.*;

import static org.easymock.EasyMock.*;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;;

public class InterestSerializerTest extends TestCase {
	private Serializer<Interest> serializer;
	
	@Before
	public void setUp() {
		serializer = Interest.SERIALIZER;
	}
	
	private Interest createMockInterest(long id, String name, String category) {
		Interest mockInterest = createMock(Interest.class);
		expect(mockInterest.getCategory()).andReturn(category);
		expect(mockInterest.getName()).andReturn(name);
		expect(mockInterest.getId()).andReturn(id);
		
		replay(mockInterest);
		return mockInterest;
	}
	
	@Test
	public void testDeserialization() throws SAXException, IOException {
		try {
			ModelFactory mockFactory = createMock(ModelFactory.class);
			expect(mockFactory.createInterest(1337L, "Mock interest", "Mock category"))
				.andReturn(createMockInterest(1337L, "Mock interest", "Mock category"));
			replay(mockFactory);
			ModelFactory.setInstance(mockFactory);
			
			serializer.fromString("<interest id=\"1337\" category=\"Mock category\" name=\"Mock interest\" />");
	
			verify(mockFactory);
		} catch (IllegalArgumentException rEx) {
			fail(rEx.getMessage());
		}
	}
	
	@Test
	public void testSerialization() throws TransformerException, IOException, SAXException {
		Interest mockInterest = createMockInterest(1337, "Mock interest", "Mock category");
		
		assertXMLEqual(serializer.toString(mockInterest), "<interest id=\"1337\" category=\"Mock category\" name=\"Mock interest\" />");	
	}

}
