package it.unisannio.aroundme.client;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public interface Constants {
	final String FACEBOOK_APP_ID = "298477283507880";
	
	final String PICTURE_SRC = "http://graph.facebook.com/%d/picture";
	
	final String MODEL_HOST = "https://aroundme-backend.appspot.com";
	
	final String MODEL_PATH_USER = "/user/";
	
	final String MODEL_PATH_POSITION = "/user/%d/position";
	
	final int CACHE_USER_SIZE = 20;

	final String AUTH_HEADER = "X-AccessToken";
}
