package org.backmeup.plugins.connectors.moodle;

import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;

public class MoodleDescriptor extends DatastoreDescription {
	
	public static final String MOODLE_ID = "org.backmeup.moodle";
	
	public MoodleDescriptor() {
		super(
			MOODLE_ID,
			"BackMeUp Moodle Plug-In",
			"Moodle Plugin for Backmeup",
			DatastoreType.SOURCE,
			AuthorizationType.INPUT_BASED,
			"http://moodle.org/logo/logo-4045x1000.jpg");
	}

}
