package it.unisannio.aroundme.adapters;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.Interest;

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
 * Adapter per mostrare una lista di interessi.
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 */
public class InterestAdapter extends ArrayAdapter<Interest> {
	private static final int ITEM_RESOURCE = R.layout.like_grid_elem;
	private static final int MAX_CHARS = 10;
	
	private AsyncQueue async;

	/**
	 * Crea una nuova istanza associata alla lista indicata e con una {@link AsyncQueue} per il download delle immagini.
	 * 
	 * @param context un Context per il recupero delle risorse
	 * @param interests la lista di interessi da mostrare
	 * @param async una {@link AsyncQueue} per il download delle immagini
	 */
	public InterestAdapter(Context context, List<Interest> interests, AsyncQueue async) {
		super(context, ITEM_RESOURCE, interests);
		this.async = async;
	}
	
	/* 
	 * View Holder pattern: si minimizzano le chiamate a findViewById() (costose) memorizzando i riferimenti in un "holder".
	 */
	private static class ViewHolder {
		TextView txtInterest;
		ImageView imgInterest;
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder h = null;
		
		if(view == null){
			LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
			view = layoutInflater.inflate(ITEM_RESOURCE, null);
			h = new ViewHolder();
			h.txtInterest = (TextView) view.findViewById(R.id.txtInterest);
			h.imgInterest = (ImageView) view.findViewById(R.id.imgInterest);	
			view.setTag(R.id.tag_viewholder, h);
		} else {
			h = (ViewHolder) view.getTag(R.id.tag_viewholder);
		}
		
			
		Interest interest = getItem(position);	
		view.setTag(R.id.tag_interest,interest);
		if (interest.getName().length()>MAX_CHARS){
			h.txtInterest.setText(interest.getName().substring(0, MAX_CHARS-3)+"...");
		}else{
			h.txtInterest.setText(interest.getName());
		}
		
		Picture.get(interest.getId()).asyncUpdate(async, h.imgInterest, R.drawable.img_downloading, R.drawable.img_error);
		
		return view;
	}
}
