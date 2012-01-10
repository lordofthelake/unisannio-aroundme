package it.unisannio.aroundme.activities;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.UserQueryFragment.OnQueryChangeListener;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.async.FutureListener;
import it.unisannio.aroundme.async.ListenableFuture;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import java.util.Collection;

import javax.xml.transform.TransformerException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class UserQueryExecutorFragment extends Fragment implements OnCancelListener, OnQueryChangeListener {
	public static interface UserQueryExecutionListener {
		void onUserQueryExecutionResults(Collection<User> results);
	}
	
	private UserQueryExecutionListener listener;
	
	private ListenableFuture<Collection<User>> task = null; 
	private ProgressDialog progress;
	
	private AsyncQueue async;
	
	private UserQuery userQuery;
	private boolean needsRefresh = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		async = new AsyncQueue();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if(task != null)
			task.cancel(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		async.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		async.resume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		async.shutdown();
	}
	
	private void notifyUserQueryExecutionListener(Collection<User> results) {
		if(listener != null)
			listener.onUserQueryExecutionResults(results);
	}
	
	public void setExecutionListener(UserQueryExecutionListener listener) {
		this.listener = listener;
	}

	public void refresh() {
		progress = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
    	progress.setOnCancelListener(this);
    	
		try {
			Log.d("UserQueryFragment", UserQuery.SERIALIZER.toString(userQuery));
		} catch (TransformerException e1) {}
		
		this.task = async.exec(userQuery, new FutureListener<Collection<User>>(){
        	@Override
        	public void onSuccess(Collection<User> object) {
        		task = null;
        		progress.dismiss();
        		notifyUserQueryExecutionListener(object);
        		needsRefresh = false;
        	}
        	@Override
        	public void onError(Throwable e) {
        		progress.dismiss();
        		Toast.makeText(getActivity(), R.string.loadingError, Toast.LENGTH_LONG).show();	
        		e.printStackTrace();
        	}
        });
		
	}
	
	public void refreshIfChanged() {
		if(needsRefresh)
			refresh();
	}
	
	// FIXME All'apertura la query non viene eseguita
	@Override
	public void onQueryChanged(UserQuery query) {
		userQuery = query;
		needsRefresh = true;
	}	
}
