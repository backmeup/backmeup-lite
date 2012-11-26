package org.backmeup.plugins.connectors.twitter;

import play.Configuration;
import play.Play;

/**
 * TwitterHelper offers application key and secret
 * @author user mmurauer
 *
 */
public class TwitterHelper {
	
	private static final Configuration config = Play.application().configuration();

	public static final String PROPERTY_TOKEN = "token";
	public static final String PROPERTY_SECRET = "secret";
	public static final String PROPERTY_APP_KEY = "twitter.app.key";
	public static final String PROPERTY_APP_SECRET = "twitter.app.secret";
	
	private String appKey = config.getString(PROPERTY_APP_KEY);
	private String appSecret = config.getString(PROPERTY_APP_SECRET);

	public static TwitterHelper getInstance() {
		return new TwitterHelper();
	}

	public String getAppKey() {
		return appKey;
	}
	
	public String getAppSecret() {
		return appSecret;
	}
}
