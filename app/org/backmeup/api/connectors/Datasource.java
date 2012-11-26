package org.backmeup.api.connectors;

import java.util.List;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.Progressable;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;

public abstract class Datasource {

	protected DatastoreProfile profile;
	
	public Datasource(DatastoreProfile profile, Backup backup) {
		this.profile = profile;
	}
	
	public abstract void downloadAll(Properties accessData, List<String> options, Storage storage, Progressable progressor)
			throws DatasourceException, StorageException;
	
}
