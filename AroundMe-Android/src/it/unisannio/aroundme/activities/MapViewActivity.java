package it.unisannio.aroundme.activities;
import java.util.HashSet;
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

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.overlay.UserItemizedOverlay;

public class MapViewActivity extends FragmentMapActivity  {
	private MapView mapView;
	private UserQuery userQuery;
	private UserQueryFragment fragment;
	private AsyncQueue async;
	
    protected void onCreate(Bundle savedStateInstance) {
    	super.onCreate(savedStateInstance);
    	
    	async = new AsyncQueue(); // 1 thread. Nelle mappe viene visualizzata un'immagine alla volta
		
    	setContentView(R.layout.map_view);	
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		
		List<Overlay> overlays = mapView.getOverlays();
		
		UserItemizedOverlay userOverlay = new UserItemizedOverlay(R.drawable.marker_red, mapView, async);

		ModelFactory f = ModelFactory.getInstance();
		User user1 = f.createUser(1321813090L, "Michele Piccirillo", new HashSet<Interest>());
		user1.setPosition(f.createPosition(41.057502,14.280308));
		userOverlay.addUser(user1);
		
		overlays.add(userOverlay);
		
		MapController controller = mapView.getController();
		
		controller.setCenter(PositionUtils.toGeoPoint(user1.getPosition()));
		controller.animateTo(PositionUtils.toGeoPoint(user1.getPosition()));

		controller.setZoom(16);

		 
		long[] ids = getIntent().getLongArrayExtra("userIds");
		if(ids != null) {
    		userQuery = UserQuery.byId(ids);
    		//refresh();
		} else {
			FragmentManager fragmentManager = getSupportFragmentManager();
	        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        fragment = new UserQueryFragment();
	        fragmentTransaction.add(R.id.mapview_layout, fragment);
	        fragmentTransaction.commit();

	        //fragment.setOnDrawerOpenListener(this);
	        //fragment.setOnDrawerCloseListener(this);
	        //fragment.setOnQueryChangeListener(this);
		}	

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
}
