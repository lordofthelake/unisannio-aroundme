package it.unisannio.aroundme.activities;

import java.util.ArrayList;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.adapters.InterestAdapter;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.async.FutureListener;
import it.unisannio.aroundme.async.ListenableFuture;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.services.C2DMNotificationService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 */
public class ProfileActivity extends FragmentActivity implements 
FutureListener<User>, OnCancelListener,OnClickListener, OnItemClickListener {
	private AsyncQueue async;
	private AsyncQueue pictureAsync;

	private GridView grdInterests;
	private TextView txtName;
	private ImageView imgPhoto;
	private ImageView fbButton;
	private ArrayList<Interest> interests;
	
	
	private ProgressDialog progress;
	private long userId;

	private ListenableFuture<User> task;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profileview);

		// User ID dell' utente da visualizzare
		userId = (Long) getIntent().getExtras().get("userId");
		if(getIntent().getBooleanExtra("fromNotification", false)==true)
			C2DMNotificationService.markAsRead(userId);
		
		txtName = (TextView) findViewById(R.id.txtName);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);	
		grdInterests = (GridView) findViewById(R.id.grdInterests);
		fbButton=(ImageView) findViewById(R.id.imgfbProfile);
		
		fbButton.setOnClickListener(this);
		grdInterests.setOnItemClickListener(this);
		
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
		txtName.setText(user.getName());
		
		interests = new ArrayList<Interest>(user.getInterests());
		grdInterests.setAdapter(new InterestAdapter(ProfileActivity.this, interests, pictureAsync));	
		
		async.exec(Picture.get(userId), new FutureListener<Bitmap>() {
			@Override
			public void onSuccess(Bitmap object) {
				imgPhoto.setImageBitmap(object);
			}
			@Override
			public void onError(Throwable e) {
				imgPhoto.setImageResource(R.drawable.img_error);
			}	
       });
		
		progress.dismiss();
	}
	
	@Override
	public void onError(Throwable e) {
		progress.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(R.string.dialog_loadingerror_message)
		       .setCancelable(false)
		       .setPositiveButton(R.string.dialog_retry, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		        	   asyncLoadUser();
		           }
		       })
		       .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   finish();
		           }
		       });
		
		builder.create().show();
	}   
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    menu.findItem(R.id.profile).setVisible(false);
	    return true;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if(task != null)
			task.cancel(true);
		
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.toList:
	    	startActivity(new Intent(this, ListViewActivity.class));
	        return true;
	    case R.id.toMap:
	        startActivity(new Intent(this, MapViewActivity.class));
	        return true;
	    case R.id.preferences:
	    	startActivity(new Intent(this, PreferencesActivity.class));
	    	return true;
	    case R.id.profile:
	    	Intent i1 = new Intent(this, ProfileActivity.class);
	    	i1.putExtra("userId", Identity.get().getId());
	    	startActivity(i1);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
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

	@Override
	public void onClick(View arg0) {
		try{
			String url="fb://profile/"+ userId +"/wall";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}catch(Exception e){
			String url = "http://www.facebook.com/profile.php?id="+userId;
			Intent browser = new Intent(Intent.ACTION_VIEW);
			browser.setData(Uri.parse(url));
			startActivity(browser);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try{
			String url="fb://profile/"+ interests.get(arg2).getId() +"/wall";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}catch(Exception e){
			String url = "http://www.facebook.com/profile.php?id="+interests.get(arg2).getId();
			Intent browser = new Intent(Intent.ACTION_VIEW);
			browser.setData(Uri.parse(url));
			startActivity(browser);
		}	
	}
}
