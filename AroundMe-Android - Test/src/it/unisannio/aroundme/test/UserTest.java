package it.unisannio.aroundme.test;

import android.test.AndroidTestCase;
import it.unisannio.aroundme.model.test.helpers.UserTestHelper;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserTest extends AndroidTestCase {
	private UserTestHelper helper;
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new UserTestHelper();
	}
	
	public void testCompatibilityRank() {
		helper.testCompatibilityRank();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
	
	public void testPosition() {
		helper.testPosition();
	}

}
