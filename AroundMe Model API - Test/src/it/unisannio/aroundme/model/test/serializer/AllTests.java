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
package it.unisannio.aroundme.model.test.serializer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CompatibilitySerializerTest.class);
		suite.addTestSuite(InterestSerializerTest.class);
		suite.addTestSuite(NeighbourhoodSerializerTest.class);
		suite.addTestSuite(PositionSerializerTest.class);
		suite.addTestSuite(PreferencesSerializerTest.class);
		suite.addTestSuite(UserQueySerializerTest.class);
		suite.addTestSuite(UserSerializerTest.class);
		//$JUnit-END$
		return suite;
	}

}
