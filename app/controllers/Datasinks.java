package controllers;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import models.DatastoreProfile;
import models.User;
import models.DatastoreProfile.Type;

import org.backmeup.api.auth.InputBased;
import org.backmeup.api.auth.OAuthBased;
import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;
import org.backmeup.plugins.PluginRegistry;

import controllers.filter.DatasinkListFilter;
import controllers.filter.DatasinkProfilesListFilter;
import controllers.filter.ErrorMessage;
import controllers.filter.InputBasedAuthFilter;
import controllers.filter.OAuthInfoFilter;
import controllers.filter.SuccessMessage;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import views.html.datasinks.index;
import views.html.datasinks.success;

public class Datasinks extends AbstractController {

	// TODO make configurable
	protected static String AUTH_CALLBACK_URL = "http://localhost:9000/datasinks/";

	/** API routes (JSON) **/
	
	@Security.Authenticated(Secured.class)	
	public static Result listAvailable_JSON() {		
		return ok(Json.toJson(new DatasinkListFilter(listAvailable())));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getProfilesFor_JSON(String username) {
		Logger.info("Listing datasink profiles");
		return ok(Json.toJson(new DatasinkProfilesListFilter(getProfilesFor(username))));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result authorizeDatasink_JSON(String username, String sinkId) {
		Logger.info("Authorizing sink: " + sinkId);
		
		// TODO needs to come from the session
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		PluginRegistry plugins = PluginRegistry.getInstance();
		DatastoreDescription sinkDescription = plugins.getSinkDescription(sinkId);
		if (sinkDescription == null)
			return notFound();
		
		// Initialize a new profile
		DatastoreProfile profile = new DatastoreProfile(
				// Profile owner
				user, 
				
				// Profile name
				getFormParam("profileName"), 
				
				// Profile description
				getFormParam("profileDescription"),
				
				// Plugin class
				sinkDescription.datastoreClass,
				
				// Profile type
				Type.DATASINK);
		profile.save();
		
		// Prepare authorization procedure
		if (sinkDescription.authType == AuthorizationType.OAUTH) {
			OAuthBased authorizable = plugins.getAuthorizable(sinkId, OAuthBased.class);
			String callbackUrl = AUTH_CALLBACK_URL + user.username + "/" + profile.id + "/post";
			
			Properties profileProps = profile.getProperties();
			String redirectUrl = authorizable.createRedirectURL(profileProps, callbackUrl);
			profile.setProperties(profileProps);
			profile.update();
			
			OAuthInfoFilter preAuthInfo = new OAuthInfoFilter(
					// Profile ID
					profile.id,
					
					// Profile type
					Type.DATASINK, 
					
					redirectUrl); 
			return ok(Json.toJson(preAuthInfo));
		} else if (sinkDescription.authType ==  AuthorizationType.INPUT_BASED) {
			InputBased authorizable = plugins.getAuthorizable(sinkId, InputBased.class);
			InputBasedAuthFilter preAuthInfo = new InputBasedAuthFilter(
					// Profile ID
					profile.id, 
					
					// Profile type
					Type.DATASINK, 
					
					// Required input fields
					authorizable.getRequiredInputFields());
			return ok(Json.toJson(preAuthInfo));			
		}
		
		return ok();
	}
	
	public static Result postAuthorize(String username, Long profileId) {
		Logger.info("Datasink Postauthorization: " + profileId);
		
		Properties queryProps = new Properties();
		Map<String, String[]> queryString = request().queryString();
		for (String key : queryString.keySet()) {
			String[] values = queryString.get(key);
			if (values.length > 0) {
				Logger.info("Got param: " + key + "=" + values[0]);
				queryProps.put(key, values[0]);
			}
		}
		
		// TODO needs to come from the session
		User user = getUser(username);
		if (user == null) // Shouldn't really happen but you never know
			return notFound();

		DatastoreProfile profile = DatastoreProfile.find.byId(profileId);
		queryProps.putAll(profile.getProperties());
		
		PluginRegistry plugins = PluginRegistry.getInstance();	
		DatastoreDescription sinkDescription = plugins.getSinkDescription(profile.pluginClass);
		if (sinkDescription == null)
			return notFound(); // Shouldn't really happen
		
		if (sinkDescription.authType == AuthorizationType.OAUTH) {
			OAuthBased authorizable = 
					plugins.getAuthorizable(sinkDescription.datastoreClass, OAuthBased.class);
			authorizable.postAuthorize(queryProps);
			
			profile.setProperties(queryProps);
			profile.update();
			return ok(success.render());
		} else if (sinkDescription.authType == AuthorizationType.INPUT_BASED) {
			InputBased authorizable = 
					plugins.getAuthorizable(sinkDescription.datastoreClass, InputBased.class);
			authorizable.postAuthorize(queryProps);
			
			profile.setProperties(queryProps);
			profile.update();
			
			Logger.info("Input-based authorization successful.");
			return ok(Json.toJson(new SuccessMessage("Authorization Successful")));		
		}
		
		return ok();
	}
	
	@Security.Authenticated(Secured.class)
	public static Result deleteProfile(String username, Long profileId) {
		Logger.info("Deleting datasink profile " + profileId);
		
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		DatastoreProfile profile = DatastoreProfile.find.byId(profileId);
		if (profile == null)
			return ok(Json.toJson(new ErrorMessage("Profile not found")));
		
		DatastoreProfile.deleteCascaded(profile);
		return ok(Json.toJson(new SuccessMessage("Sink profile has been deleted")));		
	}

	/** HTML routes **/
	
	@Security.Authenticated(Secured.class)
	public static Result listAvailable_HTML() {
		return ok(index.render(listAvailable()));
	}	
	
	/** Common implementations **/

	private static List<DatastoreDescription> listAvailable() {
		PluginRegistry plugins = PluginRegistry.getInstance();
		return plugins.listAvailableDatasinks();
	}
	
	private static List<DatastoreProfile> getProfilesFor(String username) {
		// TODO needs to come from the session
		User user = getUser(username);	
		return DatastoreProfile.findByUser(user, Type.DATASINK);
	}
	
}
