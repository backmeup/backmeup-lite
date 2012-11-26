package org.backmeup.plugins.actions.indexing;

import play.Configuration;
import play.Play;

public class IndexProperties {
	
	private static final Configuration config = Play.application().configuration();
	
	public static final String FIELD_OWNER_ID = "owner_id";

	public static final String FIELD_OWNER_NAME = "owner_name";

	public static final String FIELD_FILENAME = "filename";

	public static final String FIELD_PATH = "path";

	public static final String FIELD_THUMBNAIL_PATH = "thumbnail_path";

	public static final String FIELD_BACKUP_SOURCE = "backup_source";

	public static final String FIELD_BACKUP_SINK = "backup_sink";

	public static final String FIELD_FILE_HASH = "file_md5_hash";

	public static final String FIELD_BACKUP_AT = "backup_at";

	public static final String FIELD_CONTENT_TYPE = "Content-Type";

	public static final String FIELD_JOB_ID = "job_id";
	
	public static final String FIELD_BACKUP_ID = "backup_id";
	
	public static final String FIELD_FULLTEXT = "fulltext";
	
	public static String HOST_NAME = null;
	
	public static Integer HOST_PORT = null;
	
	public static String getIndexName() {
		// TODO we could make this configurable later on
		return "backmeup";
	}
	
	public static String getDocumentType() {
		// TODO we could make this configurable later on
		return "backup";
	}
	
	public static String getHost() {
		if (HOST_NAME == null) {
			HOST_NAME = config.getString("index.host");
			if (HOST_NAME == null)
				HOST_NAME = "localhost";
		}

		return HOST_NAME;
	}
	
	public static int getPort() {
		if (HOST_PORT == null) {
			try {
				HOST_PORT = Integer.valueOf(Integer.parseInt(config.getString("index.port")));
			} catch (Throwable t) {
				HOST_PORT = Integer.valueOf(9300);
			}
		}

		return HOST_PORT;
	}

}
