package it.unisannio.aroundme.test;

import it.unisannio.aroundme.model.test.helpers.PositionTestHelper;
import android.test.AndroidTestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PositionTest extends AndroidTestCase {
	private PositionTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		helper = new PositionTestHelper();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
	
	public void testGetDistance() {
		helper.testGetDistance();
	}
}
