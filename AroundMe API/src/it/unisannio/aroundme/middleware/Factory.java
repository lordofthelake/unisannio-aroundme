package it.unisannio.aroundme.middleware;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public abstract class Factory {
	private static Factory instance;
	private static DocumentBuilder documentBuilder = null;
	
	
	public static void setInstance(Factory e) {
		instance = e;
	}
	
	public static Factory getInstance() {
		if(instance == null)
			throw new IllegalStateException("No concrete factory set.");
		
		return instance;
	}
	
	public static DocumentBuilder getDocumentBuilder() {
		if(documentBuilder == null) {
			try {
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return documentBuilder;
	}
	
	public abstract User newUser();
	public abstract Interest newInterest();
	public abstract Position newPosition();
	public abstract Picture<?> newPicture();
	
}
