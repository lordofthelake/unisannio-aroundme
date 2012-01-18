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

import it.unisannio.aroundme.model.test.helpers.PreferencesTestHelper;
import android.test.AndroidTestCase;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesTest extends AndroidTestCase {
	private PreferencesTestHelper helper;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		helper = new PreferencesTestHelper();
	}
	
	public void testBoolean() {
		helper.testBoolean();
	}
	
	public void testContains() {
		helper.testContains();
	}
	
	public void testGetAll() {
		helper.testGetAll();
	}
	
	public void testNumber() {
		helper.testNumber();
	}
	
	public void testPutAll() {
		helper.testPutAll();
	}
	
	public void testString() {
		helper.testString();
	}
}
