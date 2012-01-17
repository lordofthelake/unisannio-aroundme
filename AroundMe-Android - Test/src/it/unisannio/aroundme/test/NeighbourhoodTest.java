package it.unisannio.aroundme.test;

import it.unisannio.aroundme.model.test.helpers.NeighbourhoodTestHelper;
import android.test.AndroidTestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class NeighbourhoodTest extends AndroidTestCase {
	private NeighbourhoodTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		helper = new NeighbourhoodTestHelper();
	}
	
	public void testGetters() {
		helper.testGetters();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
}
