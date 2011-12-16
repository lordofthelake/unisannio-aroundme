package it.unisannio.aroundme.client.model;

import java.util.Collection;
import java.util.Collections;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
class UserImpl extends User {
	private static final long serialVersionUID = 1L;
	
	private final Collection<Interest> interests;
	private final String name;
	private final long id;
	
	private Position position;

	UserImpl(long id, String name, Collection<Interest> interests) {
		this.id = id;
		this.name = name;
		this.interests = Collections.unmodifiableCollection(interests);
	}
	@Override
	public long getId() {
		return id;
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
	public void setPosition(Position p) {
		position = p;
	}

	@Override
	public Collection<Interest> getInterests() {
		return interests;
	}

}
