package it.unisannio.aroundme.client.model;

import java.util.Collection;

import it.unisannio.aroundme.client.DataService;
import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.InterestQuery;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
class InterestQueryImpl extends InterestQuery {
	private static final long serialVersionUID = 1L;
	private DataService service;
	
	InterestQueryImpl(DataService service) {
		this.service = service;
	}

	@Override
	public void perform(DataListener<Collection<? extends Interest>> l) {
		// TODO Auto-generated method stub

	}

}
