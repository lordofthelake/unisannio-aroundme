package it.unisannio.aroundme.client;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.android.maps.MapActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class ListViewActivity extends DataActivity 
		implements OnItemClickListener {
	private final int MAX_DISTANCE=5000;
	private UserAdapter usrAdapter;
	private InterestFilterAdapter interestFilterAdapter;
	private SlidingDrawer drawer;
	private List<User> users;
	private List<Interest> myInterests;
	private ListView nearByList;
	private ListView interestsFilter;
	private ProgressDialog progress;
	private SeekBar seekDistance;
	private TextView txtDistanceFilter; 
    
    public void onItemClick(AdapterView<?> arg0, View v, int index,long id) {
		Intent intent = new Intent(ListViewActivity.this, ProfileActivity.class);
		intent.putExtra("userId", ((User) v.getTag(R.id.tag_user)).getId());
		startActivity(intent);				
	}
    
    @Override
    protected void onServiceConnected(DataService service) {
    	setContentView(R.layout.listview);
    	users = new ArrayList<User>();
    	myInterests=new ArrayList(Identity.get().getInterests());
        nearByList = (ListView) findViewById(R.id.nearByList);
        drawer=(SlidingDrawer) findViewById(R.id.filterDrawer);
        seekDistance=(SeekBar) findViewById(R.id.seekDistance);
        txtDistanceFilter=(TextView) findViewById(R.id.txtDistaceFilter);
        
        nearByList.setOnItemClickListener(this);
        seekDistance.setMax(this.MAX_DISTANCE);
        seekDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int distance= seekDistance.getProgress();
				if (distance<1000){
				txtDistanceFilter.setText(distance+" m");
				}else{
					txtDistanceFilter.setText(String.format("%.1f Km", (float)distance/1000));
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//TODO Salvare le impostazioni
				Toast.makeText(ListViewActivity.this, "Saving Dinstance", Toast.LENGTH_SHORT).show();
			}
        });
        seekDistance.setProgress(3000);  
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener(){
			@Override
			public void onDrawerOpened() {
				nearByList.setEnabled(false);
			}
        	
        });
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener(){
			@Override
			public void onDrawerClosed() {
				nearByList.setEnabled(true);
			}
        	
        });
        interestsFilter=(ListView) findViewById(R.id.listInterestFilter);
        
        progress = ProgressDialog.show(ListViewActivity.this, "", ListViewActivity.this.getString(R.string.loading), true, true);
    	nearByList.setAdapter(usrAdapter = new UserAdapter(ListViewActivity.this, Identity.get(), users, service));
    	//FIXME interestsFilter.setAdapter(interestFilterAdapter = new InterestFilterAdapter(ListViewActivity.this, myInterests, service));

        // TODO Mock loader. Replace with UserQuery
        // TODO Make cancelable
    	service.asyncDo(UserQuery.byId(1321813090L, 100000268830695L, 100001053949157L, 100000293335056L), new DataListener<Collection<User>>(){
        	 @Override
        		public void onData(Collection<User> object) {
        	    	Log.i("LIST", String.valueOf(object.size()));
        			progress.dismiss();
        			users.clear();
        			users.addAll(object);
        			usrAdapter.notifyDataSetChanged();
        		}
        	 @Override
        		public void onError(Exception e) {
        			progress.dismiss();
        			Toast.makeText(ListViewActivity.this, R.string.loadingError, Toast.LENGTH_LONG).show();	
        			e.printStackTrace();
        		}
        }); 	
    }
    
   
    // Prototipo
    private void createInterestDialog() {

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		String[] items = new String[100];
		boolean[] checked = new boolean[100];
		Arrays.fill(items, "Interesse");
		Arrays.fill(checked, true);
		b.setTitle("Seleziona interessi");
		b.setPositiveButton("Filtra", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}});
		b.setMultiChoiceItems(items, checked, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
			}});
		b.create().show();
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.toMap:
	        startActivity(new Intent(this, MapViewActivity.class));
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
}