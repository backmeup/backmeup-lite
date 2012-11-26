package org.backmeup.plugins.connectors.localfs;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.apache.commons.io.FileUtils;
import org.backmeup.api.Progressable;
import org.backmeup.api.connectors.Datasink;
import org.backmeup.api.connectors.DatasinkException;
import org.backmeup.api.connectors.DatastoreDescription;
import org.backmeup.api.storage.DataObject;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.plugins.PluginRegistry;

import play.Logger;

public class LocalFilesystemDatasink extends Datasink {

	// TODO make configurable
	private static final File BASE_PATH = new File("my-backups");
	
	private File storageDir;
	
	static {
		if (!BASE_PATH.exists())
			BASE_PATH.mkdirs();
	}
	
	public LocalFilesystemDatasink(DatastoreProfile profile, Backup backup) {
		super(profile, backup);	
		storageDir = getStorageBasePath(profile, backup);
		storageDir.mkdir();
	}
	
	@Override
	public File getStorageBasePath(DatastoreProfile profile, Backup backup) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(backup.startTime);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		DatastoreDescription source = 
				PluginRegistry.getInstance().getSourceDescription(backup.job.sourceProfile.pluginClass);
		
		return new File(BASE_PATH, year + "-" + month + "-" + day + "-" + source.title.toLowerCase());
	}

	@Override
	public String upload(Properties accessData, Storage storage,
			Progressable progressor) throws DatasinkException, StorageException {
		
		Iterator<DataObject> dataObjects = storage.getDataObjects();
		while (dataObjects.hasNext()) {
			DataObject next = dataObjects.next();
			Logger.info("Storing: " + next.getPath());
			try {
				FileUtils.writeByteArrayToFile(new File(storageDir, next.getPath()), next.getBytes());
			} catch (IOException e) {
				Logger.warn("Could not write to file: " + next.getPath());
			}
		}
		
		return "";
	}

}
