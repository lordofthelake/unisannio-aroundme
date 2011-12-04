package it.unisannio.aroundme.model;

class NeighbourhoodImpl implements Neighbourhood {
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
