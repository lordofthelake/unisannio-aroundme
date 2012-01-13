package it.unisannio.aroundme.overlay;

import it.unisannio.aroundme.activities.ProfileActivity;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
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
	
	public void add(User user) {
		addOverlay(new UserOverlayItem(getMapView().getContext(), user));
	}
	
	public void addAll(Collection<User> users) {
		for(User u : users) 
			overlays.add(new UserOverlayItem(getMapView().getContext(), u));
		refresh();
	}

	public void clear() {
		overlays.clear();
		refresh();
	}
	
	public void addOverlay(UserOverlayItem overlay) {
	    overlays.add(overlay);
	    refresh();
	}
	
	public void refresh() {
		populate();
	    setLastFocusedIndex(-1);
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
		Context ctx = getMapView().getContext();
		Intent i = new Intent(ctx, ProfileActivity.class);
		i.putExtra("userId", overlays.get(index).getUser().getId());
		ctx.startActivity(i);
		return true;
	}

	@Override
	protected BalloonOverlayView<UserOverlayItem> createBalloonOverlayView() {
		return new UserBalloonOverlayView(getMapView().getContext(), getBalloonBottomOffset(), async);
	}

}
