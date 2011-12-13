package it.unisannio.aroundme.gui;


import it.unisannio.aroundme.R;
import it.unisannio.aroundme.utils.WebUtils;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NearByAdapter extends ArrayAdapter<NearByUser> {

	public NearByAdapter(Context context, int textViewResourceId,List<NearByUser> nearByUsers) {
		super(context, textViewResourceId, nearByUsers);
		this.nearByUsers=(ArrayList<NearByUser>) nearByUsers;
		this.textViewResourceId=textViewResourceId;
		this.context=context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout usersView = null;
	
		if(convertView == null){
			LayoutInflater layoutInflater = ((Activity)getContext()).getLayoutInflater();
			usersView = (LinearLayout) layoutInflater.inflate(textViewResourceId, null);
		}
		else 
			usersView = (LinearLayout) convertView;		
		//Inflating delle view
		TextView txtName = (TextView)usersView.findViewById(R.id.txtName);//nome e cognome
		TextView txtDinstance = (TextView)usersView.findViewById(R.id.txtDistance);//distanza
		TextView txtCompatibility= (TextView)usersView.findViewById(R.id.txtCompatibility);//compatibilit�
		ImageView imgPhoto = (ImageView)usersView.findViewById(R.id.imgPhoto);//foto		
		NearByUser user = getItem(position);				
		txtName.setText(user.getFirstName()+" "+ user.getLastName());
		txtDinstance.setText(user.getDinstance()+" m");
		txtCompatibility.setText(user.getAffinity()+"%");
		/*Caricamento dell' immagine del profilo
		String image_URL=user.getImageUrl();
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bm = WebUtils.LoadImage(image_URL, options);
		//Inserisco l'immagine ottenuta*/
		//imgPhoto.setImageBitmap(bm);	
		
		return usersView;
	}
	private ArrayList<NearByUser> nearByUsers;
	private int textViewResourceId;
	private Context context;
	
}
