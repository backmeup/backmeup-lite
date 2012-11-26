package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.annotation.EnumValue;

import play.db.ebean.Model;

@Entity
public class BackupLogMessage extends Model {

	private static final long serialVersionUID = -2248199381221014029L;

	@Id
	public Long id;
	
	@ManyToOne
	public Backup backup;
	
	public Date timestamp;
	
	public String message;
	
	public Level level;
	
	public static Finder<Long, BackupLogMessage> find = 
			new Finder<Long, BackupLogMessage>(Long.class, BackupLogMessage.class);
	
	public BackupLogMessage(Backup backup, String message, Level level) {
		this.backup = backup;
		this.timestamp = new Date();
		if (message.length() < 255)
			this.message = message;
		else
			this.message = message.substring(0,255);
		this.level = level;
	}
	
	public static List<BackupLogMessage> findByBackup(Backup backup) {
		return BackupLogMessage.find
				.where()
				.eq("backup.id", backup.id)
				.findList();
	}
	
	public static void deleteCascaded(Backup backup) {
		
	}
	
	public enum Level {
		// The job is currently running
		@EnumValue("I")
		INFO,
		
		// The job has completed successfully
		@EnumValue("W")
		WARNING,
		
		// The job has terminated with an error
		@EnumValue("E")
		ERROR
	}


}
