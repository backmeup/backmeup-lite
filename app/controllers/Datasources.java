package controllers;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import models.DatastoreProfile;
import models.DatastoreProfile.Type;
import models.User;

import org.backmeup.api.auth.InputBased;
import org.backmeup.api.auth.OAuthBased;
import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;
import org.backmeup.plugins.PluginRegistry;

import controllers.filter.DatasourceListFilter;
import controllers.filter.DatasourceProfilesListFilter;
import controllers.filter.ErrorMessage;
import controllers.filter.InputBasedAuthFilter;
import controllers.filter.OAuthInfoFilter;
import controllers.filter.SuccessMessage;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import views.html.datasources.index;
import views.html.datasources.success;

public class Datasources extends AbstractController {
		
	// TODO make configurable
	protected static String AUTH_CALLBACK_URL = "http://localhost:9000/datasources/";
	
	/** API routes (JSON) **/

	@Security.Authenticated(Secured.class)
	public static Result listAvailable_JSON() {
		return ok(Json.toJson(new DatasourceListFilter(listAvailable())));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getProfilesFor_JSON(String username) {
		Logger.info("Listing datasource profiles");
		return ok(Json.toJson(new DatasourceProfilesListFilter(getProfilesFor(username))));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result authorizeDatasource_JSON(String username, String sourceId) {
		Logger.info("Authorizing source: " + sourceId);
		
		// TODO needs to come from the session
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		PluginRegistry plugins = PluginRegistry.getInstance();
		DatastoreDescription sourceDescription = plugins.getSourceDescription(sourceId);
		if (sourceDescription == null)
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
				sourceDescription.datastoreClass,
				
				// Profile type
				Type.DATASOURCE);
		profile.save();
		
		// Prepare authorization procedure
		if (sourceDescription.authType == AuthorizationType.OAUTH) {
			OAuthBased authorizable = plugins.getAuthorizable(sourceId, OAuthBased.class);
			String callbackUrl = AUTH_CALLBACK_URL + user.username + "/" + profile.id + "/post";
			
			Properties profileProps = profile.getProperties();
			String redirectUrl = authorizable.createRedirectURL(profileProps, callbackUrl);
			profile.setProperties(profileProps);
			profile.update();
			
			OAuthInfoFilter preAuthInfo = new OAuthInfoFilter(
					// Profile ID
					profile.id,
					
					// Profile type
					Type.DATASOURCE, 
					
					// Redirect URL
					redirectUrl); 
			return ok(Json.toJson(preAuthInfo));
		} else if (sourceDescription.authType == AuthorizationType.INPUT_BASED) {
			InputBased authorizable = plugins.getAuthorizable(sourceId, InputBased.class);
			InputBasedAuthFilter preAuthInfo = new InputBasedAuthFilter(
					// Profile ID
					profile.id, 
					
					// Profile type
					Type.DATASOURCE, 
					
					// Required input fields
					authorizable.getRequiredInputFields());
			return ok(Json.toJson(preAuthInfo));
		}
		
		return ok();
	}
	
	public static Result postAuthorize(String username, Long profileId) {
		Logger.info("Datasource Postauthorization: " + profileId);
		
		Properties queryProps = new Properties();
		Map<String, String[]> queryString = request().queryString();
		for (String key : queryString.keySet()) {
			String[] values = queryString.get(key);
			if (values.length > 0) {
				queryProps.put(key, values[0]);
			}
		}
		
		Map<String, String[]> formParams = request().body().asFormUrlEncoded();
		if (formParams != null) {
			for (String key : formParams.keySet()) {
				String[] values = formParams.get(key);
				if (values.length > 0) {
					queryProps.put(key, values[0]);
				}
			}			
		}
		
		// TODO needs to come from the session
		User user = getUser(username);
		if (user == null) // Shouldn't really happen but you never know
			return notFound();

		DatastoreProfile profile = DatastoreProfile.find.byId(profileId);
		queryProps.putAll(profile.getProperties());
		
		PluginRegistry plugins = PluginRegistry.getInstance();	
		DatastoreDescription sourceDescription = plugins.getSourceDescription(profile.pluginClass);
		if (sourceDescription == null)
			return notFound(); // Shouldn't really happen
		
		if (sourceDescription.authType == AuthorizationType.OAUTH) {
			OAuthBased authorizable = 
					plugins.getAuthorizable(sourceDescription.datastoreClass, OAuthBased.class);
			authorizable.postAuthorize(queryProps);
			
			profile.setProperties(queryProps);
			profile.update();
			return ok(success.render());
		} else if (sourceDescription.authType == AuthorizationType.INPUT_BASED) {
			InputBased authorizable = 
					plugins.getAuthorizable(sourceDescription.datastoreClass, InputBased.class);
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
		Logger.info("Deleting datasource profile " + profileId);
		
		User user = getUser(username);
		if (user == null)
			return notFound();
		
		DatastoreProfile profile = DatastoreProfile.find.byId(profileId);
		if (profile == null)
			return ok(Json.toJson(new ErrorMessage("Profile not found")));
		
		DatastoreProfile.deleteCascaded(profile);
		
		return ok(Json.toJson(new SuccessMessage("Source profile has been deleted")));
	}

	/** HTML routes **/
	
	@Security.Authenticated(Secured.class)
	public static Result listAvailable_HTML() {
		return ok(index.render(listAvailable()));
	}	
	
	/** Common implementations **/
	
	private static List<DatastoreDescription> listAvailable() {
		PluginRegistry plugins = PluginRegistry.getInstance();
		return plugins.listAvailableDatasources();
	}
	
	private static List<DatastoreProfile> getProfilesFor(String username) {
		// TODO needs to come from the session
		User user = getUser(username);	
		return DatastoreProfile.findByUser(user, Type.DATASOURCE);
	}

}
