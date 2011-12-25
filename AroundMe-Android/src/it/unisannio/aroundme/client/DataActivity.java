package it.unisannio.aroundme.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

public abstract class DataActivity extends FragmentActivity implements ServiceConnection {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindService(new Intent(this, DataService.class), this, Context.BIND_AUTO_CREATE);
	}
	
	protected void onServiceConnected(DataService service) {}
	
	public void onServiceConnected(ComponentName name, IBinder service) {
		onServiceConnected(((DataService.ServiceBinder)service).getService());
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {}
	
	@Override
	protected void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}
}
