package org.backmeup.job.impl;

import models.Backup;

import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.job.JobException;
import org.backmeup.job.JobManager;
import org.backmeup.job.JobRunner;
import org.backmeup.plugins.storage.filesystem.FilesystemStorage;

public class SimpleJobManager extends JobManager {
	
	private static JobManager instance = null;
	
	private SimpleJobManager() { }
	
	public static JobManager getInstance() {
		if (instance == null)
			instance = new SimpleJobManager();
		
		return instance;
	}

	@Override
	protected void executeBackup(Backup backup) throws JobException {		
		try {
			Storage storage = new FilesystemStorage();
			storage.open("tmp/" + System.currentTimeMillis());
			
			JobRunner runner = new JobRunner();
			runner.executeBackup(backup, storage);
		} catch (StorageException e) {
			throw new JobException(e);
		}
	}

}
