package org.backmeup.plugins.connectors.dropbox;

import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;

public class DropboxDescriptor extends DatastoreDescription {
	
	public static final String DROPBOX_ID = "org.backmeup.dropbox";

	public DropboxDescriptor(String datasourceId, String title,
			String description, AuthorizationType authType, String imageURL) {
		
		super(DROPBOX_ID,
			   "BackMeUp Dropbox Plug-In",
			   "A plug-in that is capable of downloading and uploading from dropbox",
			   DatastoreType.BOTH,
			   AuthorizationType.OAUTH,
			   "http://about:blank");
	}

}
