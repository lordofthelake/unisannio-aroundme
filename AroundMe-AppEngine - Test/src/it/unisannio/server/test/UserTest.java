package it.unisannio.server.test;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.UserTestHelper;
import it.unisannio.aroundme.server.ServerModelFactory;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserTest extends TestCase {
	private UserTestHelper helper;
	
	protected void setUp() throws Exception {
		super.setUp();
		ModelFactory.setInstance(new ServerModelFactory());
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
