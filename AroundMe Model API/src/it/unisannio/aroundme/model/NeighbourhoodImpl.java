package it.unisannio.aroundme.model;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
class NeighbourhoodImpl implements Neighbourhood {
	private static final long serialVersionUID = 1L;
	
	private Position position;
	private double radius;
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position p) {
		this.position = p;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
} 
