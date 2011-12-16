package it.unisannio.aroundme.client;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.User;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
	private PictureStore pictures;

	public UserAdapter(Context context, User me, List<User> users, PictureStore pictures) {
		super(context, ITEM_RESOURCE, users);
		this.pictures = pictures;
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
		h.txtDistance.setText(String.format("%.1f m", me.getDistance(user)));
		h.txtCompatibility.setText(String.format("%d%%", Math.round(me.getCompatibilityRank(user) * 100)));
		
		final ImageView imgPhoto = h.imgPhoto;
		
		pictures.get(user.getId()).load(new DataListener<Bitmap>() {

			@Override
			public void onData(Bitmap object) {
				imgPhoto.setImageBitmap(object);
			}

			@Override
			public void onError(Exception e) {}
			
		});
		
		return view;
	}

	
}
