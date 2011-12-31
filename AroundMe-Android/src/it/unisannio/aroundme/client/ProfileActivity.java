package it.unisannio.aroundme.client;

import java.util.ArrayList;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.client.async.AsyncQueue;
import it.unisannio.aroundme.client.async.FutureListener;
import it.unisannio.aroundme.client.async.ListenableFuture;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.view.MenuInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 *
 */
public class ProfileActivity extends FragmentActivity implements FutureListener<User>, OnCancelListener {
	private AsyncQueue async;
	private AsyncQueue pictureAsync;

	private GridView grdInterests;
	private TextView txtName;
	private TextView txtCompatibility;
	private TextView txtDistance;
	private ImageView imgPhoto;
	
	private ProgressDialog progress;
	private long userId;

	private ListenableFuture<User> task;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profileview);

		// User ID dell' utente da visualizzare
		userId = (Long) getIntent().getExtras().get("userId");
		
		txtName = (TextView) findViewById(R.id.txtName);
		imgPhoto=(ImageView) findViewById(R.id.imgPhoto);	
		txtCompatibility= (TextView)findViewById(R.id.txtCompatibility);
		txtDistance=(TextView)findViewById(R.id.txtDistance);
		grdInterests=(GridView)findViewById(R.id.grdInterests);
		
		async = new AsyncQueue();
    	pictureAsync = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
		
        asyncLoadUser();
	}
	
	private void asyncLoadUser() {
        progress = ProgressDialog.show(ProfileActivity.this, "", getString(R.string.loading), true, true);
        progress.setOnCancelListener(this);
        
		task = async.exec(UserQuery.single(userId), this);  
	}
	
	@Override
	public void onSuccess(User user) {
		Identity me = Identity.get();
		
		txtName.setText(user.getName());
		
		if(me != null && me.getId() != userId) {
			txtDistance.setText(String.format("%.1f m", me.getDistance(user)));
			txtCompatibility.setText(me.getCompatibilityRank(user)+" %");
		}
		
		ArrayList<Interest> interests = new ArrayList<Interest>(user.getInterests());
		grdInterests.setAdapter(new InterestAdapter(ProfileActivity.this, interests, pictureAsync));	
		
		 // TODO Al click di un interesse apre la pagina facebook di quell' interesse
		 // TODO Al click dell' icona di facebook mostra il profilo facebook della persona
		
		
		async.exec(Picture.get(userId), new FutureListener<Bitmap>() {
			@Override
			public void onSuccess(Bitmap object) {
				imgPhoto.setImageBitmap(object);
			}
			@Override
			public void onError(Exception e) {
				imgPhoto.setImageResource(R.drawable.img_error);
			}	
       });
		
		progress.dismiss();
	}
	
	@Override
	public void onError(Exception e) {
		progress.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// FIXME Externalize strings
		builder.setMessage("Problema di caricamento. Si vuole riprovare?")
		       .setCancelable(false)
		       .setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		        	   asyncLoadUser();
		           }
		       })
		       .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   finish();
		           }
		       });
		
		builder.create().show();
	}   
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.user_menu, menu);
	    return true;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if(task != null)
			task.cancel(true);
		
		finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		async.pause();
		pictureAsync.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		async.resume();
		pictureAsync.resume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		async.shutdown();
		pictureAsync.shutdown();
	}
}
