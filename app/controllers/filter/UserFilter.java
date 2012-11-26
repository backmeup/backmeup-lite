package controllers.filter;

import models.User;

public class UserFilter {
	
	public String username;
	
	public String email;
	
	public UserFilter(User user) {
		this.username = user.username;
		this.email = user.email;
	}

}
