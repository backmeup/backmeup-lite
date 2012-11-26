package org.backmeup.plugins.connectors.facebook;

import play.Configuration;
import play.Play;

/**
 * offers application key and secret 
 * 
 * @author mmurauer
 *
 */
public class FacebookHelper {
	
	private static final Configuration config = Play.application().configuration();
	
	public static final String PROPERTY_APP_KEY = "facebook.app.key";
	
	public static final String PROPERTY_APP_SECRET = "facebook.app.secret";
	
	private String appKey = config.getString(PROPERTY_APP_KEY);
	private String appSecret = config.getString(PROPERTY_APP_SECRET);

	public static FacebookHelper getInstance() {
		return new FacebookHelper();
	}

	public String getAppKey() {
		return appKey;
	}
	
	public String getAppSecret() {
		return appSecret;
	}

}