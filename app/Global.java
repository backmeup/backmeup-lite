import java.util.List;
import java.util.Map;

import org.backmeup.job.JobManager;
import org.backmeup.job.impl.SimpleJobManager;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import models.User;

import com.avaje.ebean.Ebean;

import play.*;
import play.libs.Yaml;

public class Global extends GlobalSettings {
	
	private Client esClient = null;
	
	@Override
    public void onStart(Application app) {
		// Insert bootstrap data
        InitialData.insert(app);
        
        // Start embedded ElasticSearch node
        Logger.info("Starting ElasticSearch node: http://localhost:9200");
        Node indexNode = NodeBuilder.nodeBuilder().node();
        esClient = indexNode.client();
        
        // Fire up the Job Manager
        JobManager jobManager = SimpleJobManager.getInstance();
        jobManager.start();
    }
    
	@Override
	public void onStop(Application app) {
		Logger.info("Stopping ElasticSearch");
		if (esClient != null)
			esClient.close();
		Logger.info("Done.");
	}
	
    static class InitialData {
        public static void insert(Application app) {
            if(Ebean.find(User.class).findRowCount() == 0) {
                @SuppressWarnings("unchecked")
				Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");

                // Insert users
                Ebean.save(all.get("users"));

                // Insert datastore profiles
                Ebean.save(all.get("profiles"));
                
                // Insert datastore profile properties
                Ebean.save(all.get("datastoreProfileProperties"));
                
                // Insert test jobs
                Ebean.save(all.get("jobs"));
            }
        }
        
    }
    
}