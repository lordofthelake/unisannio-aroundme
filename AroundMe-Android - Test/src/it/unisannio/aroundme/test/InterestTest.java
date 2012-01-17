package it.unisannio.aroundme.test;

import it.unisannio.aroundme.model.test.helpers.InterestTestHelper;
import android.test.AndroidTestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class InterestTest extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testEquals() {
		new InterestTestHelper().testEquals();
	}

}
