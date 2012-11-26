package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.annotation.EnumValue;

import play.db.ebean.Model;

@Entity
public class UserProperty extends Model {

	private static final long serialVersionUID = 1955391716514039526L;

	@Id
	public Long id;
	
	@ManyToOne
	public User user;
	
	public String key;
	
	public String value;
	
	public Type type;
	
	public static Finder<Long, UserProperty> find = 
			new Finder<Long, UserProperty>(Long.class, UserProperty.class);
	
	public UserProperty(User user, String key, String value, Type type) {
		this.user = user;
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	public static List<UserProperty> findByUser(User user) {
		return UserProperty.find
				.where()
				.eq("user.id", user.id)
				.findList();
	}
	
	public static UserProperty getProperty(User user, String key) {
		List<UserProperty> properties = UserProperty.find
				.where()
				.eq("user.id", user.id)
				.eq("key", key)
				.findList();
		
		if (properties.size() > 0)
			return properties.get(0);
		
		return null;
	}
	
	public enum Type {
		// The job is currently running
		@EnumValue("STRING")
		STRING,
		
		@EnumValue("NUMBER")
		NUMBER,
		
		// The job has completed successfully
		@EnumValue("BOOLEAN")
		BOOLEAN
	}
	
}
