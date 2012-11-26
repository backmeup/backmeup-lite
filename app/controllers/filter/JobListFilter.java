package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import models.Backup;
import models.Job;

public class JobListFilter {
	
	public List<JobFilter> backupJobs;
	
	public JobListFilter(List<Job> jobs) {
		backupJobs = new ArrayList<JobListFilter.JobFilter>();
		for (Job j : jobs) {
			backupJobs.add(new JobFilter(j));
		}
	}
	
	public class JobFilter {
	
		public String backupJobId;
		
		public String datasourceId;
		
		public String datasourceProfileId;
		
		public String datasinkId;
		
		public String datasinkProfileId;
		
		public String jobTitle;
		
		public long startDate;
		
		public long createDate;
		
		public long modifyDate;
		
		public String currentStatus;
	
		public JobFilter(Job job) {
			this.backupJobId = Long.toString(job.id);
			this.datasourceId = job.sourceProfile.pluginClass;
			this.datasourceProfileId = Long.toString(job.sourceProfile.id);
			this.datasinkId = job.sinkProfile.pluginClass;
			this.datasinkProfileId = Long.toString(job.sinkProfile.id);
			this.jobTitle = job.jobTitle;
			this.startDate = job.start.getTime();
		    this.createDate = job.created.getTime();
		    this.modifyDate = job.modified.getTime();
		    
		    List<Backup> backups = Backup.findByJob(job);
		    if (backups.size() > 0) {
		    	currentStatus = backups.get(0).status.name();
		    } else{
		    	currentStatus = "PENDING";
		    }
		}
		
	}
	
}
