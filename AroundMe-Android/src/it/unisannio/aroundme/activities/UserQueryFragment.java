/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.activities;

import java.util.ArrayList;
import java.util.List;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.adapters.ArrayPagerAdapter;
import it.unisannio.aroundme.adapters.InterestFilterAdapter;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.widgets.SliderView;
import it.unisannio.aroundme.widgets.SliderView.OnChangeListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

/**
 * Fragment con il compito di permettere l'editing di una {@link UserQuery}.
 * 
 * Graficamente, si presenta come un "cassetto" ({@code SlidingDrawer}) che permette di modificare i parametri della query attraverso
 * vari slider e una lista di interessi.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class UserQueryFragment extends Fragment implements OnDrawerCloseListener, OnDrawerOpenListener {
	
	/**
	 * Listener che viene notificato quando la query viene modificata dall'utente.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	public static interface OnQueryChangeListener {
		/**
		 * Metodo che viene notificato a seguito di una modifica alla query.
		 * 
		 * @param query la UserQuery modificata
		 */
		void onQueryChanged(UserQuery query);
	};
	
	private AsyncQueue async;
	private UserQuery userQuery;
	private Identity me;
	
	private SlidingDrawer drawer;
	private ImageView icDrawer;

	private SliderView distance;
	private SliderView rank;
	private InterestFilterAdapter interestFilterAdapter;
	
	private OnDrawerCloseListener onDrawerCloseListener;
	private OnDrawerOpenListener onDrawerOpenListener;
	
	private OnQueryChangeListener onQueryChangeListener;
	
	private List<Interest> myInterests;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		me = Identity.get();
		myInterests = new ArrayList<Interest>(me.getInterests());
    	async = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
    	
    	userQuery = ModelFactory.getInstance().createUserQuery();

    	Log.d("UserQueryFragment", "Creating UserQuery from default values");

		
		Position position = Identity.get().getPosition();
		Neighbourhood neighbourhood = new Neighbourhood(position, Setup.FILTERS_DEFAULT_RADIUS); 
		userQuery.setCompatibility(new Compatibility(Identity.get().getId(), Setup.FILTERS_DEFAULT_RANK)); 
		
		userQuery.setNeighbourhood(neighbourhood);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        drawer = (SlidingDrawer) inflater.inflate(R.layout.filters_drawer, container, false);
        drawer.setOnDrawerCloseListener(this);
        drawer.setOnDrawerOpenListener(this);
        icDrawer = (ImageView) drawer.findViewById(R.id.filterIcon);
        
        ViewPager pager = (ViewPager) drawer.findViewById(R.id.pager);
        
        View page1 = inflater.inflate(R.layout.filters_page_nearby, null);
        View page2 = inflater.inflate(R.layout.filters_page_interests, null);
        pager.setAdapter(new ArrayPagerAdapter(page1, page2));
        
        
        distance = (SliderView) page1.findViewById(R.id.sliderDistance);
        distance.setMultipliedValue((int) userQuery.getNeighbourhood().getRadius());
        distance.setOnChangeListener(new OnChangeListener() {

			@Override
			public void onSliderChanged(SliderView view) {
				Position position = Identity.get().getPosition();
				Neighbourhood neighbourhood = new Neighbourhood(position, view.getMultipliedValue());	
				userQuery.setNeighbourhood(neighbourhood);
				notifyQueryChanged();
			}
		});
        
        rank = (SliderView) page1.findViewById(R.id.sliderCompatibility);
        rank.setConvertedValue(userQuery.getCompatibility().getRank());
        rank.setOnChangeListener(new OnChangeListener() {
			
			@Override
			public void onSliderChanged(SliderView view) {
				long id = Identity.get().getId();
				Compatibility compatibility = new Compatibility(id, view.getConvertedValue());
				userQuery.setCompatibility(compatibility);
				notifyQueryChanged();
			}
		});
        
        ListView interestsFilter = (ListView) page2.findViewById(R.id.listInterestFilter);
        interestFilterAdapter = new InterestFilterAdapter(this, myInterests, async);
        interestsFilter.setAdapter(interestFilterAdapter); 
        
        return drawer;
	}
	
	/**
	 * Segnala al fragment che &egrave; avvenuta una modifica alla query e che pertanto deve aggiornare il proprio stato
	 * e notificare i listener eventualmente associati
	 * 
	 * @see OnQueryChangeListener
	 */
	public void notifyQueryChanged() {
		interestFilterAdapter.notifyDataSetChanged();
    	if(onQueryChangeListener != null)
    		onQueryChangeListener.onQueryChanged(userQuery);
	}
	
	/**
	 * Restituisce la UserQuery associata a questo Fragment nel suo stato corrente.
	 * 
	 * @return la query nel suo stato attuale
	 */
	public UserQuery getUserQuery() {
		return userQuery;
	}
	
	/**
	 * Imposta il listener che ricver&agrave; le notifiche a seguito dei cambiamenti della UserQuery.
	 * 
	 * @param l il listener da associare a questo fragment
	 */
	public void setOnQueryChangeListener(OnQueryChangeListener l) {
		this.onQueryChangeListener = l;
	}
	
	/**
	 * Imposta un listener che ricever&agrave; le notifiche a seguito dell'apertura del cassetto.
	 * 
	 * @param l il listener da associare all'apertura del cassetto
	 */
	public void setOnDrawerOpenListener(OnDrawerOpenListener l) {
		this.onDrawerOpenListener = l;
	}
	
	/**
	 * Imposta un listener che ricever&agrave; le notifiche a seguito della chiusura del cassetto.
	 * 
	 * @param l il listener da associare alla chiusura del cassetto
	 */
	public void setOnDrawerCloseListener(OnDrawerCloseListener l) {
		this.onDrawerCloseListener = l;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		async.pause();
		
		try {
			SharedPreferences queryState = getSupportActivity().getSharedPreferences("QueryState", 0);
			SharedPreferences.Editor editor = queryState.edit();
			editor.putString("UserQuery", UserQuery.SERIALIZER.toString(userQuery));
			editor.commit();
			Log.d("UserQueryFragment", "Persisted UserQuery: " + userQuery);
		} catch (Exception e) {
			Log.w("UserQueryFragment", "UserQuery cannot be persisted", e);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		async.resume();
		
		try {
			SharedPreferences queryState = getSupportActivity().getSharedPreferences("QueryState", 0);
			String state = queryState.getString("UserQuery", null);
			if(state != null) {
				userQuery = UserQuery.SERIALIZER.fromString(state);
			
				Neighbourhood n = userQuery.getNeighbourhood();
				if(n != null)
					distance.setMultipliedValue(n.getRadius());
				
				Compatibility c = userQuery.getCompatibility();
				rank.setConvertedValue(c == null ? 0.0f : c.getRank());
				
				interestFilterAdapter.notifyDataSetChanged();
				Log.d("UserQueryFragment", "Restored UserQuery: " + userQuery);
			}
			
			notifyQueryChanged();
		} catch (Exception e) {
			Log.w("UserQueryFragment", "UserQuery cannot be restored", e);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		async.shutdown();
	}

	@Override
	public void onDrawerOpened() {
		if(onDrawerOpenListener != null)
			onDrawerOpenListener.onDrawerOpened();
		icDrawer.setImageResource(R.drawable.ic_menu_drawer_top);
	}

	@Override
	public void onDrawerClosed() {
		if(onDrawerCloseListener != null)
			onDrawerCloseListener.onDrawerClosed();
		icDrawer.setImageResource(R.drawable.ic_menu_drawer_bottom);

	}
}
