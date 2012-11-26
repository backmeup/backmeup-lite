package models;

import javax.persistence.Id;
import javax.persistence.Entity;

import play.db.ebean.Model;

/**
 * User model class.
 */
@Entity
public class User extends Model {

	private static final long serialVersionUID = 2980648807266593017L;

	@Id
	public Long id;
	
	public String username;
	
	public String email;
	
	public String password;
	
	public static Finder<Long, User> find = 
			new Finder<Long, User>(Long.class, User.class);
	
	public User(String username, String email, String password) {
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	public static void deleteCascaded(User user) {
		// Delete User properties
		for (UserProperty property : UserProperty.findByUser(user)) {
			property.delete();
		}
		
		// Delete Jobs (including Backups & BackupLogMessages)
		for (Job job : Job.findByUser(user)) {
			for (Backup backup : Backup.findByJob(job)) {
				Backup.deleteCascaded(backup);
			}
			job.delete();
		}
		
		// Profiles
		for (DatastoreProfile profile : DatastoreProfile.findByUser(user)) {
			for (DatastoreProfileProperty property : DatastoreProfileProperty.findByProfile(profile)) {
				property.delete();
			}
			profile.delete();
		}
		
		// Delete User
		user.delete();	
	}

}
