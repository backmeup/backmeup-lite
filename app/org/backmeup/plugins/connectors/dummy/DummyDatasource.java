package org.backmeup.plugins.connectors.dummy;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.Progressable;
import org.backmeup.api.connectors.Datasource;
import org.backmeup.api.connectors.DatasourceException;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.api.storage.metadata.MetainfoContainer;

import play.Logger;

public class DummyDatasource extends Datasource {

	public DummyDatasource(DatastoreProfile profile, Backup backup) {
		super(profile, backup);
	}

	@Override
	public void downloadAll(Properties accessData, List<String> options, Storage storage, Progressable progressor)
			throws DatasourceException, StorageException {
    
	    InputStream file1 = 
	    		new ByteArrayInputStream("I am an important text file.\nPlease backup me up!".getBytes());
	    progressor.progress("Downloading: plain.txt");
	    storage.addFile(file1, "/plain.txt", new MetainfoContainer());

	    InputStream file2 = 
	    		new ByteArrayInputStream(("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
	        + "http://www.w3.org/TR/html4/strict.dtd\">"
	        + "<html>"
	        + "<head>"
	        + "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"
	        + "    <title>title</title>"
	        + "</head>"
	        + "<body><p>This is one important text file.\nPlease create a backup with this file</p></body></html>").getBytes());
	    progressor.progress("Downloading: plain.html");
	    storage.addFile(file2, "/plain.html", new MetainfoContainer());
	    
	    try {
		    InputStream file3 = new FileInputStream("public/images/cc.jpg");
		    storage.addFile(file3, "/creative-commons.jpg", new MetainfoContainer());
	    } catch (IOException e) {
	    	Logger.warn("Could not load test file for dummy datasource.");
	    }
	}

}
