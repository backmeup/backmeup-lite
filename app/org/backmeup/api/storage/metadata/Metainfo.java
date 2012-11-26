package org.backmeup.api.storage.metadata;

import java.util.HashMap;
import java.util.Properties;

public class Metainfo {
	
	private String id;
	
	private String type;
	
	private long modified;
	
	private String destination;
	
	private String source;
	
	private long backupedAt;
	
	private String parent;
	
	private HashMap<String, String> attributes = new HashMap<String, String>();

	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getBackupedAt() {
		return backupedAt;
	}

	public void setBackupedAt(long backupedAt) {
		this.backupedAt = backupedAt;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public Properties getAttributes() {
		Properties props = new Properties();
		
		if (id != null)
			props.put("id", id);
		
		if (type != null)
			props.put("type", type);
		
		if (modified != 0)
			props.put("modified", Long.valueOf(modified));
		
		if (destination != null)
			props.put("destination", destination);
		
		if (source != null)
			props.put("source", source);
		
		if (backupedAt != 0)
			props.put("backupedAt", Long.valueOf(backupedAt));
		
		if (parent != null)
			props.put("parent", parent);
		
		props.putAll(attributes);
		
		return props;
	}

}
