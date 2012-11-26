package org.backmeup.api.connectors;

public class DatasourceException extends Exception {

	private static final long serialVersionUID = -8450159521462478596L;

	public DatasourceException(String msg) {
		super(msg);
	}

	public DatasourceException(Throwable t) {
		super(t);
	}

}