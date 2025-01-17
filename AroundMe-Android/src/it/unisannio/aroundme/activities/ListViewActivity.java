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


import it.unisannio.aroundme.Application;
import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.activities.UserQueryExecutorFragment.UserQueryExecutionListener;
import it.unisannio.aroundme.adapters.UserAdapter;
import it.unisannio.aroundme.async.*;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.services.C2DMNotificationService;

import java.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

/**
 * Activity che visualizza un insieme di utenti su in una lista.
 * 
 * Se l'{@code Intent} che l'ha avviata contiene un extra di tipo {@code long[]} chiamato {@code userIds}, essa funziona come semplice
 * visualizzatore di utenti (senza possibilit&agrave; di cambiare i parametri di ricerca). Senza extra, viene avviata in modalit&agrave;
 * interattiva, con uno {@link UserQueryFragment} che permette il cambiamento dei criteri di ricerca.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 */
public class ListViewActivity extends FragmentActivity 
		implements OnItemClickListener, OnDrawerOpenListener, OnDrawerCloseListener, UserQueryExecutionListener {
	private AsyncQueue pictureAsync;
	
	private UserAdapter usrAdapter;
	
	private List<User> users;
	
	private ListView nearByList;
	
	private UserQuery userQuery;
	
	private UserQueryFragment queryFragment;
	private UserQueryExecutorFragment execFragment; 
	
	private long[] ids = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.listview);

    	pictureAsync = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
    	
    	users = new ArrayList<User>();
    	
        nearByList = (ListView) findViewById(R.id.nearByList);
        
        nearByList.setOnItemClickListener(this);
       
        usrAdapter = new UserAdapter(ListViewActivity.this, Identity.get(), users, pictureAsync);
        nearByList.setAdapter(usrAdapter);
        

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		execFragment = new UserQueryExecutorFragment();
		fragmentTransaction.add(R.id.listview_layout, execFragment);
		
		ids = getIntent().getLongArrayExtra("userIds");
		if(ids != null) {
    		userQuery = UserQuery.byId(ids);
		} else {
	        queryFragment = new UserQueryFragment();
	        fragmentTransaction.add(R.id.listview_layout, queryFragment);
		}
		C2DMNotificationService.markAllAsRead(getApplicationContext());
		
		fragmentTransaction.commit();

		if(queryFragment == null) {
			execFragment.onQueryChanged(userQuery);
			execFragment.refresh();
		} else {
	        queryFragment.setOnDrawerOpenListener(this);
	        queryFragment.setOnDrawerCloseListener(this);
	        queryFragment.setOnQueryChangeListener(execFragment);
		}	 
		
		execFragment.setExecutionListener(this);
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		if(Identity.get() == null) {
			if(!((Application) getApplication()).isTerminated()) {
				startActivity(new Intent(this, LoginActivity.class));
			}
			finish();
			return;
		}
		
		pictureAsync.resume();
	}
    
    @Override
	public void onDrawerOpened() {
		nearByList.setEnabled(false);
	}
    
    @Override
	public void onDrawerClosed() {
		nearByList.setEnabled(true);
		execFragment.refreshIfChanged();
	}
    
    public void onItemClick(AdapterView<?> arg0, View v, int index,long id) {
		Intent intent = new Intent(ListViewActivity.this, ProfileActivity.class);
		intent.putExtra("userId", ((User) v.getTag(R.id.tag_user)).getId());
		startActivity(intent);				
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    menu.findItem(R.id.toList).setVisible(false);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent i = new Intent(this, ProfileActivity.class);
	    	i.putExtra("userId", Identity.get().getId());
	    	startActivity(i);
	    	return true;
	    case R.id.toMap:
	    	Intent i2 = new Intent(this, MapViewActivity.class);
	    	if(ids != null)
	    		i2.putExtra("userIds", ids);
	        startActivity(i2);
	        return true;
	    case R.id.preferences:
	    	startActivity(new Intent(this, PreferencesActivity.class));
	    	return true;
	    case R.id.profile:
	    	Intent i1 = new Intent(this, ProfileActivity.class);
	    	i1.putExtra("userId", Identity.get().getId());
	    	startActivity(i1);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		pictureAsync.pause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pictureAsync.shutdown();
	}

	@Override
	public void onUserQueryExecutionResults(Collection<User> results) {
		users.clear();
		users.addAll(results);
		usrAdapter.notifyDataSetChanged();
	}

}