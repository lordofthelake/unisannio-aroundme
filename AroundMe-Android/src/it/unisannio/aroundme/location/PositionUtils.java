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
package it.unisannio.aroundme.location;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;

import android.location.Location;

import com.google.android.maps.GeoPoint;

/**
 * Classe di utilit&agrave; contententi metodi per manipolare le posizioni geografiche.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PositionUtils {
	private PositionUtils() {}
	
	/**
	 * Converte un oggetto di tipo {@code Position} in un {@code GeoPoint}
	 * 
	 * @param p la posizione da convertire
	 * @return un oggetto di tipo {@code GeoPoint} avente le stesse coordinate
	 * 
	 */
	public static GeoPoint toGeoPoint(Position p) {
		return new GeoPoint((int) (p.getLatitude()*1E6), (int) (p.getLongitude()*1E6));
	}
	
	/**
	 * Converte un oggetto di tipo {@code Location} in una {@code Position}
	 * 
	 * @param l l'oggetto di tipo {@code Location} da convertire
	 * @return un oggetto di tipo {@code Position} avente le stesse coordinate
	 */
	public static Position toPosition(Location l) {
		return ModelFactory.getInstance().createPosition(l.getLatitude(), l.getLongitude());
	}
}
