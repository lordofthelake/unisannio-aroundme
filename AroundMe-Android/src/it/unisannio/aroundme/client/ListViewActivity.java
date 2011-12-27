package it.unisannio.aroundme.client;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import android.app.ProgressDialog;
import android.content.Intent;
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
    
    	list.setAdapter(adapter = new UserAdapter(ListViewActivity.this, service.getMe(), users, service));
        
        // TODO Mock loader. Replace with UserQuery
        // TODO Make cancelable
        service.asyncDo(
        		new Callable<Collection<User>>() {
					@Override
					public Collection<User> call() throws Exception {
						ArrayList<User> users = new ArrayList<User>();
				        ModelFactory f = ModelFactory.getInstance();
				        Collection<Interest> empty = Collections.emptySet();
				        users.add(f.createUser(1, "Tizio Caio", empty));
				        users.add(f.createUser(1, "Caio Sempronio", empty));
				        
				        Thread.sleep(2000);
				        
				        return users;
					}
				}, this);
    }
    
    @Override
	public void onData(Collection<User> object) {
		progress.dismiss();
		users.clear();
		users.addAll(object);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onError(Exception e) {
		progress.dismiss();
		Toast.makeText(ListViewActivity.this, R.string.loadingError, Toast.LENGTH_LONG);	
	}
}