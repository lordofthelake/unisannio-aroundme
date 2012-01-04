package it.unisannio.aroundme.adapters;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.UserQuery;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 *
 */
public class InterestFilterAdapter extends ArrayAdapter<Interest> {
	private static final int ITEM_RESOURCE = R.layout.interest_filter_entry;
	private AsyncQueue async;
	private Context context;
	private UserQuery userQuery;

	public InterestFilterAdapter(Context context, List<Interest> interests, AsyncQueue async,UserQuery userQuery) {
		super(context, ITEM_RESOURCE, interests);
		this.context=context;
		this.async = async;
		this.userQuery=userQuery;
	}
	
	/* 
	 * View Holder pattern: si minimizzano le chiamate a findViewById() (costose) memorizzando 
	 * i riferimenti in un "holder".
	 */
	private static class ViewHolder {
		TextView txtMyInterest;
		CheckBox ckEnabled;
		ImageView imgMyInterest;
	}
	
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder h = null;
		
		if(view == null){
			LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
			view = layoutInflater.inflate(ITEM_RESOURCE, null);
			h = new ViewHolder();
			h.txtMyInterest = (TextView) view.findViewById(R.id.txtMyInterest);
			h.imgMyInterest = (ImageView) view.findViewById(R.id.imgMyInterest);
			h.ckEnabled=(CheckBox) view.findViewById(R.id.checkUsed);
			view.setTag(R.id.tag_viewholder, h);
		} else {
			h = (ViewHolder) view.getTag(R.id.tag_viewholder);
		}
		Interest interest = getItem(position);	
		view.setTag(R.id.tag_interest,interest);
		h.ckEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				long interestId = getItem(position).getId();
				if (isChecked){
					if (!isQueried(interestId)){
						userQuery.addId(interestId);
					}
				}else{
					//Non deve essere in UserQuery
					userQuery=InterestFilterAdapter.this.removeInterestId(interestId);
					
				}
				UserQuery tmpQuery=ModelFactory.getInstance().createUserQuery();
			}
		});
		h.txtMyInterest.setText(interest.getName());
		h.ckEnabled.setChecked(isQueried(interest.getId()));
		Picture.get(interest.getId()).asyncUpdate(async, h.imgMyInterest, R.drawable.img_downloading, R.drawable.img_error);
		return view;
	}
	
	private UserQuery removeInterestId(long interestId){
		UserQuery retUserQuery =ModelFactory.getInstance().createUserQuery();
		ArrayList <Long> queriedInterests= new ArrayList<Long>(userQuery.getInterestIds());
		for (int i=0;i<queriedInterests.size();i++){
			if (!queriedInterests.get(i).equals(new Long(interestId))){
				//inserisce in userQuery solo gli id diversi
				retUserQuery.addInterestId(queriedInterests.get(i));
			}
		}
		retUserQuery.setNeighbourhood(userQuery.getNeighbourhood());
		retUserQuery.setCompatibility(userQuery.getCompatibility());
		return retUserQuery;
	}
	
	private boolean isQueried(long interestId){
		ArrayList<Long> queriedInterests = new ArrayList<Long>(userQuery.getInterestIds());
		for (int i=0;i<queriedInterests.size();i++){
			if (queriedInterests.get(i).equals(new Long(interestId)))
				return true;
		}
		return false;
	}
}
