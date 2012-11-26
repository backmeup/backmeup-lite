package org.backmeup.api.connectors;

import java.io.File;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.Progressable;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;

public abstract class Datasink {
	
	protected DatastoreProfile profile;
	
	public Datasink(DatastoreProfile profile, Backup backup) {
		this.profile = profile;
	}

	public abstract File getStorageBasePath(DatastoreProfile profile, Backup backup);
	
	public abstract String upload(Properties accessData, Storage storage, Progressable progressor)
			throws DatasinkException, StorageException;
	
}
