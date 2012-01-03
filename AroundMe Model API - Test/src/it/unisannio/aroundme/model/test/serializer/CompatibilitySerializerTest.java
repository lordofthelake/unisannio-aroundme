package it.unisannio.aroundme.model.test.serializer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import it.unisannio.aroundme.model.*;

public class CompatibilitySerializerTest extends TestCase {
	private Serializer<Compatibility> serializer;
	
	@Before
	public void setUp() {
		serializer = Compatibility.SERIALIZER;
	}
	
	@Test
	public void testDeserialization() throws SAXException, IOException {
		try {
			Compatibility obj = serializer.fromString("<compatibility userid=\"10\" rank=\"0.9\" />");
			
			assertEquals(obj.getRank(), 0.9f, 0);
			assertEquals(obj.getUserId(), 10);
		} catch (RuntimeException rEx) {
			fail("Deserializzazione fallita: " + rEx);
		}
	}
	
	@Test
	public void testSerialization() throws TransformerException, IOException, SAXException {
		Compatibility c = new Compatibility(10, 0.9f);
		assertXMLEqual(serializer.toString(c), "<compatibility userid=\"10\" rank=\"0.9\" />");
	}
}
