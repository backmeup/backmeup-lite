package controllers.filter;

import java.util.List;

import org.backmeup.api.RequiredInputField;
import org.backmeup.api.auth.Authorizable.AuthorizationType;

import models.DatastoreProfile;

public class InputBasedAuthFilter {
	
	public String profileId;

	public String profileType;
	
	public String authType;
	
	public List<RequiredInputField> fields;
	
	public InputBasedAuthFilter(Long profileId, DatastoreProfile.Type profileType, 
			List<RequiredInputField> fields) {
		
		this.profileId = Long.toString(profileId);
		this.profileType = profileType.name();
		this.authType = AuthorizationType.INPUT_BASED.name();
		this.fields = fields; 
	}

}
