package org.backmeup.api.connectors;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.Progressable;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.api.storage.metadata.MetainfoContainer;

/**
 * An abstract base class for datasources following a filesystem-like paradigm.
 * These datasources are arranged in a hierarchical structure of folders and
 * files.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public abstract class FilesystemLikeDatasource extends Datasource {

	public FilesystemLikeDatasource(DatastoreProfile profile, Backup backup) {
		super(profile, backup);
	}

	@Override
	public void downloadAll(Properties accessData, List<String> options,
			Storage storage, Progressable progressor) throws DatasourceException, StorageException {

		accessData.putAll(profile.getProperties());
		
		List<FilesystemURI> files = list(accessData, options);
		for (int i = 0; i < files.size(); i++) {
			FilesystemURI uri = files.get(i);
			download(accessData, options, uri, storage, progressor);
		}
	}

	private void download(Properties accessData, List<String> options,
			FilesystemURI uri, Storage storage, Progressable progressor)
			throws StorageException {
		
		MetainfoContainer metainfo = uri.getMetainfoContainer();
		if (uri.isDirectory()) {
			// Logger.info("Downloading contents of directory " + uri);
			for (FilesystemURI child : list(accessData, options, uri)) {
				download(accessData, options, child, storage, progressor);
			}
		} else {
			// Logger.info("Downloading file " + uri);
			progressor.progress(String.format("Downloading file %s ...",
					uri.toString()));
			InputStream is = getFile(accessData, options, uri);
			if (is == null) {
				// Logger.warn("Got a null input stream for " +
				// uri.getUri().getPath().toString());
			} else {
				URI destination = uri.getMappedUri();
				if (destination == null)
					destination = uri.getUri();
				storage.addFile(is, destination.getPath().toString(), metainfo);
			}
		}
	}

	public List<FilesystemURI> list(Properties accessData, List<String> options) {
		return list(accessData, options, null);
	}

	public abstract List<FilesystemURI> list(Properties accessData,
			List<String> options, FilesystemURI uri);

	public abstract InputStream getFile(Properties accessData,
			List<String> options, FilesystemURI uri);

}
