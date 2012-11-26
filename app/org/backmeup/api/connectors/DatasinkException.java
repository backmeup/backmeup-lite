package org.backmeup.api.connectors;

public class DatasinkException extends Exception {

	private static final long serialVersionUID = 2418320878284682610L;

	public DatasinkException(String msg) {
		super(msg);
	}

	public DatasinkException(Throwable t) {
		super(t);
	}

}
