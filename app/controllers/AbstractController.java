package controllers;

import java.util.List;
import java.util.Map;

import models.User;
import play.mvc.Controller;

public class AbstractController extends Controller {
	
	protected static String MISSING_PARAM = "Missing required query param: ";

	/**
	 * Returns the query string parameter with the specified name, or 
	 * <code>null</code> if not available.
	 * @param name the name of the query parameter
	 * @return the value of the query parameter or <code>null</code>
	 */
	protected static String getQueryParam(String name) {
		Map<String, String[]> queryString = request().queryString();
		return getParam(name, queryString);
	}
	
	public static String getFormParam(String name) {
		Map<String, String[]> formBody = request().body().asFormUrlEncoded();
		return getParam(name, formBody);
	}
	
	private static String getParam(String name, Map<String, String[]> params) {
		if (params == null)
			return null;
		
		String[] paramList = params.get(name);
		if (paramList != null)
			if (paramList.length > 0)
				return paramList[0];
		
		return null;		
	}
	
	/**
	 * TODO user needs to come from the session!
	 */
	protected static User getUser(String username) {
		List<User> users = User.find.where().eq("username", username).findList();
		if (users.size() == 0) {
			return null;
		} else if (users.size() > 1) {
			throw new RuntimeException("User not unique!");
		} else {
			return users.get(0);
		}		
	}
	
}
