package it.unisannio.aroundme.model.test.serializer;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.model.test.MockHelper;

public class UserSerializerTest extends TestCase {
	private Serializer<User> serializer;
	private ModelFactory mockFactory;
	private User mockUser;
	private Capture<Collection<Interest>> interestsCapture;
	private Capture<Position> positionCapture;
	private Interest mockInterest1;
	private Interest mockInterest2;
	private Position mockPosition;
	
	private String xml;
	
	public void setUp() {
		serializer = User.SERIALIZER;
		mockInterest1 = MockHelper.createMockInterest(1, "Test Interest 1", "Other");
		mockInterest2 = MockHelper.createMockInterest(2, "Test Interest 2", "Category");
		Collection<Interest> interests = Arrays.asList(mockInterest1, mockInterest2);
		
		mockPosition = MockHelper.createMockPosition(14.3, -13.206);
		positionCapture = new Capture<Position>();
		
		mockUser = createMock(User.class);
		expect(mockUser.getId()).andReturn(1234567L);
		expect(mockUser.getInterests()).andReturn(interests);
		expect(mockUser.getName()).andReturn("Test User");
		expect(mockUser.getPosition()).andReturn(mockPosition);
		mockUser.setPosition(capture(positionCapture));
		
		replay(mockUser);
		
		interestsCapture = new Capture<Collection<Interest>>();
		mockFactory = createMock(ModelFactory.class);
		expect(mockFactory.createUser(eq(1234567L), eq("Test User"), capture(interestsCapture))).andReturn(mockUser);
		expect(mockFactory.createInterest(1, "Test Interest 1", "Other")).andReturn(mockInterest1);
		expect(mockFactory.createInterest(2, "Test Interest 2", "Category")).andReturn(mockInterest2);
		expect(mockFactory.createPosition(14.3, -13.206)).andReturn(mockPosition);
		replay(mockFactory);
		
		ModelFactory.setInstance(mockFactory);
		
		xml = "<user id=\"1234567\" name=\"Test User\">"
				+ "<interests>"
				+ "<interest id=\"1\" name=\"Test Interest 1\" category=\"Other\" />"
				+ "<interest id=\"2\" name=\"Test Interest 2\" category=\"Category\" />"
				+ "</interests>"
				+ "<position lat=\"14.3\" lon=\"-13.206\" />"
				+ "</user>";
	}
	
	public void testDeserialization() throws SAXException, IOException {
		try {
			serializer.fromString(xml);
			
			verify(mockFactory);
			
			Collection<Interest> interests = interestsCapture.getValue();
			assertEquals(interests.size(), 2);
			assertTrue(interests.contains(mockInterest1));
			assertTrue(interests.contains(mockInterest2));
			
			assertTrue(positionCapture.hasCaptured());
			assertEquals(positionCapture.getValue(), mockPosition);
		} catch (RuntimeException rEx) {
			fail("Deserializzazione fallita: " + rEx);
		}
	}
	
	public void testSerialization() throws TransformerException, IOException, SAXException {
		assertXMLEqual(serializer.toString(mockUser), xml);
	}
}