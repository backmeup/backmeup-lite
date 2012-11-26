package org.backmeup.plugins.connectors.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import models.Backup;
import models.DatastoreProfile;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.backmeup.api.Progressable;
import org.backmeup.api.connectors.Datasink;
import org.backmeup.api.connectors.DatasinkException;
import org.backmeup.api.storage.DataObject;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;

import play.Logger;

public class ZipDatasink extends Datasink {
	
	// TODO needs to come from the profile
	private static final File DESTINATION_FOLDER = new File("zip-archives");
		
	public ZipDatasink(DatastoreProfile profile, Backup backup) {
		super(profile, backup);
	}

	@Override
	public String upload(Properties accessData, Storage storage, Progressable progressor)
			throws DatasinkException, StorageException {
		
		try {
			if (!DESTINATION_FOLDER.exists())
				DESTINATION_FOLDER.mkdirs();
			
			File archive = new File(DESTINATION_FOLDER, Long.toString(new Date().getTime()) + ".zip");
			archive.createNewFile();
			
			FileOutputStream fOut = new FileOutputStream(archive);
			BufferedOutputStream bOut = new BufferedOutputStream(fOut);
			ZipOutputStream zipOut = new ZipOutputStream(bOut);
			
			Iterator<DataObject> it = storage.getDataObjects();
			while(it.hasNext()) {
				DataObject next = it.next();
				
				String relativePath = next.getPath();
				if (relativePath.startsWith("/"))
					relativePath = relativePath.substring(1);
				
				Logger.info("Adding to ZIP: " + relativePath);
				
				zipOut.putNextEntry(new ZipArchiveEntry(relativePath));
				zipOut.write(next.getBytes(), 0, (int) next.getLength());
				zipOut.closeEntry();
			}
			
			zipOut.flush();
			zipOut.close();
			bOut.close();
			fOut.close();
			
			return archive.getAbsolutePath();
		} catch (IOException e) {
			throw new DatasinkException(e);
		}
	}

	@Override
	public File getStorageBasePath(DatastoreProfile profile, Backup backup) {
		return null;
	}

}