package it.unisannio.aroundme.client.model;

import java.util.Collection;

import it.unisannio.aroundme.client.DataService;
import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
class UserQueryImpl extends UserQuery {
	private static final long serialVersionUID = 1L;
	private DataService service;
	
	UserQueryImpl(DataService service) {
		this.service = service;
	}

	@Override
	public void perform(DataListener<Collection<User>> l) {
		// TODO Auto-generated method stub

	}

}
