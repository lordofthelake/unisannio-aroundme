/**
 * 
 */
package it.unisannio.aroundme.client.model;

import java.io.ByteArrayOutputStream;

import it.unisannio.aroundme.model.DataListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class BitmapClient implements Client<Bitmap> {
	private static class BytesToBitmapListener implements DataListener<byte[]> {
		private DataListener<Bitmap> bmpListener;
		
		BytesToBitmapListener(DataListener<Bitmap> bmpListener) {
			this.bmpListener = bmpListener;
		}
		@Override
		public void onData(byte[] object) {
			bmpListener.onData(BitmapFactory.decodeByteArray(object, 0, object.length));		
		}

		@Override
		public void onError(Exception e) {
			bmpListener.onError(e);
		}
		
	}
	
	private RawClient service;
	
	public BitmapClient(String endpoint) {
		this.service = new RawClient(endpoint);
	}
	@Override
	
	public void get(String path, DataListener<Bitmap> listener) {
		service.get(path, new BytesToBitmapListener(listener));
	}

	@Override
	public void put(String path, Bitmap data, DataListener<Bitmap> listener) {
		service.put(path, bitmapToBytes(data), new BytesToBitmapListener(listener));
	}

	@Override
	public void post(String path, Bitmap data, DataListener<Bitmap> listener) {
		service.post(path, bitmapToBytes(data), new BytesToBitmapListener(listener));
	}

	@Override
	public void delete(String path, DataListener<Bitmap> listener) {
		service.delete(path, new BytesToBitmapListener(listener));
	}

	private byte[] bitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, bout);
		
		return bout.toByteArray();
	}
}
