package org.backmeup.plugins.connectors.localfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.backmeup.api.RequiredInputField;
import org.backmeup.api.RequiredInputField.InputFieldType;
import org.backmeup.api.auth.InputBased;

public class LocalFilesystemAuthorizable implements InputBased {

	@Override
	public AuthorizationType getAuthType() {
		return AuthorizationType.INPUT_BASED;
	}

	@Override
	public String postAuthorize(Properties inputProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RequiredInputField> getRequiredInputFields() {
		List<RequiredInputField> fields = new ArrayList<RequiredInputField>();
		fields.add(new RequiredInputField(
				// Name
				"storagePath",
				
				// Label
				"Storage Path",
				
				// Description
				"Path on your drive where backups should be stored",
				
				// Required
				true,
				
				// Type
				InputFieldType.STRING));
		return fields;
	}

	@Override
	public Map<String, InputFieldType> getTypeMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(Properties inputs) {
		// TODO Auto-generated method stub
		return false;
	}

}
