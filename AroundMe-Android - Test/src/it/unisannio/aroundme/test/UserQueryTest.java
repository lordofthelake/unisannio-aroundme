package it.unisannio.aroundme.test;

import it.unisannio.aroundme.model.test.helpers.UserQueryTestHelper;
import android.test.AndroidTestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserQueryTest extends AndroidTestCase {
	private UserQueryTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
