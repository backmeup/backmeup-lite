package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import models.DatastoreProfile;

public class DatasourceProfilesListFilter {
	
	public List<DatasourceProfileFilter> sourceProfiles;
	
	public DatasourceProfilesListFilter(List<DatastoreProfile> profiles) {
		this.sourceProfiles = new ArrayList<DatasourceProfileFilter>();
		for (DatastoreProfile p : profiles) {
			this.sourceProfiles.add(new DatasourceProfileFilter(p));
		}
	}
	
	public class DatasourceProfileFilter {
	
		public String title;
		
		public String description;
		
		public String datasourceId;
		
		public String datasourceProfileId;
		
		public DatasourceProfileFilter(DatastoreProfile profile) {
			this.title = profile.profileName;
			this.description = profile.description;
			this.datasourceId = profile.pluginClass;
			this.datasourceProfileId = Long.toString(profile.id);
		}
		
	}

}
