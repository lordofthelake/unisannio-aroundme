package it.unisannio.aroundme.server;

import com.googlecode.objectify.annotation.Indexed;

import it.unisannio.aroundme.model.Position;


/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
@Indexed
public class PositionImpl implements Position {

	private double latitude;
	private double longitude;	
	
	
	/*
	 * Calcolo della distanza in metri tra due punti per piccole distanze.
	 * Il metodo è molto (troppo) approssimato e si limita ad eseguire il calcolo
	 * della distanza tra due punti sul piano cartesiano tramite il teorema di Pitagora 
	 * moltiplicando, poi, il risultato per una quantità tale da portare la distanza in metri.
	 * 
	 *  L'approssimazione aumenta di molto all'aumentare della distanza:
	 *  
	 * -Calcolando la distanza tra i dintorni dell'ingresso del Pallazzo Giannone (41.1309285, 14.7775555) e 
	 * 	il punto dove ora si erge quel bizzarro matitone rosso (41.1312275, 14.7778049) si ottiene
	 *  una distanza di 39 metri circa, lo stesso risultato che si ottiene utilizzando un software "serio".
	 *  
	 *  -Calcolando la distanza tra l'ingresso del Pallazzo Giannone (41.1309285, 14.7775555) e 
	 *  	l'ingresso dell'RCOST (41.1315992, 14.7779900) si ottiene:
	 *  	-Con questo codice, una distanza di 80 metri circa;
	 *  	-Con un software "serio", una distanza di 83 metri circa;
	 *  Il risutato è accettabile.
	 *  
	 *  - Calcolando invece la distanza tra l'ingresso
	 *  del Giannone (41.1309285, 14.7775555) e l'obelisco di Piazza Santa Sofia 
	 *  (41.1304275, 14.7809672), si ottiene:
	 *  	-Con questo codice, una distanza di 345 metri circa;
	 *  	-Con un software "serio", una distanza 291 metri circa;
	 *  La differenza è un tantino inaccettabile!
	 *  
	 * Altri test vari:
	 * 	- distanza "vera": 155m; distanza calcolata con questo metodo 177m;
	 * 	- distanza "vera": 100m; distanza calcolata con questo metodo 120m;
	 * 	- distanza "vera": 106m; distanza calcolata con questo metodo 103m;
	 *  Questi ultimi due risultati mi hanno fatto notare che la misura varia a seconda
	 *  della "direzione" verso cui prendo la misura. Se non per questo inconveniente,  
	 *  il metodo sarebbe potuto andare quasi bene se consideriamo che la nostra applicazione,
	 *  secondo la traccia, dovrebbe ricercare in un raggio di 100m. 
	 *  
	 *  I test sono stati fatti utilizzando una delle tante applicazioni che si trovano
	 *  in rete sul calcolo della distaza tra due coordinate tramite Google Maps
	 *  
	 */
	//FIXME
	@Override
	public double getDistance(Position p) {
		double d = Math.sqrt(Math.pow(p.getLatitude() - this.latitude, 2) +  Math.pow((p.getLongitude() - this.longitude), 2));
		return Math.round(d * 100000);
	}

	@Override
	public void setLongitude(double lon) {
		longitude = lon;
	}

	@Override
	public void setLatitude(double lat) {
		latitude = lat;
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
