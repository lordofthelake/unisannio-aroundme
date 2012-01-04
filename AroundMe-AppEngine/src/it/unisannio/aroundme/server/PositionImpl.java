package it.unisannio.aroundme.server;

import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
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
	private List<String> cells;
	
	public PositionImpl() {}
	
	public PositionImpl(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		cells = GeocellManager.generateGeoCell(new Point(latitude, longitude));
	}
	
	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	public List<String> getCells() {
		return cells;
	}
	
	

}
