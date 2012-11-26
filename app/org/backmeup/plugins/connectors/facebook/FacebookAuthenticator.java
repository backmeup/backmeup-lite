package org.backmeup.plugins.connectors.facebook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import org.backmeup.api.auth.OAuthBased;
import org.backmeup.api.exceptions.PluginException;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

/**
 * The FacebookAuthenticator creates a redirect URL based on
 * the app informations and stores Accesstoken in the inputProperties
 * 
 * @author mmurauer
 * 
 */
public class FacebookAuthenticator implements OAuthBased {

	@Override
	public AuthorizationType getAuthType() {
		return AuthorizationType.OAUTH;
	}

	@Override
	public String createRedirectURL(Properties inputProperties, String callback) {
		if (!callback.endsWith("/"))
			callback += "/";
		inputProperties.setProperty("callback", callback);
		
		FacebookHelper fh = FacebookHelper.getInstance();
		
		return "https://www.facebook.com/dialog/oauth?client_id=" + fh.getAppKey() +
				"&return_session=false&redirect_uri=" + callback + "&scope=" +
				"user_birthday,user_photos,read_stream,user_about_me,user_activities," +
				"user_education_history,user_events,user_groups,user_hometown,user_interests" +
				",user_likes,user_location,user_notes,user_questions,user_relationships," +
				"user_relationship_details,user_religion_politics,user_status," +
				"user_subscriptions,user_videos,user_website,user_work_history,email," +
				"read_friendlists,friends_photos, friends_about_me, friends_activities, friends_birthday, " +
				"friends_education_history, friends_hometown, " +
				"friends_interests, friends_likes, friends_location, friends_relationships, " +
				"friends_religion_politics, friends_website, friends_work_history, " +
				"manage_pages";
	}

	@Override
	public String postAuthorize(Properties inputProperties) {
		String callback = inputProperties.getProperty("callback");
		String code = inputProperties.getProperty("code");
		String accessToken = "";
		
		HttpURLConnection c = null;
		URL url;
		try {
			url = new URL("https://graph.facebook.com/oauth/access_token?" +
					"client_id=" + FacebookHelper.getInstance().getAppKey() +
					"&redirect_uri=" + callback +
					"&client_secret=" + FacebookHelper.getInstance().getAppSecret() + 
					"&code=" + code);
		
			c = (HttpURLConnection) url.openConnection();
			c.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					c.getInputStream(), Charset.forName("UTF-8")));
			StringBuilder content = new StringBuilder();

			int temp;

			while ((temp = reader.read()) != -1) {
				content.append((char) temp);
			}
			
			String body = content.toString();
			if (body.indexOf("access_token") < 0)
				throw new PluginException(FacebookDescriptor.FACEBOOK_ID, "Access Token could not be retrieved: " + body);
			
			int accessTokenStart = body.indexOf("access_token") + 13;
			int accessTokenEnd = body.indexOf("&", accessTokenStart);
			accessToken = body.substring(accessTokenStart, accessTokenEnd);
			
			/*
			if(content.toString().startsWith("access_token")){
				String[] params = content.toString().split("&");
				for (String param : params){
					String name = param.split("=")[0];
					accessToken = param.split("=")[1];
					if(name.equals("access_token")){
						inputProperties.setProperty(FacebookHelper.PROPERTY_TOKEN, accessToken);
					}
				}
			} else throw new P
			*/
			
			inputProperties.put(FacebookHelper.PROPERTY_APP_KEY, accessToken);
			
			FacebookClient client = new DefaultFacebookClient(accessToken);
			User user = client.fetchObject("me", User.class);
			return user.getName();
		} catch (Exception e) {
			throw new PluginException(FacebookDescriptor.FACEBOOK_ID, "An error occurred while retrieving authentication information", e);
		}
	}
}