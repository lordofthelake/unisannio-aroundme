/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
