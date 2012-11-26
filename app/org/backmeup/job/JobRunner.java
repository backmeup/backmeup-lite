package org.backmeup.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.actions.Action;
import org.backmeup.api.actions.ActionException;
import org.backmeup.api.connectors.Datasink;
import org.backmeup.api.connectors.DatasinkException;
import org.backmeup.api.connectors.Datasource;
import org.backmeup.api.connectors.DatasourceException;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;

public class JobRunner {
	
	private Datasource instantiateDatasource(Backup backup) throws JobException {
		Datasource source = null;
		try {
			Class<? extends Datasource> clazz = 
				Class.forName(backup.job.sourceProfile.pluginClass).asSubclass(Datasource.class);

			source = clazz.getConstructor(DatastoreProfile.class, Backup.class)
					.newInstance(backup.job.sourceProfile, backup);
		} catch (Throwable e) {
			throw new JobException(e);
		}
		
		if (source == null)
			throw new JobException("Could not instantiate source: " + backup.job.sourceProfile.pluginClass);
		
		return source;
	}
	
	private List<Action> instantiateActions(Backup backup) throws JobException {
		List<Action> actions = new ArrayList<Action>();
		
		// TODO clean up
		boolean doIndexing = false;
		for (String actionClass : backup.job.actions.split(",")) {
			if (!actionClass.isEmpty()) {
				// Hack - indexing should always happen last
				if (actionClass.equals("org.backmeup.plugins.actions.indexing.IndexAction")) {
					doIndexing = true;
				} else {
					try {
						Class<? extends Action> clazz = 
							Class.forName(actionClass.trim()).asSubclass(Action.class);
		
						actions.add(clazz.newInstance());
					} catch (Throwable e) {
						throw new JobException(e);
					}			
				}
			}
		}
		
		if (doIndexing) {
			try {
				Class<? extends Action> clazz = 
					Class.forName("org.backmeup.plugins.actions.indexing.IndexAction").asSubclass(Action.class);

				actions.add(clazz.newInstance());
			} catch (Throwable e) {
				throw new JobException(e);
			}				
		}	
		
		return actions;
	}
	
	private Datasink instantiateDatasink(Backup backup) throws JobException {
		Datasink sink = null;
		try {
			Class<? extends Datasink> clazz = 
				Class.forName(backup.job.sinkProfile.pluginClass).asSubclass(Datasink.class);

			sink = clazz.getConstructor(DatastoreProfile.class, Backup.class)
					.newInstance(backup.job.sinkProfile, backup);
		} catch (Throwable e) {
			throw new JobException(e);
		}
		
		if (sink == null)
			throw new JobException("Could not instantiate source: " + backup.job.sourceProfile.pluginClass);
		
		return sink;
	}
		
	public void executeBackup(Backup backup, Storage storage) throws JobException {
		// Instantiate workflow objects
		Datasource source = instantiateDatasource(backup);
		List<Action> actions = instantiateActions(backup);
		Datasink sink = instantiateDatasink(backup);
		
		// Record status updates to DB
		BackupLogMessageProgressor progressor = new BackupLogMessageProgressor(backup);
		
		Properties props = new Properties();
		
		File storageBasePath = sink.getStorageBasePath(backup.job.sinkProfile, backup);
		if (storageBasePath != null)
			props.setProperty(JobProperties.PROP_SINK_BASE_PATH, storageBasePath.getAbsolutePath());
		
    	// Download from source
        try {
			source.downloadAll(props, new ArrayList<String>(), storage, progressor);
		} catch (DatasourceException e) {
			throw new JobException(e);
		} catch (StorageException e) {
			throw new JobException(e);
		}
        
        // Execute Actions
        for (Action a : actions) {
        	try {
				a.doAction(props, storage, backup, progressor);
			} catch (ActionException e) {
				throw new JobException(e);
			}
        }
        
    	// Upload to Sink
        try {
			sink.upload(props, storage, progressor);
		} catch (DatasinkException e) {
			throw new JobException(e);
		} catch (StorageException e) {
			throw new JobException(e);
		}
        
        // Remove temporary storage
        try {
	        storage.close();
        } catch (StorageException e) {
        	throw new JobException(e);
        }
   }

}
