package it.unisannio.aroundme.server;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.Interest;

/**
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
@Entity(name="Interest")
@Indexed
public class InterestImpl extends Interest{
	private static final long serialVersionUID = 1L;
	
	@Id private long id;
	@Unindexed private String name;
	@Unindexed private String category;
	
	public InterestImpl(long id, String name, String category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}
	
	public InterestImpl(){}

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
