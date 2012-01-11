package it.unisannio.aroundme.activities;
import java.util.Collection;
import java.util.List;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.UserQueryExecutorFragment.UserQueryExecutionListener;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.overlay.UserItemizedOverlay;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class MapViewActivity extends FragmentMapActivity implements OnDrawerOpenListener, OnDrawerCloseListener, UserQueryExecutionListener  {
	private MapView mapView;
	private UserQuery userQuery;
	
	private UserQueryFragment queryFragment;
	private UserQueryExecutorFragment execFragment;
	
	private AsyncQueue async;
	
	private UserItemizedOverlay myOverlay;
	private UserItemizedOverlay nearbyOverlay;
	
    protected void onCreate(Bundle savedStateInstance) {
    	super.onCreate(savedStateInstance);
    	Identity me = Identity.get();
		if(me == null) {
			// FIXME Se l'utente non e' settato, redireziona al login
		}
			
    	async = new AsyncQueue(); // 1 thread. Nelle mappe viene visualizzata un'immagine alla volta
		
    	setContentView(R.layout.map_view);	
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(false);
		
		List<Overlay> overlays = mapView.getOverlays();
		
		myOverlay = new UserItemizedOverlay(R.drawable.marker_green, mapView, async);
		myOverlay.add(me);
		overlays.add(myOverlay);
		
		nearbyOverlay = new UserItemizedOverlay(R.drawable.marker_red, mapView, async);
		overlays.add(nearbyOverlay);
		
		MapController controller = mapView.getController();
		controller.animateTo(PositionUtils.toGeoPoint(me.getPosition()));
		controller.setZoom(16);

		 
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		execFragment = new UserQueryExecutorFragment();
		fragmentTransaction.add(R.id.mapview_layout, execFragment);
		
		long[] ids = getIntent().getLongArrayExtra("userIds");
		if(ids != null) {
    		userQuery = UserQuery.byId(ids);
		} else {
	        queryFragment = new UserQueryFragment();
	        fragmentTransaction.add(R.id.mapview_layout, queryFragment);
		}
		
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
	public void onDrawerOpened() {
		mapView.setEnabled(false);
	}
    
    @Override
	public void onDrawerClosed() {
		mapView.setEnabled(true);
		execFragment.refreshIfChanged();
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    menu.findItem(R.id.toMap).setVisible(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent i =new Intent(this, ProfileActivity.class);
	    	i.putExtra("userId", Identity.get().getId());
	    	startActivity(i);
	    	return true;
	    case R.id.toList:
	    	startActivity(new Intent(this, ListViewActivity.class));
	        return true;
	    case R.id.toMap:
	        startActivity(new Intent(this, MapViewActivity.class));
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
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		async.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		async.resume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		async.shutdown();
	}


	@Override
	public void onUserQueryExecutionResults(Collection<User> results) {
		nearbyOverlay.clear();
		nearbyOverlay.addAll(results);
		
		mapView.getController().zoomToSpan(nearbyOverlay.getLatSpanE6(), nearbyOverlay.getLonSpanE6());
	}
}
