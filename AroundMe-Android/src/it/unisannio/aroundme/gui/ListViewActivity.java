package it.unisannio.aroundme.gui;


import it.unisannio.aroundme.R;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListViewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        ListView nearbyList=(ListView) findViewById(R.id.nearByList);
        //Utenti di prova
        users = new ArrayList<NearByUser>();
        users.add(new NearByUser("Marco", "Magnetti", 50,80, "347893as", 
        		"http://profile.ak.fbcdn.net/hprofile-ak-ash2/372499_100001053949157_1013155008_q.jpg", 
        		"http://www.facebook.com/marcomagnetti", 30.292334, 21.43039, null, null));
        users.add(new NearByUser("Pippo", "Rossi", 20,60, "wiuued", 
        		"http://images4.wikia.nocookie.net/__cb20080126164830/nonciclopedia/images/d/db/Scimmia_idiota.jpg", 
        		"http://www.facebook.com/pipporossi", 34.292334, 11.43039, null, null));
        users.add(new NearByUser("Ciro", "Verdi", 50,70, "3208370dh", 
        		"http://assets1.qypecdn.net/uploads/photos/0297/6526/2006_25200717_2520Le_2520Foto-2037_medium.jpg", 
        		"http://www.facebook.com/ciroverdi", 110.292334, 17.43039, null, null));
        users.add(new NearByUser("Danilo", "Iannelli", 0,90, "kfvgtykujh", 
        		"http://profile.ak.fbcdn.net/hprofile-ak-ash2/273519_100000268830695_5190621_q.jpg", 
        		"http://www.facebook.com/daniloiannelli", 30.292334, 21.43039, null, null));
        users.add(new NearByUser("Michele", "Piccirillo", 0,90, "fhljjvhy", 
        		"http://a4.sphotos.ak.fbcdn.net/hphotos-ak-snc6/196773_1737818937261_1592255374_1634344_3731687_n.jpg", 
        		"http://www.facebook.com/michelepiccirillo", 34.292334, 11.43039, null, null));
        users.add(new NearByUser("Giuseppe", "Fusco", 0,90, "jhgjlkguy", 
        		"http://a4.sphotos.ak.fbcdn.net/hphotos-ak-ash4/291875_138211209610945_100002662007028_191993_458510495_n.jpg", 
        		"http://www.facebook.com/giseppefusco", 110.292334, 17.43039, null, null));
        //--
        adapter = new NearByAdapter(this, R.layout.list_entry, users);
        /*nearbyList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int index,long id) {
				Intent intent = new Intent(this, ProfileViewActivity.class);
				intent.putExtra("user", (NearByUser) users.get(index));
				startActivity(intent);				
			}
		});*/
        nearbyList.setAdapter(adapter);
    }
    private ArrayList<NearByUser> users;
	private NearByAdapter adapter;
}