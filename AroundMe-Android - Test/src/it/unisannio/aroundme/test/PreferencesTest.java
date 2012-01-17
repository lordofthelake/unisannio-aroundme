package it.unisannio.aroundme.test;

import it.unisannio.aroundme.model.test.helpers.PreferencesTestHelper;
import android.test.AndroidTestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesTest extends AndroidTestCase {
	private PreferencesTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
