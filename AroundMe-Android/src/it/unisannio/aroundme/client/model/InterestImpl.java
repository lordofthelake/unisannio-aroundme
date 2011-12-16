package it.unisannio.aroundme.client.model;

import it.unisannio.aroundme.model.Interest;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
class InterestImpl implements Interest {
	private static final long serialVersionUID = 1L;
	
	private final long id;
	private final String name;
	private final String category;

	InterestImpl(long id, String name, String category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public long getId() {
		return id;
	}

}
