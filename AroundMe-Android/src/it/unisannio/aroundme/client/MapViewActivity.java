package it.unisannio.aroundme.client;
import android.os.Bundle;

import com.google.android.maps.MapActivity;

import it.unisannio.aroundme.R;

public class MapViewActivity extends MapActivity  {
    protected void onCreate(Bundle ciccio) {
    	super.onCreate(ciccio);
		setContentView(R.layout.map_view);		       
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
