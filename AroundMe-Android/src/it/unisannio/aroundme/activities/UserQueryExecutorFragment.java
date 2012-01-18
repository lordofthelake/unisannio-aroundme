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
package it.unisannio.aroundme.activities;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.UserQueryFragment.OnQueryChangeListener;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.async.FutureListener;
import it.unisannio.aroundme.async.ListenableFuture;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import java.util.Collection;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Fragment con il compito di eseguire una {@link UserQuery} e di notificare l'Activity ospitante dei risultati.
 * 
 * @see MapViewActivity
 * @see ListViewActivity
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class UserQueryExecutorFragment extends Fragment implements OnCancelListener, OnQueryChangeListener {
	
	/**
	 * Listener che viene notificato qualora la query termini con esito positivo.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	public static interface UserQueryExecutionListener {
		/**
		 * Metodo che viene notificato dell'avvenuta esecuzione della query.
		 * 
		 * @param results i risultati dell'esecuzione
		 */
		void onUserQueryExecutionResults(Collection<User> results);
	}
	
	private UserQueryExecutionListener listener;
	
	private ListenableFuture<Collection<User>> task = null; 
	private ProgressDialog progress;
	
	private AsyncQueue async;
	
	private UserQuery userQuery;
	private boolean needsRefresh = false;
	
	private boolean ready = false;
	
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
		ready = true;
		if(needsRefresh)
			refresh();
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
	
	/**
	 * Imposta il listener che verr&agrave; notificato dell'avvenuta esecuzione di ogni query.
	 * 
	 * @param listener il listener da usare
	 * @see UserQueryExecutionListener
	 */
	public void setExecutionListener(UserQueryExecutionListener listener) {
		this.listener = listener;
	}

	/**
	 * Richiede al Fragment di eseguire l'ultima query impostata.
	 * 
	 * Un listener, se impostato con {@link #setExecutionListener(UserQueryExecutionListener)}, verr&agrave; notificato
	 * con i risultati in caso di completamento corretto della stessa.
	 */
	public void refresh() {
		if(!ready) {
			needsRefresh = true;
			return;
		}
		progress = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, true);
    	progress.setOnCancelListener(this);
    	
		Log.d("UserQueryExecutorFragment", userQuery.toString());
		
		this.task = async.exec(userQuery, new FutureListener<Collection<User>>(){
        	@Override
        	public void onSuccess(Collection<User> object) {
        		task = null;
        		progress.dismiss();
        		notifyUserQueryExecutionListener(object);
        		needsRefresh = false;
        		Log.i("UserQueryExecutorFragment", "Query completed with " + object.size() + " results.");
        	}
        	@Override
        	public void onError(Throwable e) {
        		progress.dismiss();
        		Toast.makeText(getActivity(), R.string.loadingError, Toast.LENGTH_LONG).show();	
        		Log.w("UserQueryExecutorFragment", "Query completed with errors", e);
        	}
        });
		
	}
	
	/**
	 * Richiede al Fragment di eseguire l'ultima query impostata, se questa &egrave; cambiata dall'ultima 
	 * chiamata di {@link #refresh()}.
	 * 
	 * @see #refresh()
	 */
	public void refreshIfChanged() {
		if(needsRefresh)
			refresh();
	}
	
	/**
	 * Riceve una notifica in caso di cambiamenti della query da eseguire, ad esempio in seguito di modifiche
	 * effettuate da uno {@link UserQueryFragment}.
	 */
	@Override
	public void onQueryChanged(UserQuery query) {
		boolean needsRefreshNow = (userQuery == null);
		
		userQuery = query;
		needsRefresh = true;
		
		if(needsRefreshNow)
			refresh();
	}	
}
