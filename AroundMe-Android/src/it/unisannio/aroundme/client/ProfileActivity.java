package it.unisannio.aroundme.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 *
 */
public class ProfileActivity extends DataActivity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profileview);
		/*ottengo l'user ID dell' utente da visualizzare*/
		this.userId= Long.parseLong((String) getIntent().getExtras().get("userId"));
		this.txtName=(TextView) findViewById(R.id.txtName);
		this.image=(ImageView) findViewById(R.id.imgPhoto);	
		this.compatibility= (TextView)findViewById(R.id.txtCompatibility);
		this.distance=(TextView)findViewById(R.id.txtDistance);
		
	}
	@Override
    protected void onServiceConnected(final DataService service) {
        progress = ProgressDialog.show(ProfileActivity.this, "", ProfileActivity.this.getString(R.string.loading), true, true);
      //Return a collection with only one element
       service.asyncDo(UserQuery.single(this.userId),new DataListener<User>(){
		@Override
		public void onData(User user) {
			loadedUser=user;
			txtName.setText(user.getName());
			//TODO creare utente Me
			distance.setText(String.format("%.1f m", user.getDistance(user)));
			compatibility.setText(user.getCompatibilityRank(user)+" %");
		}
		@Override
		public void onError(Exception e) {}   
       });
       service.asyncDo(Picture.get(loadedUser.getId()), new DataListener<Bitmap>() {
			@Override
			public void onData(Bitmap object) {
				image.setImageBitmap(object);
			}
			@Override
			public void onError(Exception e) {}	
		});
    }	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.user_menu, menu);
	    return true;
	}
	private TextView txtName;
	private TextView compatibility;
	private TextView distance;
	private ImageView image;
	private User loadedUser;
	private ProgressDialog progress;
	private long userId;
}
