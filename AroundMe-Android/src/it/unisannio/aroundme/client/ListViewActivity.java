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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class ListViewActivity extends DataActivity 
		implements OnItemClickListener, DataListener<Collection<User>> {

	private UserAdapter adapter;
	private List<User> users;
	private ListView list;
	
	private ProgressDialog progress;
    
    public void onItemClick(AdapterView<?> arg0, View v, int index,long id) {
		Intent intent = new Intent(ListViewActivity.this, ProfileActivity.class);
		intent.putExtra("userId", ((User) v.getTag(R.id.tag_user)).getId());
		startActivity(intent);				
	}
    
    @Override
    protected void onServiceConnected(DataService service) {
    	users = new ArrayList<User>();
    	setContentView(R.layout.listview);
        list = (ListView) findViewById(R.id.nearByList);
        list.setOnItemClickListener(this);
        
        progress = ProgressDialog.show(ListViewActivity.this, "", ListViewActivity.this.getString(R.string.loading), true, true);
    
    	list.setAdapter(adapter = new UserAdapter(ListViewActivity.this, Identity.get(), users, service));
        
        // TODO Make cancelable

        service.asyncDo(UserQuery.byId(1321813090L, 100000268830695L, 100001053949157L, 100000293335056L), this);
        
    }
    
    @Override
	public void onData(Collection<User> object) {
    	Log.i("LIST", String.valueOf(object.size()));
		progress.dismiss();
		users.clear();
		users.addAll(object);
		adapter.notifyDataSetChanged();
		
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
	public void onError(Exception e) {
		progress.dismiss();
		Toast.makeText(ListViewActivity.this, R.string.loadingError, Toast.LENGTH_LONG).show();	
		e.printStackTrace();
	
	}
}