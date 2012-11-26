package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import models.Backup;

public class BackupFilter {
	
	public String backupId;
	
	public String jobId;

	public String status;
	
	public long startTime;
	
	public long endTime;
	
	public BackupFilter(Backup backup) {
		this.backupId = Long.toString(backup.id);
		this.jobId = Long.toString(backup.job.id.longValue());
		this.status = backup.status.name();
		this.startTime = backup.startTime.getTime();
		if (backup.endTime != null)
			this.endTime = backup.endTime.getTime();
	}
	
	public static List<BackupFilter> map(List<Backup> backups) {
		List<BackupFilter> mapped = new ArrayList<BackupFilter>();
		for (Backup b : backups) {
			mapped.add(new BackupFilter(b));
		}
		return mapped;
	}

}
