package org.backmeup.api.actions;

import java.util.Properties;

import models.Backup;

import org.backmeup.api.Progressable;
import org.backmeup.api.storage.Storage;

public interface Action {
	
	public void doAction(Properties parameters, Storage storage, Backup backup, Progressable progressor)
			throws ActionException;

}
