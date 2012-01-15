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
