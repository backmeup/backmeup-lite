package controllers;

import java.util.List;

import controllers.filter.ErrorMessage;
import controllers.filter.SuccessMessage;
import controllers.filter.UserFilter;
import controllers.filter.UserPropertyFilter;
import models.User;
import models.UserProperty;
import models.UserProperty.Type;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.mvc.Security;

import views.html.users.index;
import views.html.users.get_user;

public class Users extends AbstractController {

	private static String MISSING_PARAM = "Missing parameter: ";
	
	/** API routes (JSON) **/
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result getUser_JSON(String username) {
		User user = getUser(username);
		if (user == null) {
			return notFound(); 
		} else {
			// TODO implement custom serializer classes
			return ok(Json.toJson(new UserFilter(user)));
		}
	}
	
	public static Result register_JSON(String username) {
		User user = getUser(username);
		if (user != null)
			return ok(Json.toJson(new ErrorMessage("User exists already")));
		
		String email = getFormParam("email");
		if (email == null)
			return ok(Json.toJson(new ErrorMessage("Missing parameter: email")));
			
		String password = getFormParam("password");
		if (password == null)
			return ok(Json.toJson(new ErrorMessage("Missing paramter: password")));
		
		User newUser = new User(username, email, password);
		newUser.save();
		
		return ok(Json.toJson(new UserFilter(newUser)));
	}
	
	public static Result deleteUser_JSON(String username) {
		User user = getUser(username);
		if (user == null) 
			return notFound(); 
			
		User.deleteCascaded(user);
		
		return ok(Json.toJson(new SuccessMessage("User has been deleted")));
	}
	
	public static Result login_JSON(String username) {
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		String password = getFormParam("password");
		if (!user.password.equals(password))
			return ok(Json.toJson(new ErrorMessage("Invalid username or password")));
		
		session("username", username);
		return ok(Json.toJson(new SuccessMessage("User logged in")));
	}

	public static Result logout_JSON(String username) {
		session().clear();
		return ok(Json.toJson(new SuccessMessage("User logged out")));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result listProperties_JSON(String username) {
		User user = getUser(username);
		if (user == null) {
			return notFound();
		} else {
			List<UserProperty> properties = UserProperty.findByUser(user);
			return ok(Json.toJson(UserPropertyFilter.map(properties)));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getProperty_JSON(String username, String key) {
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		return ok(Json.toJson(new UserPropertyFilter(UserProperty.getProperty(user, key))));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result createProperty_JSON(String username, String key) {
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		String value = getFormParam("value");
		if (value == null)
			return badRequest(Json.toJson(new ErrorMessage(MISSING_PARAM + "value")));
		
		String sType = getFormParam("type");
		if (sType == null)
			return badRequest(Json.toJson(new ErrorMessage(MISSING_PARAM + "type")));
		
		Type type = Type.valueOf(sType.toUpperCase());
		UserProperty property = new UserProperty(user, key, value, type);
		property.save();
		
		return ok(Json.toJson(new SuccessMessage("User property set")));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result deleteProperty_JSON(String username, String key) {
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		UserProperty property = UserProperty.getProperty(user, key);
		if (property == null)
			return ok(Json.toJson(new ErrorMessage("Property not found")));
		
		property.delete();
		return ok(Json.toJson(new SuccessMessage("User property deleted")));
	}
	
	/** HTML routes **/
	
	public static Result index() {
		return ok(index.render());
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getUser_HTML(String username) {
		User user = getUser(username);
		if (user == null) {
			return notFound();
		} else {
			return ok(get_user.render(user));
		}
	}
	
	public static Result register_HTML(String username) {
		User exists = getUser(username);
		if (exists != null)
			return ok("Error: username no longer available");
		
		// TODO needs validation
		String password = request().body().asFormUrlEncoded().get("password")[0];
		String email = request().body().asFormUrlEncoded().get("email")[0];
		User user = new User(username, email, password);
		user.save();
		
		return ok("Registered user '" + username + "'");
	}
	
	@Security.Authenticated(Secured.class)
	public static Result deleteUser_HTML(String username) {
		return noContent();
	}
	
	@Security.Authenticated(Secured.class)
	public static Result updateUser_HTML(String username) {
		return noContent();
	}
	
	public static Result login_HTML(String username) {
		return noContent();
	}
	
	public static Result logout_HTML(String username) {
		return noContent();
	}

}
