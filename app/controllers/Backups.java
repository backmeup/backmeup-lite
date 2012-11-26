package controllers;

import java.util.ArrayList;
import java.util.List;

import org.backmeup.search.ESSearchClient;

import models.Backup;
import models.Job;
import models.User;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Backups extends AbstractController {
	
	/** API routes (JSON) **/
	
	public static Result getBackupsFor_JSON(String username) {
		return ok(Json.toJson(getBackupsFor(username)));
	}
	
	public static Result searchBackups_JSON(String username) {
		// TODO user must come from the session
		User user = getUser(username);
		String query = getQueryParam("query");
		if (query == null)
			query = getFormParam("query");
		
		if (user != null && query != null) {
			ESSearchClient searcher = new ESSearchClient();
			return ok(Json.toJson(searcher.queryIndex(user, query)));
		}
		
		// TODO return something useful here
		return noContent();
	}
	
	/** Common implementations **/
	
	private static List<Backup> getBackupsFor(String username) {
		// TODO needs to come from the session
		User user = getUser(username);	
		List<Backup> backups = new ArrayList<Backup>();
		for (Job job : Job.findByUser(user)) {
			backups.addAll(Backup.findByJob(job));
		}
		return backups;
	}

}
