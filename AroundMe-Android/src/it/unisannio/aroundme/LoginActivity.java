package it.unisannio.aroundme;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class LoginActivity extends Activity {
	
	private Facebook facebook = new Facebook(ApplicationConstants.FACEBOOK_APP_ID);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Button btnFacebookConnect = (Button) findViewById(R.id.btnFacebookConnect);
		final TextView txtAroundMe = (TextView) findViewById(R.id.textView1);
		btnFacebookConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				facebook.authorize(LoginActivity.this, new DialogListener() {

					@Override
					public void onComplete(Bundle values) {
						Toast.makeText(getApplicationContext(), "On complete", Toast.LENGTH_LONG).show();
						
					}

					@Override
					public void onFacebookError(FacebookError e) {
						Toast.makeText(getApplicationContext(), "Si è verificato un errore durante l'autorizzazione: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						txtAroundMe.setText(e.getLocalizedMessage());
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
}
