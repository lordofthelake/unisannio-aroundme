package it.unisannio.aroundme.server;

import java.util.ArrayList;
import java.util.List;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.googlecode.objectify.annotation.Indexed;

import it.unisannio.aroundme.model.Position;

/**
 * Implementazione lato server di {@link Position}.
 * Utilizza le annotazioni necessarie per la persistenza sul Datastore
 * 
 * @see Position
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
@Indexed
public class PositionImpl extends Position {
	private static final long serialVersionUID = 1L;
	
	private double latitude;
	private double longitude;
	/**
	 * List contenente gli hash che identificano un la cella della posizione
	 * mediante la tecnica di Geohashing
	 * 
	 * @see http://code.google.com/apis/maps/articles/geospatial.html
	 */
	private List<String> cells;
	
	/**
	 * Crea una nuova {@link Position}.
	 * &Egrave dichiarato protected per evitare che
	 * non venga creato tramite {@link ModelFactory}. 
	 * @param latitude
	 * @param longitude
	 */
	protected PositionImpl(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		/*
		 * Calcola gli hash della cella della posizione data
		 */
		cells = GeocellManager.generateGeoCell(new Point(latitude, longitude));
	}
	
	/**
	 * Costruttore senza argomenti necessario per la persistenza dell'User sul Datastore
	 */
	protected PositionImpl() {
		cells = new ArrayList<String>();
	}
	
	/**
	 * {@inheritDoc}	
	 */
	@Override
	public double getLatitude() {
		return latitude;
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Restituisce una List contenente gli hash che identificano la cella della posizione
	 * mediante la tecnica di Geohashing
	 * @return una List contenente gli hash che identificano la cella della posizione
	 */
	 public List<String> getCells() {
		return cells;
	}
	
	

}
