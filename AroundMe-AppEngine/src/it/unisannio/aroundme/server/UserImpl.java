package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Embedded;
import javax.persistence.Id;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;

/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
@Indexed
public class UserImpl implements User{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1270045595803902572L;

	@Id
	private long id;	
	
	@Unindexed
	private String name;
	
	
	@Embedded
	private Position position;	
	
	private ArrayList<Key<Interest>> interestsKeys; 

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}


	@Override
	public void addInterest(Interest interest) {
		Objectify ofy = ObjectifyService.begin();
		if(ofy.get(Interest.class, interest.getId()) == null)			
			ofy.put(interest);
		interestsKeys.add(new Key<Interest>(Interest.class, interest.getId()));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Position getPosition() {
		return position;
	}


	@Override
	public Collection<Interest> getInterests() {
		Objectify ofy = ObjectifyService.begin();
		return ofy.get(interestsKeys).values();
	}
	
	protected Collection<Key<Interest>> getInterestsKey(){
		return interestsKeys;
	}
	
}
