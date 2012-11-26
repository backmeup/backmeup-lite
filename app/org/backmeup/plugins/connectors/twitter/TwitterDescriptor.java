package org.backmeup.plugins.connectors.twitter;

import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;

/**
 * The TwitterDescriptor provides all necessary information about this plugin.
 * Note: TWITTER_ID matches the filters stated in the configuration files:
 * META-INF/spring/org.backmeup.twitter-context.xml
 * META-INF/spring/org.backmeup.twitter-osgi-context.xml
 * 
 * @author 
 */
public class TwitterDescriptor extends DatastoreDescription {
	
	public static final String TWITTER_ID = "org.backmeup.twitter";
	
	public TwitterDescriptor() {
		super(TWITTER_ID, 
				"BackMeUp Twitter Plug-In", 
				"A plug-in that is capable of downloading from twitter",
				DatastoreType.SOURCE,
				AuthorizationType.OAUTH,
				"https://twitter.com/images/resources/twitter-bird-light-bgs.png");
	}

}