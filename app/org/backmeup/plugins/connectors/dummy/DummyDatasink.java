package org.backmeup.plugins.connectors.dummy;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.Progressable;
import org.backmeup.api.connectors.Datasink;
import org.backmeup.api.storage.DataObject;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.api.storage.metadata.Metainfo;

public class DummyDatasink extends Datasink {
	
	public DummyDatasink(DatastoreProfile profile, Backup backup) {
		super(profile, backup);
	}

	@Override
	public String upload(Properties accessData, Storage storage, Progressable progressor)
			throws StorageException {

		Iterator<DataObject> it = storage.getDataObjects();
		while (it.hasNext()) {
			DataObject dob = it.next();
			progressor.progress("Uploading: " + dob.getPath());
			
			System.out.println("=============================================");
			System.out.println("Object: " + dob.getPath());
			System.out.println("=============================================");
			
			Iterator<Metainfo> meta = dob.getMetainfoContainer().iterator();
			while (meta.hasNext()) {
				Metainfo next = meta.next();
				next.toString();				
			}
	    }
	    return "/fake/destination/path";
	  }

	@Override
	public File getStorageBasePath(DatastoreProfile profile, Backup backup) {
		return null;
	}

}
