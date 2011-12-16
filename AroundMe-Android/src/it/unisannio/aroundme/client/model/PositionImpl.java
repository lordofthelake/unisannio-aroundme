package it.unisannio.aroundme.client.model;

import it.unisannio.aroundme.model.Position;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
class PositionImpl extends Position {
	private static final long serialVersionUID = 1L;
	
	private final double latitude;
	private final double longitude;

	PositionImpl(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

}
