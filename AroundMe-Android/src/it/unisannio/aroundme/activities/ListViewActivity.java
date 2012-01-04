package it.unisannio.aroundme.activities;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.adapters.InterestFilterAdapter;
import it.unisannio.aroundme.adapters.UserAdapter;
import it.unisannio.aroundme.async.*;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.*;

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
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
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
		implements OnItemClickListener, OnCancelListener {
	private static final int MAX_DISTANCE=50;
	private AsyncQueue async;
	private AsyncQueue pictureAsync;
	
	private UserAdapter usrAdapter;
	private InterestFilterAdapter interestFilterAdapter;
	
	private List<User> users;
	private List<Interest> myInterests;
	
	private ListView nearByList;
	
	private SlidingDrawer drawer;
	private ListView interestsFilter;
	private ProgressDialog progress;
	private SeekBar seekDistance;
	private TextView txtDistanceFilter;
	
	private UserQuery userQuery;
	
	private ListenableFuture<Collection<User>> task = null; 
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	if(Identity.get() == null) {
    		// TODO Si dovrebbe avviare l'attività di login per procedere all'autenticazione
    	}
    	
    	if(savedInstanceState == null) {
    		// L'Activity è stata avviata per la prima volta tramite un Intent
    		long[] ids = getIntent().getLongArrayExtra("userIds");
    		if(ids != null) {
        		userQuery = UserQuery.byId(ids);
        	}
    	} else { 
    		// Controlliamo se c'è uno stato salvato
    		String serializedQuery = savedInstanceState.getString("userQuery");
    		if(serializedQuery != null) {
    			try {
					userQuery = UserQuery.SERIALIZER.fromString(serializedQuery);
				} catch (SAXException e1) {
					Log.d("ListViewActivity", "Error deserializing UserQuery", e1);
				}
    		} 
    	}

    	if(userQuery == null) {
    		// Non Ã¨ stato possibile ricostruire la query. Usiamo le impostazioni di default
    		userQuery = ModelFactory.getInstance().createUserQuery();
    		/**Caricamento delle impostazioni di default
    		 * 	-posizione: 				attuale
    		 * 	-Raggio: 					1km
    		 * 	-compatibilità:				60%
    		 * 	-Interessi considerati:		tutti
    		 */
    		//TODO remove toast
    		Toast.makeText(ListViewActivity.this, "Loading Default UserQuery", Toast.LENGTH_LONG).show();
    		Identity.get().setPosition(ModelFactory.getInstance().createPosition(41.1309285, 14.7775555));
    		Position position = Identity.get().getPosition();
    		Neighbourhood neighbourhood = new Neighbourhood(position, 1000);	
    		userQuery.setCompatibility(new Compatibility(Identity.get().getId(), 0.6F));
    		ArrayList<Interest> interests= new ArrayList<Interest>(Identity.get().getInterests());
    		for (int i=0;i<interests.size();i++){
    			userQuery.addInterestId(interests.get(i).getId());
    		}
    		userQuery.setNeighbourhood(neighbourhood);
    	}
    	
    	setContentView(R.layout.listview);
    	
    	async = new AsyncQueue();
    	pictureAsync = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
    	
    	users = new ArrayList<User>();
    	myInterests = new ArrayList<Interest>(Identity.get().getInterests());
    	
        nearByList = (ListView) findViewById(R.id.nearByList);
        drawer = (SlidingDrawer) findViewById(R.id.filterDrawer);
        seekDistance = (SeekBar) findViewById(R.id.seekDistance);
        txtDistanceFilter = (TextView) findViewById(R.id.txtDistaceFilter);
        
        nearByList.setOnItemClickListener(this);
        seekDistance.setMax(MAX_DISTANCE);
        seekDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int distance= seekDistance.getProgress()*100;
				if (distance==0){
					txtDistanceFilter.setText("Off  ");
				}
				else if (distance<1000){
					txtDistanceFilter.setText(distance+" m");
				}else{
					txtDistanceFilter.setText(String.format("%.1f Km", (float)distance/1000));
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Position position = Identity.get().getPosition();
				Neighbourhood neighbourhood = new Neighbourhood(position, seekBar.getProgress() * 100);	
				userQuery.setNeighbourhood(neighbourhood); //Imposto le preferenze di visualizzazione
			}
        });
        seekDistance.setProgress((int) (userQuery.getNeighbourhood().getRadius())/100);  
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener(){
			@Override
			public void onDrawerOpened() {
				nearByList.setEnabled(false);
			}
        	
        });
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener(){
			@Override
			public void onDrawerClosed() {
				nearByList.setEnabled(true);
				ListViewActivity.this.refresh();
			}
        	
        });
        
        interestsFilter=(ListView) findViewById(R.id.listInterestFilter);
        
        progress = ProgressDialog.show(ListViewActivity.this, "", ListViewActivity.this.getString(R.string.loading), true, true);
    	progress.setOnCancelListener(this);
        nearByList.setAdapter(usrAdapter = new UserAdapter(ListViewActivity.this, Identity.get(), users, pictureAsync));
    	interestsFilter.setAdapter(interestFilterAdapter = new InterestFilterAdapter(ListViewActivity.this,myInterests, pictureAsync,userQuery)); 	
	    ListViewActivity.this.refresh();
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
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.toMap:
	        startActivity(new Intent(this, MapViewActivity.class));
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
		
		if(userQuery != null) { 
			try {
				// Sfruttiamo il serializzatore XML per salvare lo stato della query
				outState.putString("userQuery", UserQuery.SERIALIZER.toString(userQuery));
			} catch (TransformerException tEx) {
				Log.d("ListViewActivity", "Error serializing UserQuery", tEx);
			}
		}
		
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
		//TODO remove toast
		Toast.makeText(ListViewActivity.this, "Performing UserQuery", Toast.LENGTH_LONG).show();
		this.task = async.exec(userQuery, new FutureListener<Collection<User>>(){
        	@Override
        	public void onSuccess(Collection<User> object) {
        		Log.i("LIST", String.valueOf(object.size()));
        		task = null;
        		progress.dismiss();
        		users.clear();
        		users.addAll(object);
        		usrAdapter.notifyDataSetChanged();
        	}
        	@Override
        	public void onError(Throwable e) {
        		progress.dismiss();
        		Toast.makeText(ListViewActivity.this, R.string.loadingError, Toast.LENGTH_LONG).show();	
        		e.printStackTrace();
        	}
        });
	}
}