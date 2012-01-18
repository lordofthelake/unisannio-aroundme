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

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Model;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import java.util.Collection;
/**
 * {@link ModelFactory} implementata in modo tale da produrre le implementazioni lato server
 * dei vari {@link Model}
 * @see {@link ModelFactory}
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class ServerModelFactory extends ModelFactory{

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public User createUser(long id, String name, Collection<Interest> interests) {
		UserImpl userImpl = new UserImpl(id, name);
		if(interests != null){
			for(Interest i: interests){
				userImpl.addInterest(i);
			}
		}
		return userImpl;
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public Interest createInterest(long id, String name, String category) {
		return new InterestImpl(id, name, category);
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public Position createPosition(double lat, double lon) {
		return new PositionImpl(lat, lon);
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public UserQuery createUserQuery() {
		return new UserQueryImpl();
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public Preferences createPreferences() {
		return new PreferencesImpl();
	}
}
