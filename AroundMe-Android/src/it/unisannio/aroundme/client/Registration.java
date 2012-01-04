package it.unisannio.aroundme.client;

import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.Log;

import com.facebook.android.Facebook;

public class Registration implements Callable<Identity> {
	public static Callable<Registration> create(final Facebook fb) {
		return new Callable<Registration>() {

			@Override
			public Registration call() throws Exception {
				ModelFactory f = ModelFactory.getInstance();
				
				JSONObject likes = (JSONObject) new JSONTokener(fb.request("me/likes")).nextValue();
				JSONArray data = likes.getJSONArray("data");
				
				Collection<Interest> interests = new HashSet<Interest>();
				for(int i = 0, len = data.length(); i < len; ++i) {
					JSONObject like = data.getJSONObject(i);
					Interest interest = f.createInterest(like.getLong("id"), like.getString("name"), like.getString("category"));
					interests.add(interest);
				}
				
				JSONObject me = (JSONObject) new JSONTokener(fb.request("me")).nextValue();
				return new Registration(me.getLong("id"), me.getString("name"), interests, fb.getAccessToken());
			}
			
		};
	}
	
	private final long id;
	private final String name;
	private final LinkedHashMap<Interest, Boolean> interests;
	private final String accessToken;
	
	protected Registration(long id, String name, Collection<Interest> interests, String accessToken) {
		this.id = id;
		this.name = name;
		this.interests = new LinkedHashMap<Interest, Boolean>();
		this.accessToken = accessToken;
		
		for(Interest i : interests) {
			this.interests.put(i, true);
		}
	}
	
	public Collection<Interest> getInterests() {
		return interests.keySet();
	}
	
	public Collection<Interest> getCheckedInterests() {
		Collection<Interest> checked = new LinkedList<Interest>();
		for(Map.Entry<Interest, Boolean> entry : interests.entrySet()) {
			if(entry.getValue()) checked.add(entry.getKey());
		}
		Log.d("Checked interests", checked.toString());
		return checked;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public long getId() {
		return id;
	}
	
    public Dialog createInterestEditorDialog(Context ctx, OnClickListener onEditFinishListener) {
    	// FIXME Externalize strings
		AlertDialog.Builder b = new AlertDialog.Builder(ctx);
		
		final Interest[] items = new Interest[interests.size()];
		String[] names = new String[interests.size()];
		final boolean[] checked = new boolean[interests.size()];
		
		int i = 0;
		for(Map.Entry<Interest, Boolean> entry : interests.entrySet()) {
			items[i] = entry.getKey();
			names[i] = items[i].getName();
			checked[i] = entry.getValue();
			++i;
		}

		b.setTitle(getName());
		b.setCancelable(false);
		b.setPositiveButton("Importa", onEditFinishListener);
		
		b.setMultiChoiceItems(names, checked, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checked[which] = isChecked;
				interests.put(items[which], isChecked);
			}
		});
		
		return b.create();
    }

	@Override
	public Identity call() throws Exception {
		final User user = ModelFactory.getInstance().createUser(id, name, getCheckedInterests());
		final Identity identity = new Identity(user, accessToken);
		
		return (new HttpTask<Identity>(identity, "PUT", Setup.BACKEND_USER_URL) {

			@Override
			protected Identity read(InputStream in) throws Exception {
				Identity.set(identity);
				return identity;
			}
			
			@Override
			protected void write(OutputStream out) throws Exception {
				User.SERIALIZER.write(user, out);
			}
			
		}).call();
	}

}
