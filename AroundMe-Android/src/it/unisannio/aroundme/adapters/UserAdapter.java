package it.unisannio.aroundme.adapters;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.User;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserAdapter extends ArrayAdapter<User> {
	private static final int ITEM_RESOURCE = R.layout.list_entry;
	
	private User me;
	private AsyncQueue async; 

	public UserAdapter(Context context, User me, List<User> users, AsyncQueue async) {
		super(context, ITEM_RESOURCE, users);
		this.async = async;
		this.me = me;
	}
	
	/* 
	 * View Holder pattern: si minimizzano le chiamate a findViewById() (costose) memorizzando i riferimenti in un "holder".
	 */
	private static class ViewHolder {
		TextView txtName;
		TextView txtDistance;
		TextView txtCompatibility;
		ImageView imgPhoto;
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder h = null;
		
		if(view == null){
			LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
			view = layoutInflater.inflate(ITEM_RESOURCE, null);
			
			h = new ViewHolder();
			h.txtName = (TextView) view.findViewById(R.id.txtName);
			h.txtDistance = (TextView) view.findViewById(R.id.txtDistance);
			h.txtCompatibility = (TextView) view.findViewById(R.id.txtCompatibility);
			h.imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
			
			view.setTag(R.id.tag_viewholder, h);
		} else {
			h = (ViewHolder) view.getTag(R.id.tag_viewholder);
		}
		
			
		User user = getItem(position);	
		view.setTag(R.id.tag_user, user);
		
		h.txtName.setText(user.getName());
		// TODO Externalize strings
		int dist=(int) me.getDistance(user);
		if (dist!=-1){
			h.txtDistance.setText(String.format("%d m", dist));
		}else{
			h.txtDistance.setText("N/A");
		}		
		int rank=Math.round(me.getCompatibilityRank(user) * 100);
		if (rank!=-1){
			h.txtCompatibility.setText(String.format("%d%%", rank));
		}else{
			h.txtCompatibility.setText("N/A");
		}
		
		Picture.get(user.getId()).asyncUpdate(async, h.imgPhoto, R.drawable.img_downloading, R.drawable.img_error);
		
		
		return view;
	}

	
}