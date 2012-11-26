package org.backmeup.job;

import org.backmeup.api.Progressable;

import play.Logger;

import models.Backup;
import models.BackupLogMessage;

public class BackupLogMessageProgressor implements Progressable {

	private Backup backup;
	
	public BackupLogMessageProgressor(Backup backup) {
		this.backup = backup;
	}

	@Override
	public void progress(String message) {
		Logger.info("INFO - " + message);
		BackupLogMessage msg = new BackupLogMessage(backup, message, BackupLogMessage.Level.INFO);
		msg.save();
	}
	
	public void progress(String message, BackupLogMessage.Level level) {
		Logger.info(level.name().toUpperCase() + " - " + message);
		BackupLogMessage msg = new BackupLogMessage(backup, message, level);
		msg.save();		
	}

}
