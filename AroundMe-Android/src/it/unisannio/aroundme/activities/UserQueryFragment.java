package it.unisannio.aroundme.activities;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.adapters.ArrayPagerAdapter;
import it.unisannio.aroundme.adapters.InterestFilterAdapter;
import it.unisannio.aroundme.adapters.UserAdapter;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.widgets.SliderView;
import it.unisannio.aroundme.widgets.SliderView.OnChangeListener;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class UserQueryFragment extends Fragment implements OnDrawerCloseListener, OnDrawerOpenListener {
	public static interface OnQueryChangeListener {
		void onQueryChanged(UserQuery query);
	};
	
	private AsyncQueue async;
	private UserQuery userQuery;
	private Identity me;
	
	private SlidingDrawer drawer;
	private OnDrawerCloseListener onDrawerCloseListener;
	private OnDrawerOpenListener onDrawerOpenListener;
	
	private OnQueryChangeListener onQueryChangeListener;
	
	private List<Interest> myInterests;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		me = Identity.get();
		myInterests = new ArrayList<Interest>(me.getInterests());
		
		if(savedInstanceState == null) {
    		// L'Activity è stata avviata per la prima volta tramite un Intent
    		long[] ids = getActivity().getIntent().getLongArrayExtra("userIds");
    		if(ids != null) {
        		userQuery = UserQuery.byId(ids);
        	}
    	} else { 
    		// Controlliamo se c'è uno stato salvato
    		String serializedQuery = savedInstanceState.getString("userQuery");
    		if(serializedQuery != null) {
    			try {
					userQuery = UserQuery.SERIALIZER.fromString(serializedQuery);
				} catch (SAXException e1) {
					Log.d("ListViewActivity", "Error deserializing UserQuery", e1);
				}
    		} 
    	}

    	if(userQuery == null) {
    		// Non Ã¨ stato possibile ricostruire la query. Usiamo le impostazioni di default
    		userQuery = ModelFactory.getInstance().createUserQuery();
    		/**Caricamento delle impostazioni di default
    		 * 	-posizione: 				attuale
    		 * 	-Raggio: 					1km
    		 * 	-compatibilità:				60%
    		 * 	-Interessi considerati:		tutti
    		 */
    		// FIXME
    		me.setPosition(ModelFactory.getInstance().createPosition(41.1309285, 14.7775555));
    		
    		Position position = Identity.get().getPosition();
    		Neighbourhood neighbourhood = new Neighbourhood(position, 1000); // FIXME	
    		userQuery.setCompatibility(new Compatibility(Identity.get().getId(), 0.6F)); // FIXME
    		
    		userQuery.setNeighbourhood(neighbourhood);
    	}
    	notifyQueryChangeListener();
    	async = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        drawer = (SlidingDrawer) inflater.inflate(R.layout.filters_drawer, container, false);
        drawer.setOnDrawerCloseListener(this);
        drawer.setOnDrawerOpenListener(this);
        
        ViewPager pager = (ViewPager) drawer.findViewById(R.id.pager);
        
        View page1 = inflater.inflate(R.layout.filters_page_nearby, null);
        View page2 = inflater.inflate(R.layout.filters_page_interests, null);
        pager.setAdapter(new ArrayPagerAdapter(page1, page2));
        
        SliderView distance = (SliderView) page1.findViewById(R.id.sliderDistance);
        distance.setOnChangeListener(new OnChangeListener() {

			@Override
			public void onSliderChanged(SliderView view) {
				Position position = Identity.get().getPosition();
				Neighbourhood neighbourhood = new Neighbourhood(position, view.getValue()*100);	
				userQuery.setNeighbourhood(neighbourhood);
				notifyQueryChangeListener();
			}
		});
        
        SliderView compatibility = (SliderView) page1.findViewById(R.id.sliderCompatibility);
        compatibility.setOnChangeListener(new OnChangeListener() {
			
			@Override
			public void onSliderChanged(SliderView view) {
				long id = Identity.get().getId();
				Compatibility compatibility = new Compatibility(id, view.getConvertedValue());
				userQuery.setCompatibility(compatibility);
				notifyQueryChangeListener();
			}
		});
        
        ListView interestsFilter=(ListView) page2.findViewById(R.id.listInterestFilter);
        interestsFilter.setAdapter(new InterestFilterAdapter(getActivity(), this, myInterests, async, userQuery)); 	
		
        return drawer;
	}
	
	public void notifyQueryChangeListener() {
    	if(onQueryChangeListener != null)
    		onQueryChangeListener.onQueryChanged(userQuery);
	}
	
	public UserQuery getUserQuery() {
		return userQuery;
	}
	
	public void setOnQueryChangeListener(OnQueryChangeListener l) {
		this.onQueryChangeListener = l;
	}
	
	public void setOnDrawerOpenListener(OnDrawerOpenListener l) {
		this.onDrawerOpenListener = l;
	}
	
	public void setOnDrawerCloseListener(OnDrawerCloseListener l) {
		this.onDrawerCloseListener = l;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		async.pause();
	}
	
	public void onResume() {
		super.onResume();
		async.resume();
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		async.shutdown();
	}

	@Override
	public void onDrawerOpened() {
		if(onDrawerOpenListener != null)
			onDrawerOpenListener.onDrawerOpened();
		
	}

	@Override
	public void onDrawerClosed() {
		if(onDrawerCloseListener != null)
			onDrawerCloseListener.onDrawerClosed();
	}
}
