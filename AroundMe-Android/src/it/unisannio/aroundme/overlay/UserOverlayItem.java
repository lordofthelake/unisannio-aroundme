package it.unisannio.aroundme.overlay;

import java.text.MessageFormat;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.location.PositionUtils;
import it.unisannio.aroundme.model.User;

import android.content.Context;

import com.google.android.maps.OverlayItem;

/**
 * Un item sulla mappa rappresentante un utente.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class UserOverlayItem extends OverlayItem {
	private final User user;
	
	/**
	 * Crea un nuovo item, associato all'utente indicato.
	 * 
	 * @param ctx un Context, necessario per poter accedere alle risorse dell'applicazione
	 * @param user l'utente a cui l'item &egrave; associato
	 */
	public UserOverlayItem(Context ctx, User user) {
		super(
				PositionUtils.toGeoPoint(user.getPosition()), 
				user.getName(), 
				(user.equals(Identity.get()) 
						? ctx.getString(R.string.balloon_snippet_itsyou) 
						: MessageFormat.format(ctx.getString(R.string.balloon_snippet_format), Identity.get().getCompatibilityRank(user))
				)
				
		);
		
		this.user = user;
	}
	
	/**
	 * Restituisce l'utente associato a questo item.
	 * 
	 * @return l'utente associato a questo item
	 */
	public User getUser() {
		return user;
	}
	

}
