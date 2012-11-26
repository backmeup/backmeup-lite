package org.backmeup.api.auth;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.backmeup.api.RequiredInputField;
import org.backmeup.api.RequiredInputField.InputFieldType;

public interface InputBased extends Authorizable {
	
	public List<RequiredInputField> getRequiredInputFields();
	
	public Map<String, InputFieldType> getTypeMapping();
	
	public boolean isValid(Properties inputs);
	
}
