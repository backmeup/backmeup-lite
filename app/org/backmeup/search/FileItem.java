package org.backmeup.search;

import java.util.Date;

public class FileItem {
	
	private String filename;
	
	private String filepath;

	private String mimetype;
	
	private Date timestamp;

	private Long backupId;
	
	private Long jobId;
	
	private String sourceId;
	
	private String sinkId;
	
	private String thumbnailURL;
	
	public FileItem(String filename, String filepath, String mimetype, Date timestamp, Long backupId, 
			Long jobId, String sourceId, String sinkId) {
		this(filename, filepath, mimetype, timestamp, backupId, jobId, sourceId, sinkId, null);
	}
	
	public FileItem(String filename, String filepath, String mimetype, Date timestamp, Long backupId,
			Long jobId, String sourceId, String sinkId, String thumbnailURL) {
		this.filename = filename;
		this.filepath = filepath;
		this.mimetype = mimetype;
		this.timestamp = timestamp;
		this.backupId = backupId;
		this.jobId = jobId;
		this.sourceId = sourceId;
		this.sinkId = sinkId;
		this.thumbnailURL = thumbnailURL;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Long getBackupId() {
		return backupId;
	}

	public void setBackupId(Long backupId) {
		this.backupId = backupId;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSinkId() {
		return sinkId;
	}

	public void setSinkId(String sinkId) {
		this.sinkId = sinkId;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

}
