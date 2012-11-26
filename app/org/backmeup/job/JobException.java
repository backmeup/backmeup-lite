package org.backmeup.job;

public class JobException extends Exception {

	private static final long serialVersionUID = 4422099767764379562L;

	public JobException(String msg) {
		super(msg);
	}

	public JobException(Throwable t) {
		super(t);
	}
	
}
