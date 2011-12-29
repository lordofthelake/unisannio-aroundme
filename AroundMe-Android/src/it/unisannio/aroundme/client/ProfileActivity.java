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
import android.widget.Toast;


/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 *
 */
public class ProfileActivity extends DataActivity{
	@Override
    protected void onServiceConnected(final DataService service) {
		setContentView(R.layout.profileview);
		/*ottengo l'user ID dell' utente da visualizzare*/
		userId= (Long) getIntent().getExtras().get("userId");
		Toast toast = Toast.makeText(ProfileActivity.this,"Id: "+userId, Toast.LENGTH_SHORT);	
		toast.show();
		txtName=(TextView) findViewById(R.id.txtName);
		image=(ImageView) findViewById(R.id.imgPhoto);	
		compatibility= (TextView)findViewById(R.id.txtCompatibility);
		distance=(TextView)findViewById(R.id.txtDistance);
        progress = ProgressDialog.show(ProfileActivity.this, "", ProfileActivity.this.getString(R.string.loading), true, true);
        
        service.asyncDo(/*UserQuery.single(this.userId)*/
    		   new Callable<User>() {
					@Override
					public User call() throws Exception {
						//FIXME mock method
				        ModelFactory f = ModelFactory.getInstance();
				        Collection<Interest> empty = Collections.emptySet();
				        User user=f.createUser(100001053949157L, "Marco Magnetti", empty);
				        Thread.sleep(2000);
				        return user;
					}
				},new DataListener<User>(){
		@Override
		public void onData(User user) {
			progress.dismiss();
			Toast toast = Toast.makeText(ProfileActivity.this, user.getName()+" Caricato!", Toast.LENGTH_SHORT);	
			toast.show();
			loadedUser=user;
			txtName.setText(user.getName());
			//TODO creare utente Me
			//distance.setText(String.format("%.1f m", user.getDistance(user)));
			//compatibility.setText(user.getCompatibilityRank(user)+" %");
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
		public void onError(Exception e) {
			Toast toast = Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT);	
		}   
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
