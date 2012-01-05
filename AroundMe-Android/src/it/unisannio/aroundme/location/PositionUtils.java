package it.unisannio.aroundme.location;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class PositionUtils {
	private PositionUtils() {}
	
	public static GeoPoint toGeoPoint(Position p) {
		return new GeoPoint((int) (p.getLatitude()*1E6), (int) (p.getLongitude()*1E6));
	}
	
	public static Position toPosition(Location l) {
		return ModelFactory.getInstance().createPosition(l.getLatitude(), l.getLongitude());
	}
}
