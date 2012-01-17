package it.unisannio.server.test;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.NeighbourhoodTestHelper;
import it.unisannio.aroundme.server.ServerModelFactory;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class NeighbourhoodTest extends TestCase {
	private NeighbourhoodTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelFactory.setInstance(new ServerModelFactory());
		helper = new NeighbourhoodTestHelper();
	}
	
	public void testGetters() {
		helper.testGetters();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
}
