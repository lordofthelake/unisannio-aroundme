package it.unisannio.aroundme.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WebUtils {
	public static Bitmap LoadImage(String URL, BitmapFactory.Options options){       
	    Bitmap bitmap = null;
	    InputStream in = null;       
	       try {
	           in = OpenHttpConnection(URL);
	           bitmap = BitmapFactory.decodeStream(in, null, options);
	           in.close();
	       } catch (IOException e1) {}
	       return bitmap;               
	 }	   
	 private static InputStream OpenHttpConnection(String strURL) throws IOException{
		 InputStream inputStream = null;
		 URL url = new URL(strURL);
		 URLConnection conn = url.openConnection();
		 System.out.println("Ottengo inputstream");
		 try{
			  HttpURLConnection httpConn = (HttpURLConnection)conn;
			  httpConn.setRequestMethod("GET");
			  httpConn.connect();
		  if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			  inputStream = httpConn.getInputStream();
		  }
		 }
		 catch (Exception ex)
		 {
		 }
		 return inputStream;
	}
}
