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

import it.unisannio.aroundme.activities.MapViewActivity;
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
 * Overlay per la MapView che utilizza marker colorati e balloon informativi per mostrare gli utenti.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 * @see MapViewActivity
 */
public class UserItemizedOverlay extends
		BalloonItemizedOverlay<UserOverlayItem> {
	
	private List<UserOverlayItem> overlays;
	private AsyncQueue async;
	
	/**
	 * Crea un nuovo overlay che utilizza la risorsa specificata come marker.
	 * 
	 * @param defaultMarker il marker che viene usato per segnalare gli utenti sulla mappa
	 * @param mapView la {@code MapView} a cui &egrave; associato questo overlay
	 * @param async una {@code AsyncQueue}, usata per il download delle immagini
	 */
	public UserItemizedOverlay(int defaultMarker, MapView mapView, AsyncQueue async) {
		this(mapView.getContext().getResources().getDrawable(defaultMarker), mapView, async); 
	}

	/**
	 * Crea un nuovo overlay che utilizza il {@code Drawable} specificato come marker.
	 * 
	 * @param defaultMarker il marker che viene usato per segnalare gli utenti sulla mappa
	 * @param mapView la {@code MapView} a cui &egrave; associato questo overlay
	 * @param async una {@code AsyncQueue}, usata per il download delle immagini
	 */
	public UserItemizedOverlay(Drawable defaultMarker, MapView mapView, AsyncQueue async) {
		super(defaultMarker, mapView);
		this.overlays = new ArrayList<UserOverlayItem>();
		this.async = async;
	}
	
	/**
	 * Aggiunge un nuovo utente all'overlay.
	 * 
	 * @param user l'utente da aggiungere
	 */
	public void add(User user) {
		addOverlay(new UserOverlayItem(getMapView().getContext(), user));
	}
	
	/**
	 * Aggiunge all'overlay tutti gli utenti contenuti nelal collezione.
	 * 
	 * @param users gli utenti da aggiungere
	 */
	public void addAll(Collection<User> users) {
		for(User u : users) 
			overlays.add(new UserOverlayItem(getMapView().getContext(), u));
		refresh();
	}

	/**
	 * Ripulisce l'overlay da tutti gli utenti.
	 */
	public void clear() {
		overlays.clear();
		refresh();
	}
	
	/**
	 * Aggiunge un item all'overlay
	 * @param overlay l'item da aggiungere
	 * @see #add(User)
	 */
	public void addOverlay(UserOverlayItem overlay) {
	    overlays.add(overlay);
	    refresh();
	}
	
	/**
	 * Chiede all'overlay di rifare tutti i calcoli interni ed eliminare il focus dall'ultimo utente selezionato.
	 */
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

	/**
	 * Metodo che riceve l'evento nel caso in cui si registri un click sul balloon.
	 * 
	 * L'azione intrapresa &egrave; quella di aprire il profilo dell'utente
	 */
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
