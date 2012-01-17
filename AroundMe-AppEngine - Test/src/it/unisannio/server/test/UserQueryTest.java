package it.unisannio.server.test;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.UserQueryTestHelper;
import it.unisannio.aroundme.server.ServerModelFactory;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserQueryTest extends TestCase {
	private UserQueryTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelFactory.setInstance(new ServerModelFactory());
		helper = new UserQueryTestHelper();
	}
	
	public void testById() {
		helper.testById();
	}
	
	public void testCompatibility() {
		helper.testCompatibility();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
	
	public void testIds() {
		helper.testIds();
	}
	
	public void testInterestIds() {
		helper.testInterestIds();
	}
	
	public void testNeighbourhood() {
		helper.testNeighbourhood();
	}
}
