package controllers.filter;

import org.backmeup.api.auth.Authorizable.AuthorizationType;

import models.DatastoreProfile;

public class OAuthInfoFilter {

	public String profileId;

	public String profileType;
	
	public String authType;

	public String redirectURL;
	
	public OAuthInfoFilter(Long profileId, DatastoreProfile.Type profileType, String redirectURL) {
		this.profileId = Long.toString(profileId);
		this.profileType = profileType.name();
		this.authType = AuthorizationType.OAUTH.name();
		this.redirectURL = redirectURL;
	}
	
}
