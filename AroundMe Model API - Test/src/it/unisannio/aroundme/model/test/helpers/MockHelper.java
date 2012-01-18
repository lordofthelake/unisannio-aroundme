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
package it.unisannio.aroundme.model.test.helpers;

import static org.easymock.EasyMock.*;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Position;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class MockHelper {
	public static Interest createMockInterest(long id, String name, String category) {
		Interest mockInterest = createMock(Interest.class);
		expect(mockInterest.getCategory()).andReturn(category);
		expect(mockInterest.getName()).andReturn(name);
		expect(mockInterest.getId()).andReturn(id);
		
		replay(mockInterest);
		return mockInterest;
	}
	
	public static Position createMockPosition(double lat, double lon) {

		Position mockPosition = createMock(Position.class);
		expect(mockPosition.getLatitude()).andReturn(lat);
		expect(mockPosition.getLongitude()).andReturn(lon);
		replay(mockPosition);
		
		return mockPosition;
	}
	
}
