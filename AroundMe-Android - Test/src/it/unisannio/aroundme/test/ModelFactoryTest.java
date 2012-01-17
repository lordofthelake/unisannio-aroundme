package it.unisannio.aroundme.test;

import android.test.AndroidTestCase;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.test.helpers.ModelFactoryTestHelper;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class ModelFactoryTest extends AndroidTestCase {
	
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
