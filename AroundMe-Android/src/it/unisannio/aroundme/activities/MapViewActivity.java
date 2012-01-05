package it.unisannio.aroundme.activities;
import java.util.HashSet;
import java.util.List;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.os.Bundle;
import android.support.v4.app.FragmentMapActivity;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.overlay.UserItemizedOverlay;

public class MapViewActivity extends FragmentMapActivity  {
	private MapView mapView;
	
	private AsyncQueue pictureAsync;
	
    protected void onCreate(Bundle savedStateInstance) {
    	super.onCreate(savedStateInstance);
    	
    	pictureAsync = new AsyncQueue(); // 1 thread. Nelle mappe viene visualizzata un'immagine alla volta
		
    	setContentView(R.layout.map_view);	
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		
		List<Overlay> overlays = mapView.getOverlays();
		
		UserItemizedOverlay userOverlay = new UserItemizedOverlay(R.drawable.marker_red, mapView, pictureAsync);

		ModelFactory f = ModelFactory.getInstance();
		User user1 = f.createUser(1321813090L, "Michele Piccirillo", new HashSet<Interest>());
		user1.setPosition(f.createPosition(41.057502,14.280308));
		userOverlay.addUser(user1);
		
		overlays.add(userOverlay);
		
		//mapView.getController().animateTo(PositionUtils.toGeoPoint(user1.getPosition()));
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		pictureAsync.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		pictureAsync.resume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pictureAsync.shutdown();
	}
}