package it.unisannio.aroundme.overlay;

import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.User;

import com.google.android.maps.OverlayItem;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class UserOverlayItem extends OverlayItem {
	private final User user;
	
	public UserOverlayItem(User user) {
		// FIXME Migliora snippet
		super(PositionUtils.toGeoPoint(user.getPosition()), user.getName(), String.valueOf(user.getCompatibilityRank(Identity.get())));
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	

}
