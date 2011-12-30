package it.unisannio.aroundme.client;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.model.User;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */ 
public class LoginActivity extends DataActivity implements DataListener<Identity>{
	
	private Facebook facebook = new Facebook(Constants.FACEBOOK_APP_ID);
	String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;
	
	@Override
	protected void onServiceConnected(final DataService service) {
		setContentView(R.layout.login);
		Button btnFacebookConnect = (Button) findViewById(R.id.btnFacebookConnect);
		
		mPrefs = getPreferences(MODE_PRIVATE);
        final String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        long id = mPrefs.getLong("userId", 0);
        
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        if(facebook.isSessionValid()) {
        	//service.asyncDo(Identity.login(id, access_token), this);
        }
	
		btnFacebookConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				facebook.authorize(LoginActivity.this, new String[] { "offline_access", "user_likes" }, new DialogListener() {

					@Override
					public void onComplete(Bundle values) {
						Toast.makeText(getApplicationContext(), "On complete", Toast.LENGTH_LONG).show();
						
	                    service.asyncDo(Identity.create(facebook), new DataListener<User>() {

							@Override
							public void onData(User object) {
								SharedPreferences.Editor editor = mPrefs.edit();
			                    editor.putString("access_token", facebook.getAccessToken());
			                    editor.putLong("access_expires", facebook.getAccessExpires());
			                    editor.putLong("userId", object.getId());
			                    editor.commit();
			                    
			                    ((Application) getApplication()).addToCache(object);
			                    Identity.set(object, facebook.getAccessToken());
			                    startActivity(new Intent(LoginActivity.this, ListViewActivity.class));
			                	finish();
							}

							@Override
							public void onError(Exception e) {
								Toast.makeText(LoginActivity.this, "Errore", Toast.LENGTH_LONG).show();
								e.printStackTrace();
								
							}
	                    	
	                    });
	                    /* TODO
	                     * Fai una richiesta al server. Se l'utente c'è, ridireziona alla ListView.
	                     * Altrimenti alla pagina di registrazione.
	                     */
						startActivity(new Intent(LoginActivity.this, ListViewActivity.class));
						finish();
					}

					@Override
					public void onFacebookError(FacebookError e) {
						Toast.makeText(getApplicationContext(), "Si � verificato un errore durante l'autorizzazione: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onError(DialogError e) {
						Toast.makeText(getApplicationContext(), "On Error", Toast.LENGTH_LONG).show();
						
					}

					@Override
					public void onCancel() {
						Toast.makeText(getApplicationContext(), "On cancel", Toast.LENGTH_LONG).show();
						
					}
					
				});
			}
			
		});
	}
	
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }


	@Override
	public void onData(Identity object) {
		startActivity(new Intent(LoginActivity.this, ListViewActivity.class));
    	finish();
	}


	@Override
	public void onError(Exception e) {
		// TODO Auto-generated method stub
		
	}
    
}
