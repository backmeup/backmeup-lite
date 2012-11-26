package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import models.DatastoreProfile;

public class DatasinkProfilesListFilter {
	
	public List<DatasourceProfileFilter> sinkProfiles;
	
	public DatasinkProfilesListFilter(List<DatastoreProfile> profiles) {
		this.sinkProfiles = new ArrayList<DatasourceProfileFilter>();
		for (DatastoreProfile p : profiles) {
			this.sinkProfiles.add(new DatasourceProfileFilter(p));
		}
	}
	
	public class DatasourceProfileFilter {
	
		public String title;
		
		public String datasinkId;
		
		public String datasinkProfileId;
		
		public String description;
		
		public DatasourceProfileFilter(DatastoreProfile profile) {
			this.title = profile.profileName;
			this.datasinkId = profile.pluginClass;
			this.datasinkProfileId = Long.toString(profile.id);
			this.description = profile.description;
		}
		
	}

}
