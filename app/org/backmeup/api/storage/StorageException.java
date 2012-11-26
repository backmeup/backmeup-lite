package org.backmeup.api.storage;

public class StorageException extends Exception {

	private static final long serialVersionUID = 1817173544876192684L;

	public StorageException(String msg) {
		super(msg);
	}

	public StorageException(Throwable t) {
		super(t);
	}
	
}
