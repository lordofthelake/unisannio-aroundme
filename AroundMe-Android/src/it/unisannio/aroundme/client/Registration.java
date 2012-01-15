package it.unisannio.aroundme.client;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.Log;

import com.facebook.android.Facebook;

/**
 * Classe delegata all'importazione dei dati di un utente da Facebook.
 * 
 * La classe provvede alcuni metodi di utilit&agrave; per importare i dati necessari alla costituzione del profilo
 * dalle Graph API di Facebook e per la costruzione di un'interfaccia grafica che permetta di modificare cosa includere nel profilo, 
 * oltre che per registrare il nuovo profilo sul server di backend.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class Registration implements Callable<Identity> {
	
	/**
	 * Restituisce un task in grado di importare i dati necessari alla registrazione da Facebook.
	 * 
	 * @param fb un client Facebook con le autorizzazioni necessarie ad effettuare l'operazione
	 * @return un task in grado di importare i dati dalle Graph API
	 * 
	 * @see AsyncQueue#exec(Callable, it.unisannio.aroundme.async.FutureListener)
	 */
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
	
	/**
	 * Crea un nuovo processo di registrazione con i dati indicati.
	 * 
	 * @param id l'ID utente
	 * @param name il nome dell'utente
	 * @param interests l'insieme degli interessi dell'utente
	 * @param accessToken un token di autenticazione valido
	 * 
	 * @see #create(Facebook)
	 */
	protected Registration(long id, String name, Collection<Interest> interests, String accessToken) {
		this.id = id;
		this.name = name;
		this.interests = new LinkedHashMap<Interest, Boolean>();
		this.accessToken = accessToken;
		
		for(Interest i : interests) {
			this.interests.put(i, true);
		}
	}
	
	/**
	 * Restituisce gli interessi importati durante la creazione del processo.
	 * 
	 * @return una collezione di tutti gli interessi importati
	 */
	public Collection<Interest> getInterests() {
		return interests.keySet();
	}
	
	/**
	 * Restituisce il sottoinsieme degli interessi importati che l'utente ha deciso di includere nel suo profilo.
	 *
	 * @return una collezione degli interessi selezionati in fase di registrazione
	 */
	public Collection<Interest> getCheckedInterests() {
		Collection<Interest> checked = new LinkedList<Interest>();
		for(Map.Entry<Interest, Boolean> entry : interests.entrySet()) {
			if(entry.getValue()) checked.add(entry.getKey());
		}
		Log.d("Checked interests", checked.toString());
		return checked;
	}
	
	/**
	 * Restituisce il nome dell'utente importato, che verr&agrave; usato nella costituzione del suo profilo.
	 * 
	 * @return il nome dell'utente importato
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Restituisce la chiave d'accesso associata a questo utente.
	 * 
	 * @return la chiave d'accesso di questo utente
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Restituisce l'ID dell'utente importato.
	 * 
	 * @return l'ID dell'utente importato
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Crea un Dialog per permettere all'utente di scegliere cosa includere nel proprio profilo tra i dati importati.
	 * 
	 * @param ctx l'Activity che ha avviato il Dialog.
	 * @param onEditFinishListener un listener che ricever&agrave; la notifica che l'utente ha premuto il pulsante "Importa"
	 * @return il Dialog per modificare le preferenze importate
	 */
    public Dialog createInterestEditorDialog(final Activity ctx, OnClickListener onEditFinishListener) {

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
		b.setCancelable(true);
		b.setPositiveButton(R.string.dialog_import, onEditFinishListener);
		
		b.setMultiChoiceItems(names, checked, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checked[which] = isChecked;
				interests.put(items[which], isChecked);
			}
		});
		
		b.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				ctx.finish();
			}
		});
		
		return b.create();
    }

    /**
     * Crea il profilo sul server di backend, usando i dati importati e approvati dall'utente.
     * 
     * In caso di successo, l'identit&agrave; in uso viene impostata a quella appena creata.
     * <p>Essendo un'operazione bloccante, la modalit&agrave; d'uso &egrave; di utilizzare l'istanza
     * come task in una {@link AsyncQueue}. </p>
     * 
     * @return l'identit&agrave; creata
     * @throws Exception in caso di errori nella registrazione del profilo
     * 
     * @see Identity
     * @see AsyncQueue#exec(Callable, it.unisannio.aroundme.async.FutureListener)
     */
	@Override
	public Identity call() throws Exception {
		final User user = ModelFactory.getInstance().createUser(id, name, getCheckedInterests());
		final Identity identity = new Identity(user, accessToken);
		
		return (new HttpTask<Identity>(identity, "PUT", Setup.BACKEND_USER_URL_SIMPLE) {

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
