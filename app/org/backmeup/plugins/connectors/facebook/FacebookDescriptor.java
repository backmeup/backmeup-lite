package org.backmeup.plugins.connectors.facebook;

import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;

public class FacebookDescriptor extends DatastoreDescription {

	public static final String FACEBOOK_ID = "org.backmeup.facebook";
	
	public FacebookDescriptor() {
		
		super(FACEBOOK_ID,
			   "BackMeUp Facebook Plug-In",
			   "A plug-in that is capable of downloading from facebook",
			   DatastoreType.SOURCE,
			   AuthorizationType.OAUTH,
			   "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc4/174597_20531316728_2866555_s.jpg");
	}

}
