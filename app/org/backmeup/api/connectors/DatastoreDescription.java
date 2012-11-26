package org.backmeup.api.connectors;

import org.backmeup.api.auth.Authorizable.AuthorizationType;

public class DatastoreDescription {

	public String datastoreClass;
	
	public String title;
	
	public String description;
	
	public DatastoreType datastoreType;
	
	public AuthorizationType authType;
	
	public String imageURL;
		
	public DatastoreDescription(String datasinkId, String title, String description, DatastoreType datastoreType, AuthorizationType authType, String imageURL) {
		this.datastoreClass = datasinkId;
		this.title = title;
		this.description = description;
		this.datastoreType = datastoreType;
		this.authType = authType;
		this.imageURL = imageURL;
	}
	
	public enum DatastoreType { SOURCE, SINK, BOTH }
	
}
