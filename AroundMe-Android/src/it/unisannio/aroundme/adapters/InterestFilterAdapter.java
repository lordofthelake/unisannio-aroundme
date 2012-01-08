package it.unisannio.aroundme.adapters;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.UserQueryFragment;
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
	private UserQueryFragment fragment;

	public InterestFilterAdapter(Context context, UserQueryFragment f, List<Interest> interests, AsyncQueue async,UserQuery userQuery) {
		super(context, ITEM_RESOURCE, interests);
		this.context=context;
		this.async = async;
		this.userQuery=userQuery;
		this.fragment = f;
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
		
		final Interest interest = getItem(position);	
		view.setTag(R.id.tag_interest, interest);		
		
		h.ckEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					userQuery.addId(interest.getId());
				else
					userQuery.removeInterestId(interest.getId());
				
				fragment.notifyQueryChangeListener();
			}
		});
		
		h.txtMyInterest.setText(interest.getName());
		h.ckEnabled.setChecked(userQuery.getInterestIds().contains(interest.getId()));
		Picture.get(interest.getId()).asyncUpdate(async, h.imgMyInterest, R.drawable.img_downloading, R.drawable.img_error);
		return view;
	}
	
}
