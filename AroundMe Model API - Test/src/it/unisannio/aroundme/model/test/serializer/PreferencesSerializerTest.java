package it.unisannio.aroundme.model.test.serializer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

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