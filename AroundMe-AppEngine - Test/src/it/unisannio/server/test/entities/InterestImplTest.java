package it.unisannio.server.test.entities;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import junit.framework.TestCase;

/**
 * {@link TestCase} che verifica che un oggetto di tipo {@link InterestImpl} sia regolarmente
 * reso persistente sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class InterestImplTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private static Objectify ofy = ObjectifyService.begin();
	private Interest interest;
	
	@Override
	public void setUp() {
		helper.setUp();

		ModelFactory.setInstance(new ServerModelFactory());
		
		try{
			ObjectifyService.register(InterestImpl.class);
		}catch(IllegalArgumentException e){}
		
		//L'Interest da persistere nel datastore
		interest = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		
	}
	
	@Override
	public void tearDown(){
		helper.tearDown();
	}
	
	/**
	 * Testa che un {@link InterestImpl} reso persistente sul datastore, conservi tutti i suoi attributi
	 */
	public void testInterestImplPersistence(){
				
		ofy.put(interest); //Salva un Interest sul datastore
		
		Interest retrievedInterest = ofy.get(InterestImpl.class, interest.getId()); //Recupera l'Interest dal Datastore
		
		assertEquals(interest, retrievedInterest);
		assertEquals(interest.getName(), retrievedInterest.getName());
		assertEquals(interest.getCategory(), retrievedInterest.getCategory());	
	}
	
	/**
	 * Testa che un la cancellazione di un {@link InterestImpl} dal datastore vada a buon fine.
	 * Verifica che sia stata lanciata l'eccezione {@link NotFoundException} se si tenta di accedere
	 * ad un {@link InterestImpl} precedentemente eliminato
	 */
	public void testInterestImplDelete(){
		ofy.put(interest); //Salva un Interest sul datastore
	
		ofy.delete(interest); //Cancella l'Interest dal datastore
		
		Exception notFoundException = null;
		try{ 
			ofy.get(InterestImpl.class, interest.getId());
		}catch (NotFoundException e) {
			notFoundException = e;
		}
		assertNotNull(notFoundException);
	}
}