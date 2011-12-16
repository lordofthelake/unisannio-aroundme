package it.unisannio.aroundme.client;


import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class PictureStore {
	private Map<Long, Picture> pictures = new HashMap<Long, Picture>();
	private DataService service;
	
	public PictureStore(DataService service) {
		this.service = service;
	}
	
	public Picture get(long id) {
		if(!pictures.containsKey(id)) {
			pictures.put(id, new Picture(service, id));
		}
		
		return pictures.get(id);
	}
	
	public void clean() {
		pictures.clear();
	}
}
