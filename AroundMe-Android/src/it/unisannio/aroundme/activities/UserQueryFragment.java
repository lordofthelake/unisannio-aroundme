package it.unisannio.aroundme.activities;

import java.util.ArrayList;
import java.util.List;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.adapters.ArrayPagerAdapter;
import it.unisannio.aroundme.adapters.InterestFilterAdapter;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.*;
import it.unisannio.aroundme.widgets.SliderView;
import it.unisannio.aroundme.widgets.SliderView.OnChangeListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class UserQueryFragment extends Fragment implements OnDrawerCloseListener, OnDrawerOpenListener {
	public static interface OnQueryChangeListener {
		void onQueryChanged(UserQuery query);
	};
	
	private AsyncQueue async;
	private UserQuery userQuery;
	private Identity me;
	
	private SlidingDrawer drawer;
	private ImageView icDrawer;

	private SliderView distance;
	private SliderView rank;
	private InterestFilterAdapter interestFilterAdapter;
	
	private OnDrawerCloseListener onDrawerCloseListener;
	private OnDrawerOpenListener onDrawerOpenListener;
	
	private OnQueryChangeListener onQueryChangeListener;
	
	private List<Interest> myInterests;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		me = Identity.get();
		myInterests = new ArrayList<Interest>(me.getInterests());
    	async = new AsyncQueue(Setup.PICTURE_CONCURRENCY, Setup.PICTURE_KEEPALIVE);
    	
    	userQuery = ModelFactory.getInstance().createUserQuery();

    	Log.d("UserQueryFragment", "Creating UserQuery from default values");

		
		Position position = Identity.get().getPosition();
		Neighbourhood neighbourhood = new Neighbourhood(position, Setup.FILTERS_DEFAULT_RADIUS); 
		userQuery.setCompatibility(new Compatibility(Identity.get().getId(), Setup.FILTERS_DEFAULT_RANK)); 
		
		userQuery.setNeighbourhood(neighbourhood);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        drawer = (SlidingDrawer) inflater.inflate(R.layout.filters_drawer, container, false);
        drawer.setOnDrawerCloseListener(this);
        drawer.setOnDrawerOpenListener(this);
        icDrawer=(ImageView) drawer.findViewById(R.id.filterIcon);
        
        ViewPager pager = (ViewPager) drawer.findViewById(R.id.pager);
        
        View page1 = inflater.inflate(R.layout.filters_page_nearby, null);
        View page2 = inflater.inflate(R.layout.filters_page_interests, null);
        pager.setAdapter(new ArrayPagerAdapter(page1, page2));
        
        
        distance = (SliderView) page1.findViewById(R.id.sliderDistance);
        distance.setMultipliedValue((int) userQuery.getNeighbourhood().getRadius());
        distance.setOnChangeListener(new OnChangeListener() {

			@Override
			public void onSliderChanged(SliderView view) {
				Position position = Identity.get().getPosition();
				Neighbourhood neighbourhood = new Neighbourhood(position, view.getMultipliedValue());	
				userQuery.setNeighbourhood(neighbourhood);
				notifyQueryChangeListener();
			}
		});
        
        rank = (SliderView) page1.findViewById(R.id.sliderCompatibility);
        rank.setConvertedValue(userQuery.getCompatibility().getRank());
        rank.setOnChangeListener(new OnChangeListener() {
			
			@Override
			public void onSliderChanged(SliderView view) {
				long id = Identity.get().getId();
				Compatibility compatibility = new Compatibility(id, view.getConvertedValue());
				userQuery.setCompatibility(compatibility);
				notifyQueryChangeListener();
			}
		});
        
        ListView interestsFilter=(ListView) page2.findViewById(R.id.listInterestFilter);
        interestFilterAdapter = new InterestFilterAdapter(getActivity(), this, myInterests, async, userQuery);
        interestsFilter.setAdapter(interestFilterAdapter); 
        interestsFilter.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CheckBox ck =(CheckBox) arg1.findViewById(R.id.checkUsed);
				ck.setChecked(!ck.isChecked());
			}
        });
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
		
		try {
			SharedPreferences queryState = getSupportActivity().getSharedPreferences("QueryState", 0);
			SharedPreferences.Editor editor = queryState.edit();
			editor.putString("UserQuery", UserQuery.SERIALIZER.toString(userQuery));
			editor.commit();
			Log.d("UserQueryFragment", "Persisted UserQuery: " + UserQuery.SERIALIZER.toString(userQuery));
		} catch (Exception e) {
			Log.w("UserQueryFragment", "UserQuery cannot be persisted", e);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		async.resume();
		
		try {
			SharedPreferences queryState = getSupportActivity().getSharedPreferences("QueryState", 0);
			String state = queryState.getString("UserQuery", null);
			if(state != null) {
				Log.d("UserQueryFragment", "state: "+state);
				userQuery = UserQuery.SERIALIZER.fromString(state);
			
				Neighbourhood n = userQuery.getNeighbourhood();
				if(n != null)
					distance.setMultipliedValue(n.getRadius());
				
				Compatibility c = userQuery.getCompatibility();
				rank.setConvertedValue(c == null ? 0.0f : c.getRank());
				
				interestFilterAdapter.notifyDataSetChanged();
				Log.d("UserQueryFragment", "Restored UserQuery from saved state ");
				Log.d("UserQueryFragment", UserQuery.SERIALIZER.toString(userQuery));
			}
			
			notifyQueryChangeListener();
		} catch (Exception e) {
			Log.w("UserQueryFragment", "UserQuery can't be restored", e);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		async.shutdown();
	}

	@Override
	public void onDrawerOpened() {
		if(onDrawerOpenListener != null)
			onDrawerOpenListener.onDrawerOpened();
		icDrawer.setImageResource(R.drawable.ic_menu_drawer_top);
	}

	@Override
	public void onDrawerClosed() {
		if(onDrawerCloseListener != null)
			onDrawerCloseListener.onDrawerClosed();
		icDrawer.setImageResource(R.drawable.ic_menu_drawer_bottom);

	}
}
