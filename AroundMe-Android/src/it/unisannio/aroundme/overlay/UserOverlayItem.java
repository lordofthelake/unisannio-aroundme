package it.unisannio.aroundme.overlay;

import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.User;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class UserOverlayItem extends OverlayItem {
	private User user;
	
	public UserOverlayItem(User user) {
		// FIXME
		super(PositionUtils.toGeoPoint(user.getPosition()), user.getName(), String.valueOf(user.getCompatibilityRank(Identity.get())));
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

}
