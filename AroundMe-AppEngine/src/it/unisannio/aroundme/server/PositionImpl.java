package it.unisannio.aroundme.server;

import com.googlecode.objectify.annotation.Indexed;

import it.unisannio.aroundme.model.Position;


/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */

@Indexed
public class PositionImpl extends Position {

	private static final long serialVersionUID = 1L;
	
	private double latitude;
	private double longitude;	
	
	public PositionImpl() {}
	
	public PositionImpl(double latitude, double longitude) {
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
