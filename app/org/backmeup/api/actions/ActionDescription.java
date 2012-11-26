package org.backmeup.api.actions;

public class ActionDescription {
	
	public String actionId;
	
	public String title;
	
	public String description;
	
	public ActionDescription(String actionId, String title, String description) {
		this.title = title;
		this.actionId = actionId;
		this.description = description;
	}
	
}
