package it.unisannio.aroundme.server;

import javax.jdo.annotations.Key;

import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.Interest;

/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
@Indexed
public class InterestImpl implements Interest{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Key private long id;
	@Unindexed private String name;
	@Unindexed private String category;
	

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
