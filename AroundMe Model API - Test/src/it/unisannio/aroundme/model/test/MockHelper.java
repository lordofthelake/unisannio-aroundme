package it.unisannio.aroundme.model.test;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;

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
