package it.unisannio.server.test;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.PreferencesTestHelper;
import it.unisannio.aroundme.server.ServerModelFactory;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesTest extends TestCase {
	private PreferencesTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelFactory.setInstance(new ServerModelFactory());
		helper = new PreferencesTestHelper();
	}
	
	public void testBoolean() {
		helper.testBoolean();
	}
	
	public void testContains() {
		helper.testContains();
	}
	
	public void testGetAll() {
		helper.testGetAll();
	}
	
	public void testNumber() {
		helper.testNumber();
	}
	
	public void testPutAll() {
		helper.testPutAll();
	}
	
	public void testString() {
		helper.testString();
	}
}
