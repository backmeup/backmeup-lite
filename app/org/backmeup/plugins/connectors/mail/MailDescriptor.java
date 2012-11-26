package org.backmeup.plugins.connectors.mail;

import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;

/** 
 * @author fschoeppl
 */
public class MailDescriptor extends DatastoreDescription {
	
	public static final String MAIL_ID = "org.backmeup.mail";
	
	public MailDescriptor() {
		super(MAIL_ID,
				"BackMeUp Facebook Plug-In",
				"A plug-in that is capable of downloading from facebook",
				DatastoreType.SOURCE,
				AuthorizationType.INPUT_BASED,
				"http://about:blank");
	}
	
}
