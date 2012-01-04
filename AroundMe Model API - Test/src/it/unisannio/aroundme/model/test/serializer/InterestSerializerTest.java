package it.unisannio.aroundme.model.test.serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.model.test.MockHelper;

import static org.easymock.EasyMock.*;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;;

public class InterestSerializerTest extends TestCase {
	private Serializer<Interest> serializer;
	private Interest mockInterest;
	private ModelFactory mockFactory;
	
	public void setUp() {
		serializer = Interest.SERIALIZER;
		mockInterest = MockHelper.createMockInterest(1337L, "Mock interest", "Mock category");

		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createInterest(1337L, "Mock interest", "Mock category"))
			.andReturn(mockInterest);
		replay(mockFactory);
		ModelFactory.setInstance(mockFactory);
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			
			serializer.fromString("<interest id=\"1337\" category=\"Mock category\" name=\"Mock interest\" />");
			
			verify(mockFactory);
		} catch (IllegalArgumentException rEx) {
			fail(rEx.getMessage());
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		assertXMLEqual(serializer.toString(mockInterest), "<interest id=\"1337\" category=\"Mock category\" name=\"Mock interest\" />");	
	}

}
