package org.backmeup.api.auth;

import java.util.Properties;

public interface Authorizable {

	public static final String PROP_REDIRECT_URL = "PROP_REDIRECT_URL";
	
	public enum AuthorizationType { OAUTH, INPUT_BASED, NONE }
	
	public AuthorizationType getAuthType(); 
	
	public String postAuthorize(Properties inputProperties);
		
}
