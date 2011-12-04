package it.unisannio.aroundme.client.model;

import java.util.Collection;

import android.os.AsyncTask;

import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.InterestQuery;

class InterestQueryImpl extends InterestQuery {

	@Override
	public void perform(final DataListener<Collection<Interest>> l) {
		(new AsyncTask<InterestQuery, Integer, Collection<Interest>>() {

			@Override
			protected Collection<Interest> doInBackground(InterestQuery... params) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected void onPostExecute(Collection<Interest> result) {
				l.onData(result);
			}
		}).execute(this);
	}

}
