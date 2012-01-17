package it.unisannio.server.test;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.PositionTestHelper;
import it.unisannio.aroundme.server.ServerModelFactory;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PositionTest extends TestCase {
	private PositionTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelFactory.setInstance(new ServerModelFactory());
		helper = new PositionTestHelper();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
	
	public void testGetDistance() {
		helper.testGetDistance();
	}
}
