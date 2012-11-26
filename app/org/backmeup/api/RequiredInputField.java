package org.backmeup.api;

public class RequiredInputField {
	
	public String name;
	
	public String label;
	
	public String description;
	
	public boolean required;
	
	public InputFieldType type;
	
	public enum InputFieldType { STRING, NUMBER, PASSWORD, BOOLEAN }
	
	public RequiredInputField(String name, String label, String description, boolean required, InputFieldType type) {
		this.name = name;
		this.label = label;
		this.description = description;
		this.required = required;
		this.type = type;
	}

}
