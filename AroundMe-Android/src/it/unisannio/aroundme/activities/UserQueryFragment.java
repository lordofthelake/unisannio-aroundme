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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
				Neighbourhood neighbourhood = new Neighbourhood(position, view.getValue()*100);	
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
				userQuery = UserQuery.SERIALIZER.fromString(state);
			
				Neighbourhood n = userQuery.getNeighbourhood();
				if(n != null)
					distance.setMultipliedValue((int) n.getRadius());
				
				Compatibility c = userQuery.getCompatibility();
				rank.setConvertedValue(c == null ? 0.0f : c.getRank());
				
				interestFilterAdapter.notifyDataSetChanged();
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
