package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import org.backmeup.api.connectors.DatastoreDescription;

public class DatasourceListFilter {
	
	public List<DatasourceDescriptionFilter> sources;
	
	public DatasourceListFilter(List<DatastoreDescription> datasources) {
		this.sources = new ArrayList<DatasourceDescriptionFilter>();
		for (DatastoreDescription d : datasources) {
			this.sources.add(new DatasourceDescriptionFilter(d));
		}
	}
	
	public class DatasourceDescriptionFilter {
		
		public String datasourceId;
	      
		public String title;
		
		public String description;
		
		public String imageURL;
		
		public DatasourceDescriptionFilter(DatastoreDescription description) {
			this.datasourceId = description.datastoreClass;
			this.title = description.title;
			this.description = description.description;
			this.imageURL = description.imageURL;
		}

	}

}
