package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class DatastoreProfileProperty extends Model {

	private static final long serialVersionUID = -1911263956150607494L;

	@Id
	public Long id;
	
	@ManyToOne
	public DatastoreProfile profile;

	public String propertyName;

	public String propertyValue;
	
	public static Finder<Long, DatastoreProfileProperty> find = 
			new Finder<Long, DatastoreProfileProperty>(Long.class, DatastoreProfileProperty.class);
	
	public DatastoreProfileProperty(DatastoreProfile profile, String propertyName, String propertyValue) {
		this.profile = profile;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}
	
	public static List<DatastoreProfileProperty> findByProfile(DatastoreProfile profile) {
		return DatastoreProfileProperty.find
				.where()
				.eq("profile.id", profile.id)
				.findList();
	}
	
	public static List<DatastoreProfileProperty> findByProfileAndName(DatastoreProfile profile, String name) {
		return DatastoreProfileProperty.find
				.where()
				.eq("profile.id", profile.id)
				.eq("propertyName", name)
				.findList();
	}

}
