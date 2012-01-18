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
package it.unisannio.aroundme.test;

import android.test.AndroidTestCase;
import it.unisannio.aroundme.model.test.helpers.UserTestHelper;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserTest extends AndroidTestCase {
	private UserTestHelper helper;
	
	protected void setUp() throws Exception {
		super.setUp();
		helper = new UserTestHelper();
	}
	
	public void testCompatibilityRank() {
		helper.testCompatibilityRank();
	}
	
	public void testEquals() {
		helper.testEquals();
	}
	
	public void testPosition() {
		helper.testPosition();
	}

}
