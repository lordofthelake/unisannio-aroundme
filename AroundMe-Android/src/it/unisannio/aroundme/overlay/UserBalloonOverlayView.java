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

public class UserBalloonOverlayView extends BalloonOverlayView<UserOverlayItem> {

	private TextView title;
	private TextView snippet;
	private ImageView image;
	
	private AsyncQueue async;
	
	public UserBalloonOverlayView(Context context, int balloonBottomOffset, AsyncQueue async) {
		super(context, balloonBottomOffset);
		this.async = async;
	}
	
	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		
		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.user_balloon_overlay, parent);
		
		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);

		// implement balloon close
		ImageView close = (ImageView) v.findViewById(R.id.balloon_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				parent.setVisibility(GONE);
			}
		});
		
	}

	@Override
	protected void setBalloonData(UserOverlayItem item, ViewGroup parent) {
		
		// map our custom item data to fields
		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		
		// get remote image from network.
		// bitmap results would normally be cached, but this is good enough for demo purpose.
		Picture.get(item.getUser().getId()).asyncUpdate(async, image, R.drawable.img_downloading, R.drawable.img_error);
		
	}
}
