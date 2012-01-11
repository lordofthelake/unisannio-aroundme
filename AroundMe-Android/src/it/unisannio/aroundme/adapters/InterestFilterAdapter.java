package it.unisannio.aroundme.adapters;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.UserQueryFragment;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.UserQuery;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 *
 */
// FIXME Stato delle checkbox degli interessi non persiste
public class InterestFilterAdapter extends ArrayAdapter<Interest> {
	private static final int ITEM_RESOURCE = R.layout.interest_filter_entry;
	private AsyncQueue async;
	private UserQuery userQuery;
	private UserQueryFragment fragment;

	public InterestFilterAdapter(Context context, UserQueryFragment f, List<Interest> interests, AsyncQueue async,UserQuery userQuery) {
		super(context, ITEM_RESOURCE, interests);
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
		LinearLayout entry;
	}
	
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder h;
		if(view == null){
			LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
			view = layoutInflater.inflate(ITEM_RESOURCE, null);
			h = new ViewHolder();
			h.txtMyInterest = (TextView) view.findViewById(R.id.txtMyInterest);
			h.imgMyInterest = (ImageView) view.findViewById(R.id.imgMyInterest);
			h.ckEnabled=(CheckBox) view.findViewById(R.id.checkUsed);
			h.entry=(LinearLayout) view.findViewById(R.id.entry);
			
			view.setTag(R.id.tag_viewholder, h);
		} else {
			h = (ViewHolder) view.getTag(R.id.tag_viewholder);
		}
		final ViewHolder h1=h;
		
		final Interest interest = getItem(position);	
		view.setTag(R.id.tag_interest, interest);				
		h.entry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				h1.ckEnabled.setChecked(!h1.ckEnabled.isChecked());
				if (h1.ckEnabled.isChecked())
					userQuery.addInterestId(interest.getId());
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
