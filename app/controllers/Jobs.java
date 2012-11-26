package controllers;

import java.util.Date;
import java.util.List;

import org.backmeup.job.impl.SimpleJobManager;

import controllers.filter.BackupFilter;
import controllers.filter.JobListFilter;

import models.Backup;
import models.DatastoreProfile;
import models.DatastoreProfile.Type;
import models.Job;
import models.User;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Jobs extends AbstractController {
	
	private static String PROFILE_NOT_FOUND = "Profile not found: ";
	private static String NOT_A_SOURCE = "Datastore @@id@@ is a sink, not a source";
	private static String NOT_A_SINK = "Datastore @@id@@ is a source, not a sink";
	
	private static final long DELAY_DAILY = 24 * 60 * 60 * 1000;
	private static final long DELAY_WEEKLY = 24 * 60 * 60 * 1000 * 7;
	private static final long DELAY_MONTHLY = (long) (24 * 60 * 60 * 1000 * 365.242199 / 12.0);
	private static final long DELAY_YEARLY = (long) (24 * 60 * 60 * 1000 * 365.242199);
	
	/** API routes (JSON) **/
	
	public static Result getJobsFor_JSON(String username) {
		return ok(Json.toJson(new JobListFilter(getJobsFor(username))));
	}
	
	public static Result createJob(String username) {
		// TODO needs to come from the session
		User user = getUser(username);
		
		// Get source profile
		String sourceProfileId = getFormParam("sourceProfileId");
		if (sourceProfileId == null) {
			Logger.warn(MISSING_PARAM + "sourceProfileId");
			return badRequest(MISSING_PARAM + "sourceProfileId");
		}
		
		DatastoreProfile sourceProfile = null;
		try {
			sourceProfile = DatastoreProfile.find.byId(Long.parseLong(sourceProfileId));
		} catch (Throwable t) {
			Logger.error(t.getMessage());
		}
		
		if (sourceProfile == null) {
			Logger.warn(PROFILE_NOT_FOUND + sourceProfileId);
			return notFound(PROFILE_NOT_FOUND + sourceProfileId);
		}
		
		if (sourceProfile.type != Type.DATASOURCE) {
			Logger.warn(NOT_A_SOURCE.replace("@@id@@", sourceProfileId));
			return badRequest(NOT_A_SOURCE.replace("@@id@@", sourceProfileId));
		}
		
		Logger.info("New job - source " + sourceProfile.pluginClass);

		String listOfActions = getFormParam("requiredActions");
		if (listOfActions == null)
			listOfActions = "";
		Logger.info("New job - actions " + listOfActions);
		
		// Get sink profile
		String sinkProfileId = getFormParam("sinkProfileId");
		if (sinkProfileId == null) {
			Logger.warn(MISSING_PARAM + "sinkProfileId");
			return badRequest(MISSING_PARAM + "sinkProfileId");
		}
		
		DatastoreProfile sinkProfile = null;
		try {
			sinkProfile = DatastoreProfile.find.byId(Long.parseLong(sinkProfileId));
		} catch (Throwable t) {
			Logger.error(t.getMessage());
		}
		
		if (sinkProfile == null) {
			Logger.warn(PROFILE_NOT_FOUND + sinkProfileId);
			return notFound(PROFILE_NOT_FOUND + sinkProfileId);
		}
		
		if (sinkProfile.type != Type.DATASINK) {
			Logger.warn(NOT_A_SINK.replace("@@id@@", sinkProfileId));
			return badRequest(NOT_A_SINK.replace("@@id@@", sinkProfileId));
		}
		
		Logger.info("New job - sink " + sinkProfile.pluginClass);
		
		// Time expression 
		String timeExpression = getFormParam("timeExpression");
		Logger.info("New job - time expression: " + timeExpression);
		if (timeExpression == null)
			return badRequest(MISSING_PARAM + "timeExpression");
		
		// TODO - we start immediately for testing purposes ONLY
		Date start = new Date();
		
		// Job Title
		String jobTitle = getFormParam("jobTitle");
		if (jobTitle == null)
			return badRequest(MISSING_PARAM + "jobTitle");
		
		Job job = new Job(user, jobTitle, sourceProfile, listOfActions.trim(), sinkProfile, start, getDelay(timeExpression));
		job.save();
		
		SimpleJobManager.getInstance().queueJob(job);
		
		return ok();
	}
	
	public static Result deleteJob(String username, Long jobId) {
		Job job = Job.findById(jobId);
		if (job != null)
			Job.deleteCascaded(job);
		
		return noContent();
	}
	
	public static Result listBackupsFor_JSON(String username, Long jobId) {
		Logger.info("Listing backups for job " +  jobId);
		Job job = Job.findById(jobId);
		if (job == null)
			return notFound();
		
		List<Backup> backups = Backup.findByJob(job);
		return ok(Json.toJson(BackupFilter.map(backups)));
	}
	
	/** Common implementations **/
	
	private static List<Job> getJobsFor(String username) {
		// TODO needs to come from the session
		User user = getUser(username);	
		return Job.findByUser(user);
	}
	
	private static Long getDelay(String timeExpression) {
		// TODO there's got to be nicer ways to handle this!
	    if (timeExpression.equalsIgnoreCase("daily"))
	    	return DELAY_DAILY;      
	    
	    if (timeExpression.equalsIgnoreCase("weekly"))
	        return DELAY_WEEKLY;

	    if (timeExpression.equalsIgnoreCase("monthly"))
	    	return DELAY_MONTHLY;
	    
	    if (timeExpression.equalsIgnoreCase("yearly"))
	    	return DELAY_YEARLY;
	    	
	    // Monthly as default
	    return DELAY_MONTHLY;
	}

}

