package it.unisannio.aroundme.overlay;

import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.model.User;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class UserItemizedOverlay extends
		BalloonItemizedOverlay<UserOverlayItem> {
	
	private List<UserOverlayItem> overlays;
	private AsyncQueue async;
	
	public UserItemizedOverlay(int defaultMarker, MapView mapView, AsyncQueue async) {
		this(mapView.getContext().getResources().getDrawable(defaultMarker), mapView, async); 
	}

	public UserItemizedOverlay(Drawable defaultMarker, MapView mapView, AsyncQueue async) {
		super(defaultMarker, mapView);
		this.overlays = new ArrayList<UserOverlayItem>();
		this.async = async;
	}
	
	public void addUser(User user) {
		addOverlay(new UserOverlayItem(user));
	}

	public void addOverlay(UserOverlayItem overlay) {
	    overlays.add(overlay);
	    populate();
	}

	@Override
	protected UserOverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, UserOverlayItem item) {
		Toast.makeText(getMapView().getContext(), "onBalloonTap for overlay index " + index,
				Toast.LENGTH_LONG).show();
		return true;
	}

	@Override
	protected BalloonOverlayView<UserOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new UserBalloonOverlayView(getMapView().getContext(), getBalloonBottomOffset(), async);
	}

}
