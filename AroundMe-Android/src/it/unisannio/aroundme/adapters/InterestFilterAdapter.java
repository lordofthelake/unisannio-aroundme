package it.unisannio.aroundme.adapters;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.client.async.AsyncQueue;
import it.unisannio.aroundme.model.Interest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

	public InterestFilterAdapter(Context context, List<Interest> interests, AsyncQueue async) {
		super(context, ITEM_RESOURCE, interests);
		this.context=context;
		this.async = async;
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
	
	public View getView(int position, View view, ViewGroup parent) {
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
		Toast.makeText(context, interest.getName(), 3);
		h.txtMyInterest.setText(interest.getName());
		Picture.get(interest.getId()).asyncUpdate(async, h.imgMyInterest, R.drawable.img_downloading, R.drawable.img_error);
		return view;
	}
}
