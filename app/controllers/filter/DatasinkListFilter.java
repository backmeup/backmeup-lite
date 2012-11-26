package controllers.filter;

import java.util.ArrayList;
import java.util.List;

import org.backmeup.api.connectors.DatastoreDescription;

public class DatasinkListFilter {
	
	public List<DatasinkDescriptionFilter> sinks;
	
	public DatasinkListFilter(List<DatastoreDescription> datasinks) {
		this.sinks = new ArrayList<DatasinkDescriptionFilter>();
		for (DatastoreDescription d : datasinks) {
			this.sinks.add(new DatasinkDescriptionFilter(d));
		}
	}
	
	public class DatasinkDescriptionFilter {
		
		public String datasinkId;
	      
		public String title;
		
		public String description;
		
		public String imageURL;
		
		public DatasinkDescriptionFilter(DatastoreDescription description) {
			this.datasinkId = description.datastoreClass;
			this.title = description.title;
			this.description = description.description;
			this.imageURL = description.imageURL;
		}

	}

}
