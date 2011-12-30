package it.unisannio.aroundme.client;

import java.util.ArrayList;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v4.view.Menu;
import android.view.MenuInflater;
import android.widget.GridView;
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
		txtName=(TextView) findViewById(R.id.txtName);
		image=(ImageView) findViewById(R.id.imgPhoto);	
		compatibility= (TextView)findViewById(R.id.txtCompatibility);
		distance=(TextView)findViewById(R.id.txtDistance);
		grdInterests=(GridView)findViewById(R.id.grdInterests);
        progress = ProgressDialog.show(ProfileActivity.this, "", ProfileActivity.this.getString(R.string.loading), true, true);
        
        service.asyncDo(UserQuery.single(userId),new DataListener<User>(){
			@Override
			public void onData(User user) {
				//Questa activity vivr? in questo evento 
				loadedUser=user;
				setFbPicture(loadedUser.getId(),service);
				txtName.setText(user.getName());
				//TODO creare utente Me
				//distance.setText(String.format("%.1f m", user.getDistance(user)));
				//compatibility.setText(user.getCompatibilityRank(user)+" %");
				ArrayList arrInterests = new ArrayList(user.getInterests());
				grdInterests.setAdapter(new InterestAdapter(ProfileActivity.this,arrInterests,service));	
				/*
				 * TODO Al click di un interesse apre la pagina facebook di quell' interesse
				 * TODO Al click dell' icona di facebook mostra il profilo facebook della persona
				 * */
				
				progress.dismiss();
			}
			@Override
			public void onError(Exception e) {
				Toast.makeText(ProfileActivity.this, "Impossibile accedere al server", Toast.LENGTH_SHORT).show();
			}   
       });        
	}
	
	private void setFbPicture(long userId,DataService service){
		//ottengo l'immagine dall' user id dell' intent
	       service.asyncDo(Picture.get(userId), new DataListener<Bitmap>() {
				@Override
				public void onData(Bitmap object) {
					image.setImageBitmap(object);
				}
				@Override
				public void onError(Exception e) {
					image.setImageResource(R.drawable.img_error);
				}	
	       });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.user_menu, menu);
	    return true;
	}

	private GridView grdInterests;
	private TextView txtName;
	private TextView compatibility;
	private TextView distance;
	private ImageView image;
	private User loadedUser;
	private ProgressDialog progress;
	private long userId;
}
