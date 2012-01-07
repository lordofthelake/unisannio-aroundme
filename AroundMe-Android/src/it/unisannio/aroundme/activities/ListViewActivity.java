package it.unisannio.aroundme.activities;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.activities.UserQueryFragment.OnQueryChangeListener;
import it.unisannio.aroundme.adapters.ArrayPagerAdapter;
import it.unisannio.aroundme.adapters.InterestFilterAdapter;
import it.unisannio.aroundme.adapters.UserAdapter;
import it.unisannio.aroundme.async.*;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.services.PositionTrackingService;

import java.text.MessageFormat;
import java.util.*;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */

/* TODO Si dovrebbe accettare una lista di id via Intent, cosÃ¬ che possa essere usato
 * dal Notification service
 */
public class ListViewActivity extends FragmentActivity 
		implements OnItemClickListener, OnCancelListener, OnDrawerOpenListener, OnDrawerCloseListener,
		OnQueryChangeListener {
	private AsyncQueue async;
	private AsyncQueue pictureAsync;
	
	private UserAdapter usrAdapter;
	
	private List<User> users;
	
	private ListView nearByList;
	
	private SlidingDrawer drawer;
	private ProgressDialog progress;
	
	private boolean needsRefresh = true;
	private UserQuery userQuery;
	private UserQueryFragment fragment;
	
	private ListenableFuture<Collection<User>> task = null; 
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	if(Identity.get() == null) {
    		// TODO Si dovrebbe avviare l'attività di login per procedere all'autenticazione
    	}
    	
    	
    	
    	setContentView(R.layout.listview);
    	
    	async = new AsyncQueue();
    	pictureAsync = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
    	
    	users = new ArrayList<User>();
    	
        nearByList = (ListView) findViewById(R.id.nearByList);
        
        nearByList.setOnItemClickListener(this);
       
        nearByList.setAdapter(usrAdapter = new UserAdapter(ListViewActivity.this, Identity.get(), users, pictureAsync));
    	
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new UserQueryFragment();
        fragmentTransaction.add(R.id.listview_layout, fragment);
        fragmentTransaction.commit();
        
        fragment.setOnDrawerOpenListener(this);
        fragment.setOnDrawerCloseListener(this);
        fragment.setOnQueryChangeListener(this);

	    
    }
    
    @Override
	public void onDrawerOpened() {
		nearByList.setEnabled(false);
	}
    
    @Override
	public void onDrawerClosed() {
		nearByList.setEnabled(true);
		if(needsRefresh)
			refresh();
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
	    case R.id.toMap:
	        startActivity(new Intent(this, MapViewActivity.class));
	        return true;
	    case R.id.preferences:
	    	startActivity(new Intent(this, PreferencesActivity.class));
	    	return true;
	    case R.id.profile:
	    	Intent i = new Intent(this, ProfileActivity.class);
	    	i.putExtra("userId", Identity.get().getId());
	    	startActivity(i);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if(task != null)
			task.cancel(true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		async.pause();
		pictureAsync.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		async.resume();
		pictureAsync.resume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/*
		if(userQuery != null) { 
			try {
				// Sfruttiamo il serializzatore XML per salvare lo stato della query
				outState.putString("userQuery", UserQuery.SERIALIZER.toString(userQuery));
			} catch (TransformerException tEx) {
				Log.d("ListViewActivity", "Error serializing UserQuery", tEx);
			}
		}*/
		
		/* TODO Andrebbero cacheati anche i risultati.
		 * Se l'utente gira il dispositivo non vogliamo che venga fatta un'altra query in rete
		 */
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		async.shutdown();
		pictureAsync.shutdown();
	}
	
	/*
	 * Effettua il refresh della view inviando la query definita
	 * nelle impostazioni al datastore remoto
	 * */
	private void refresh(){
		progress = ProgressDialog.show(ListViewActivity.this, "", ListViewActivity.this.getString(R.string.loading), true, true);
    	progress.setOnCancelListener(this);
    	
		try {
			Log.d("UserQuery", UserQuery.SERIALIZER.toString(userQuery));
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.task = async.exec(userQuery, new FutureListener<Collection<User>>(){
        	@Override
        	public void onSuccess(Collection<User> object) {
        		task = null;
        		progress.dismiss();
        		users.clear();
        		users.addAll(object);
        		usrAdapter.notifyDataSetChanged();
        		needsRefresh = false;
        	}
        	@Override
        	public void onError(Throwable e) {
        		progress.dismiss();
        		Toast.makeText(ListViewActivity.this, R.string.loadingError, Toast.LENGTH_LONG).show();	
        		e.printStackTrace();
        	}
        });
	}

	@Override
	public void onQueryChanged(UserQuery query) {
		boolean mustRefreshNow = (userQuery == null);
		userQuery = query;
		needsRefresh = true;
		if(mustRefreshNow)
			refresh();
	}
}