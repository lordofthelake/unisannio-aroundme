package it.unisannio.aroundme.model.test;

import static org.junit.Assert.*;
import it.unisannio.aroundme.model.ModelFactory;

import org.junit.Before;
import org.junit.Test;

public abstract class ModelFactoryTest {
	private ModelFactory factory;
	@Before
	public void setUp() throws Exception {
		factory = ModelFactory.getInstance();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(ModelFactory.getInstance());
	}

	@Test
	public void testCreateUser() {
		assertNotNull(factory.createUser());
	}

	@Test
	public void testCreateInterest() {
		assertNotNull(factory.createInterest());
	}

	@Test
	public void testCreatePosition() {
		assertNotNull(factory.createPosition());
	}


	@Test
	public void testCreateInterestQuery() {
		assertNotNull(factory.createInterestQuery());
	}

	@Test
	public void testCreateUserQuery() {
		assertNotNull(factory.createUserQuery());
	}

	@Test
	public void testCreateNeighbourhood() {
		assertNotNull(factory.createNeighbourhood());
	}

	@Test
	public void testCreateCompatibility() {
		assertNotNull(factory.createCompatibility());
	}

}
