package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class Job extends Model {

	private static final long serialVersionUID = 4566426827442818445L;
	
	@Id
	public Long id;
	
	@ManyToOne
	public User user;
	
	public String jobTitle;

	@ManyToOne
	public DatastoreProfile sourceProfile;
	
	public String actions;
	
	@ManyToOne
	public DatastoreProfile sinkProfile;
	
	public Date start;
	
	public Long delay;
		  
	public Date created;
	
	public Date modified;
	
	public static Finder<Long, Job> find = 
			new Finder<Long, Job>(Long.class, Job.class);
	
	public Job(User user, String jobTitle, DatastoreProfile sourceProfile, String actions,
			DatastoreProfile sinkProfile, Date start, Long delay) {
		
	    this.user = user;
	    this.jobTitle = jobTitle;
	    this.sourceProfile = sourceProfile;
	    this.actions = actions;
	    this.sinkProfile = sinkProfile;
	    this.start = start;
	    this.delay = delay;
	    
	    this.created = new Date();
	    this.modified = this.created;
	}

	public static Job findById(Long id) {
		List<Job> jobs = Job.find
				.fetch("user")
				.fetch("sourceProfile")
				.fetch("sinkProfile")
				.where()
				.eq("id", id)
				.findList();
		
		if (jobs.size() > 0)
			return jobs.get(0);
		else
			return null;
	}
	
	public static List<Job> findByUser(User user) {
		return Job.find
				.fetch("user")
				.fetch("sourceProfile")
				.fetch("sinkProfile")
				.where()
				.eq("user.id", user.id)
				.findList();
	}

	public static List<Job> findBySourceProfile(DatastoreProfile profile) {
		return Job.find
				.where()
				.eq("sourceProfile.id", profile.id)
				.findList();
	}
	
	public static List<Job> findBySinkProfile(DatastoreProfile profile) {
		return Job.find
				.where()
				.eq("sinkProfile.id", profile.id)
				.findList();		
	}
	
	public static void deleteCascaded(Job job) {
		for (Backup backup : Backup.findByJob(job)) {
			Backup.deleteCascaded(backup);
		}
		job.delete();
	}

}
