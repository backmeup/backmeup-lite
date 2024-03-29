package org.backmeup.plugins.connectors.twitter;

import java.util.Properties;

import org.backmeup.api.auth.OAuthBased;
import org.backmeup.api.exceptions.PluginException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


/**
 * The TwitterAuthenticator creates a redirect URL based on
 * the app informations and stores Accesstoken in the inputProperties
 * 
 * @author mmurauer
 *
 */
public class TwitterAuthenticator implements OAuthBased {

	static RequestToken requestToken;
	
	@Override
	public AuthorizationType getAuthType() {
		return AuthorizationType.OAUTH;
	}

	@Override
	public String createRedirectURL(Properties inputProperties, String callback) {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			TwitterHelper th = TwitterHelper.getInstance();
			twitter.setOAuthConsumer(th.getAppKey(), th.getAppSecret());
			requestToken = twitter.getOAuthRequestToken(callback);
			return requestToken.getAuthorizationURL();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PluginException(TwitterDescriptor.TWITTER_ID, "An error occurred while retrieving authentication information", e);
		}
	}

	@Override
	public String postAuthorize(Properties inputProperties) {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			TwitterHelper th = TwitterHelper.getInstance();
			twitter.setOAuthConsumer(th.getAppKey(), th.getAppSecret());
			
			String verifier = inputProperties.getProperty("oauth_verifier");
			
			AccessToken at = twitter.getOAuthAccessToken(requestToken, verifier);
			inputProperties.setProperty(TwitterHelper.PROPERTY_TOKEN, at.getToken());
			inputProperties.setProperty(TwitterHelper.PROPERTY_SECRET, at.getTokenSecret());
			// Retrieve the twitter username based on the inputProperties and return it here
			return twitter.getScreenName();
		} catch (TwitterException e) {
			throw new PluginException(TwitterDescriptor.TWITTER_ID, "An error occurred during post authorization", e);
		}
		
	}

}