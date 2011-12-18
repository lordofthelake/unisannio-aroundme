package it.unisannio.aroundme.client;

import it.unisannio.aroundme.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.view.MenuInflater;


/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 *
 */
public class ProfileActivity extends FragmentActivity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profileview);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.user_menu, menu);
	    return true;
	}
}
