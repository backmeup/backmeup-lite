package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import models.UserProperty;

public class UserPropertyFilter {
	
	public String key;
	
	public String value;
	
	public String type;
		
	public UserPropertyFilter(UserProperty property) {
		this.key = property.key;
		this.value = property.value;
		this.type = property.type.name();
	}
	
	public static List<UserPropertyFilter> map(List<UserProperty> properties) {
		List<UserPropertyFilter> mapped = new ArrayList<UserPropertyFilter>();
		for (UserProperty p : properties) {
			mapped.add(new UserPropertyFilter(p));
		}
		return mapped;
	}

}
