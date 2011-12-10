package it.unisannio.aroundme.server;

import java.util.Collection;

import com.google.gwt.dev.util.collect.HashSet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.InterestQuery;

public class InterestQueryImpl extends InterestQuery{
	private static final long serialVersionUID = 1L;

	@Override
	public void perform(DataListener<Collection<Interest>> l) {
		try{
			Objectify ofy = ObjectifyService.begin();			
			//Inizializza queriedInterests aggiungendovi tutti gli Interest presenti sul Datastore
			Collection<Interest> queriedInterests = ofy.get(Interest.class).values();
			
			if(this.getInterestIds() != null){
				HashSet<Long> interestsKeys = new HashSet<Long>(this.getInterestIds());
				queriedInterests = ofy.get(Interest.class, interestsKeys).values();
			}	
			
			l.onData(queriedInterests);
		}catch (Exception e) {
			l.onError(e);
		}
	}

}
