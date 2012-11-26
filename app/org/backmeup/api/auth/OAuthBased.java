package org.backmeup.api.auth;

import java.util.Properties;

public interface OAuthBased extends Authorizable {

	public String createRedirectURL(Properties inputProperties, String callbackUrl);
	
}
