package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.annotation.EnumValue;

import play.db.ebean.Model;

@Entity
public class Backup extends Model {

	private static final long serialVersionUID = -2451833444776641020L;

	@Id 
	public Long id;

	@ManyToOne
	public Job job;

	public Status status;
	
	public Date startTime;
	
	public Date endTime;
	
	public static Finder<Long, Backup> find = 
			new Finder<Long, Backup>(Long.class, Backup.class);
	
	public Backup(Job job, Status status, Date startTime, Date endTime) {
		this.job = job;
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public static List<Backup> findByJob(Job job) {
		return Backup.find
				.fetch("job")
				.where()
				.eq("job.id", job.id)
				.order()
				.desc("startTime")
				.findList();
	}
	
	public static void deleteCascaded(Backup backup) {
		for (BackupLogMessage logMessage : BackupLogMessage.findByBackup(backup)) {
			logMessage.delete();
		}
		backup.delete();
	}
	
	public enum Status {
		// The job is currently running
		@EnumValue("R")
		RUNNING,
		
		// The job has completed successfully
		@EnumValue("S")
		SUCCESS,
		
		// The job has terminated with an error
		@EnumValue("E")
		ERROR
	}
	

}
