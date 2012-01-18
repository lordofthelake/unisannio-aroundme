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
package it.unisannio.aroundme.overlay;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.Picture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**
 * View da usare come contenuto del balloon quando viene fatto il tapping su un marker.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserBalloonOverlayView extends BalloonOverlayView<UserOverlayItem> {

	private TextView title;
	private TextView snippet;
	private ImageView image;
	
	private AsyncQueue async;
	
	/**
	 * Crea una nuova istanza
	 * 
	 * @param context un Context per accedere alle risorse dell'applicazione
	 * @param balloonBottomOffset
	 * @param async una {@link AsyncQueue}, utilizzata per il download delle immagini
	 */
	public UserBalloonOverlayView(Context context, int balloonBottomOffset, AsyncQueue async) {
		super(context, balloonBottomOffset);
		this.async = async;
	}
	
	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.user_balloon_overlay, parent);
		
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);

		ImageView close = (ImageView) v.findViewById(R.id.balloon_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				parent.setVisibility(GONE);
			}
		});
		
	}

	@Override
	protected void setBalloonData(UserOverlayItem item, ViewGroup parent) {
		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		
		Picture.get(item.getUser().getId()).asyncUpdate(async, image, R.drawable.img_downloading, R.drawable.img_error);
	}
}
