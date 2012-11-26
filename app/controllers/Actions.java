package controllers;

import java.util.List;

import org.backmeup.api.actions.ActionDescription;
import org.backmeup.plugins.PluginRegistry;

import controllers.filter.ActionListFilter;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Actions extends AbstractController {
	
	/** API routes (JSON) **/
	
	public static Result listAvailable_JSON() {
		return ok(Json.toJson(new ActionListFilter(listAvailable())));
	}
	
	/** Common implementations **/
	
	private static List<ActionDescription> listAvailable() {
		PluginRegistry plugins = PluginRegistry.getInstance();
		return plugins.listAvailableActions();
	}

}
