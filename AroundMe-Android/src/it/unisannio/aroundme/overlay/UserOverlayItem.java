package it.unisannio.aroundme.overlay;

import java.text.MessageFormat;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.User;

import android.content.Context;

import com.google.android.maps.OverlayItem;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class UserOverlayItem extends OverlayItem {
	private final User user;
	
	public UserOverlayItem(Context ctx, User user) {
		super(
				PositionUtils.toGeoPoint(user.getPosition()), 
				user.getName(), 
				(user.equals(Identity.get()) 
						? ctx.getString(R.string.balloon_snippet_itsyou) 
						: MessageFormat.format(ctx.getString(R.string.balloon_snippet_format), Math.round(Identity.get().getCompatibilityRank(user)*100)/100f)
				)
				
		);
		
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	

}
