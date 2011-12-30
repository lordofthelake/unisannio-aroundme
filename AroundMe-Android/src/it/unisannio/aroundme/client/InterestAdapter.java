package it.unisannio.aroundme.client;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.Interest;

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

public class InterestAdapter extends ArrayAdapter<Interest> {
	private static final int ITEM_RESOURCE = R.layout.like_grid_elem;
	private DataService service;

	public InterestAdapter(Context context, List<Interest> interests, DataService service) {
		super(context, ITEM_RESOURCE, interests);
		this.service = service;
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
		h.txtInterest.setText(interest.getName());
		
		// FIXME Va resettata con un'immagine di default
		final ImageView imgInterest = h.imgInterest;
		
		/* FIXME Potenziale problema di concorrenza
		 * Le viste vengono riciclate, quindi il download dell'immagine potrebbe finire
		 * quando la vista è già stata riciclata e dovrebbe visualizzare qualche altra cosa.
		 */
		service.asyncDo(Picture.get(interest.getId()), new DataListener<Bitmap>() {
			@Override
			public void onData(Bitmap object) {
				imgInterest.setImageBitmap(object);
			}

			@Override
			public void onError(Exception e) {
				imgInterest.setImageResource(R.drawable.img_error);
			}
		});
		
		return view;
	}
}
