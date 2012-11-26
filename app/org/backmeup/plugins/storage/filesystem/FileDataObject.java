package org.backmeup.plugins.storage.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.backmeup.api.storage.DataObject;
import org.backmeup.api.storage.metadata.MetainfoContainer;

public class FileDataObject extends DataObject {
	
	private File file;
	
	private File metaFile;

	private String path;

	public FileDataObject(String path) {
		this(new File(path), path);
	}
	
	public FileDataObject(File file, String path) {
		this.file = file;
		this.metaFile = new File(file.getAbsolutePath() + ".meta.json");
		
		String[] parts = path.split ("/");
		this.path = "";
		// Ignore the first folder. parts[0] = "", parts[1] = "job-xxxxx"
		for (int i = 2; i < parts.length; i++)
		{
			this.path += "/" + parts[i];
		}
	}

	@Override
	public byte[] getBytes() throws IOException {
	  FileInputStream fis = null;
	  try {
	    fis = new FileInputStream(file);
  		return IOUtils.toByteArray(fis);
	  } finally {
	    fis.close();
	  }
	}
	
	@Override
	public long getLength() {
		return file.length();
	}
	
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public MetainfoContainer getMetainfoContainer() {
		// TODO we need a better solution - meta needs to be directly
		// backed by the filesystem
		try {
			if (metaFile.exists()) {
				String json = FileUtils.readFileToString(metaFile);
				return MetainfoContainer.fromJSON(json);
			}
		} catch (IOException e) {
			// Return empty MetainfoContainer if JSON doesn't exist
		}
		return new MetainfoContainer();
	}
	
	@Override
	public void setMetainfoContainer(MetainfoContainer metadata) {
		try {
			FileUtils.writeStringToFile(metaFile, MetainfoContainer.toJSON(metadata));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
