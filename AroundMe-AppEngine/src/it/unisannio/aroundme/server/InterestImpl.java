/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.server;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;

/**
 * Implementazione lato server di {@link Interest}.
 * Utilizza le annotazioni necessarie per la persistenza sul Datastore
 * 
 * @see Interest
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
	
	/**
	 * Crea un nuovo {@link InterestImpl}.
	 * &Egrave dichiarato protected per evitare che
	 * non venga creato tramite {@link ModelFactory}. 
	 * @param l'id univoco dell'Interest
	 * @param name il nome dell'Interest
	 * @param category la categoria dell'Interest
	 */
	protected InterestImpl(long id, String name, String category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}
	
	/**
	 * Costruttore senza argomenti necessario per la persistenza dell'Interest sul Datastore
	 */
	protected InterestImpl(){}

	
	/**
	 *{@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 *{@inheritDoc}
	 */
	@Override
	public String getCategory() {
		return category;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}

}
