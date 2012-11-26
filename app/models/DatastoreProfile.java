package models;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.annotation.EnumValue;

import play.db.ebean.Model;

@Entity
public class DatastoreProfile extends Model {

	private static final long serialVersionUID = -7087262255989325435L;

	@Id
	public Long id;
	
	@ManyToOne
	public User user;
	
	public String profileName;
	
	public String description;
	
	public String pluginClass;
	
	public Type type;
	
	public Date created;
	
	public Date modified;
	
	public static Finder<Long, DatastoreProfile> find = 
			new Finder<Long, DatastoreProfile>(Long.class, DatastoreProfile.class);
	
	public DatastoreProfile(User user, String profileName, String description, String pluginClass, Type type) {
		this.user = user;
		this.profileName = profileName;
		this.description = description;
		this.pluginClass = pluginClass;
		this.created = new Date();
		this.modified = new Date();
		this.type = type;
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		for (DatastoreProfileProperty p : DatastoreProfileProperty.findByProfile(this)) {
			properties.put(p.propertyName, p.propertyValue);
		}
		return properties;
	}

	public String getProperty(String propertyName) {
		List<DatastoreProfileProperty> profiles = 
				DatastoreProfileProperty.findByProfileAndName(this, propertyName);
		
		if (profiles.size() > 0)
			return profiles.get(0).propertyValue;
		else
			return null;
	}
	
	public void setProperties(Properties props) {
		for (Entry<Object, Object> entry : props.entrySet()) {
			this.setProperty(entry.getKey().toString(), entry.getValue().toString());
		}
	}

	public void setProperty(String name, String value) {
		// Remove if exists
		for (DatastoreProfileProperty old : DatastoreProfileProperty.findByProfileAndName(this, name)) {
			old.delete();
		}

		DatastoreProfileProperty property = new DatastoreProfileProperty(this, name, value);
		property.save();
	}
	
	public static List<DatastoreProfile> findByUser(User user) {
		return DatastoreProfile.find
				.fetch("user")
				.where()
				.eq("user.id", user.id)
				.findList();
	}
	
	public static List<DatastoreProfile> findByUser(User user, Type type) {
		return DatastoreProfile.find
				.fetch("user")
				.where()
				.eq("user.id", user.id)
				.eq("type", type)
				.findList();		
	}
	
	public static void deleteCascaded(DatastoreProfile profile) {
		// Delete all jobs that use this profile as source or sink profile
		// TODO optimize
		for (Job job : Job.findBySourceProfile(profile)) {
			Job.deleteCascaded(job);
		}
		
		for (Job job : Job.findBySinkProfile(profile)) {
			Job.deleteCascaded(job);
		}
		
		// Delete all properties
		for (DatastoreProfileProperty property : DatastoreProfileProperty.findByProfile(profile)) {
			property.delete();
		}
		profile.delete();
	}
	
	public enum Type {
		@EnumValue("SINK")		
		DATASINK, 
		
		@EnumValue("SOURCE")
		DATASOURCE
	}

}
