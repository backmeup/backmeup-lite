package org.backmeup.plugins.connectors.dropbox;

import java.util.Properties;

import org.backmeup.api.exceptions.InvalidKeyException;

import play.Configuration;
import play.Play;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

/**
 * This Helper class constructs and configures the DropboxAPI element.
 * It uses dropbox.properties found within the bundles jar file
 * to retrieve the access token + secret token.
 * 
 * @author fschoeppl
 *
 */
public class DropboxHelper {
	
	private static final Configuration config = Play.application().configuration();
	
	public static final String PROPERTY_TOKEN = "dropbox.app.key";
	
	public static final String PROPERTY_SECRET = "dropbox.app.secret";
	
	private String appKey = config.getString(PROPERTY_TOKEN);

	private String appSecret = config.getString(PROPERTY_SECRET);
	
	public static DropboxHelper getInstance() {
		return new DropboxHelper();
	}

	public WebAuthSession getWebAuthSession() {
		AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
		return new WebAuthSession(appKeys, AccessType.DROPBOX);
	}
	
	public static DropboxAPI<WebAuthSession> getApi(Properties items) {		
		String token = items.getProperty(DropboxHelper.PROPERTY_TOKEN);
		String secret = items.getProperty(DropboxHelper.PROPERTY_SECRET);
		
		WebAuthSession session = DropboxHelper.getInstance().getWebAuthSession();
		session.setAccessTokenPair(new AccessTokenPair(token, secret));
		if (!session.isLinked()) {
			throw new InvalidKeyException("org.backmeup.dropbox", "userToken, userSecret", token + ", " + secret, "dropbox.properties");
		}		
		return new DropboxAPI<WebAuthSession>(session);
	}
	
}