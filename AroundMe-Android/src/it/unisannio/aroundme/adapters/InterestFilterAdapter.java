/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.adapters;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.UserQueryFragment;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.UserQuery;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Adapter per mostrare in una {@code ListView} una lista di interessi da usare come filtri in una query.
 * 
 * @author Marco Magnetti <marcomagnetti@gmail.com>
 */
public class InterestFilterAdapter extends ArrayAdapter<Interest> {
	private static final int ITEM_RESOURCE = R.layout.interest_filter_entry;
	
	private AsyncQueue async;
	private UserQueryFragment fragment;
	
	/**
	 * Crea una nuova istanza associata alla lista indicata e con una {@link AsyncQueue} per il download delle immagini.
	 * 
	 * @param fragment lo {@link UserQueryFragment} responsabile di comporre la query
	 * @param interests la lista di interessi da mostrare
	 * @param async una {@link AsyncQueue} per il download delle immagini
	 */
	public InterestFilterAdapter(UserQueryFragment fragment, List<Interest> interests, AsyncQueue async) {
		super(fragment.getActivity(), ITEM_RESOURCE, interests);
		this.async = async;
		this.fragment = fragment;
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
		ViewHolder h;
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
		h.ckEnabled.setTag(interest.getId());	
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((CheckBox) arg0.findViewById(R.id.checkUsed)).toggle();
			}
		});
		
		
		h.txtMyInterest.setText(interest.getName());
		h.ckEnabled.setChecked(fragment.getUserQuery().getInterestIds().contains(interest.getId()));
		h.ckEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				UserQuery query = fragment.getUserQuery();
				long id = (Long) buttonView.getTag();
				if (buttonView.isChecked())
					query.addInterestId(id);
				else
					query.removeInterestId(id);
				
				fragment.notifyQueryChanged();	
			}
			
		});
		
		Picture.get(interest.getId()).asyncUpdate(async, h.imgMyInterest, R.drawable.img_downloading, R.drawable.img_error);
		return view;
	}
}
