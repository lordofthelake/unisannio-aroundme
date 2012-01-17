package it.unisannio.server.test;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.ModelFactoryTestHelper;
import it.unisannio.aroundme.server.ServerModelFactory;
import junit.framework.TestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class ModelFactoryTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		ModelFactory.setInstance(new ServerModelFactory());
	}

	
	public void testModelFactoryState() {
		assertNotNull(ModelFactory.getInstance());
	}
	
	public void testCreateUser() {
		ModelFactoryTestHelper.testCreateUser(ModelFactory.getInstance());
	}
	
	public void testCreateInterest() {
		ModelFactoryTestHelper.testCreateInterest(ModelFactory.getInstance());
	}
	
	public void testCreatePosition() {
		ModelFactoryTestHelper.testCreatePosition(ModelFactory.getInstance());
	}
	
	public void testCreatePreferences() {
		ModelFactoryTestHelper.testCreatePreferences(ModelFactory.getInstance());
	}
	
	public void testCreateUserQuery() {
		ModelFactoryTestHelper.testCreateUserQuery(ModelFactory.getInstance());
	}
}
