package org.backmeup.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.backmeup.api.actions.ActionDescription;
import org.backmeup.api.auth.Authorizable;
import org.backmeup.api.auth.Authorizable.AuthorizationType;
import org.backmeup.api.connectors.DatastoreDescription;
import org.backmeup.api.connectors.DatastoreDescription.DatastoreType;

import play.Logger;

public class PluginRegistry {
	
	private static final String COULD_NOT_LOAD_AUTHORIZABLE = "Could not instantiate authorizer class: ";
	
	private static PluginRegistry singleton = null;
	
	private Map<String, DatastoreDescription> sinks = new HashMap<String, DatastoreDescription>();
	
	private Map<String, DatastoreDescription> sources= new HashMap<String, DatastoreDescription>();
	
	private Map<String, ActionDescription> actions = new HashMap<String, ActionDescription>();
	
	private Map<String, String> authorizers = new HashMap<String, String>();
	
	public static PluginRegistry getInstance() {
		if (singleton == null) {
			singleton = new PluginRegistry();
			
			/** SOURCES **/
			
			singleton.sources.put("org.backmeup.plugins.connectors.dummy.DummyDatasource",
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.dummy.DummyDatasource",
					"DummySource",
					"A dummy Datasource",
					DatastoreType.SOURCE,
					AuthorizationType.NONE,
					"http://localhost:9000/assets/images/icons/datasource_neutral_32.png"
				));
			
			singleton.sources.put("org.backmeup.plugins.connectors.twitter.TwitterDatasource", 
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.twitter.TwitterDatasource", 
					"Twitter", 
					"BackMeUp Twitter Plug-In",
					DatastoreType.SOURCE,
					AuthorizationType.OAUTH,
					"http://localhost:9000/assets/images/icons/datastore_twitter_32.png"
				));

			singleton.sources.put("org.backmeup.plugins.connectors.dropbox.DropboxDatasource", 
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.dropbox.DropboxDatasource", 
					"Dropbox", 
					"Dropbox Datasource",
					DatastoreType.SOURCE,
					AuthorizationType.OAUTH,
					"http://localhost:9000/assets/images/icons/datastore_dropbox_32.png"
				));
			
			singleton.sources.put("org.backmeup.plugins.connectors.facebook.FacebookDatasource", 
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.facebook.FacebookDatasource", 
					"Facebook", 
					"Facebook Datasource",
					DatastoreType.SOURCE,
					AuthorizationType.OAUTH,
					"http://localhost:9000/assets/images/icons/datastore_facebook_32.png"
				));
			
			singleton.sources.put("org.backmeup.plugins.connectors.moodle.MoodleDatasource", 
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.moodle.MoodleDatasource", 
					"Moodle", 
					"Moodle Datasource",
					DatastoreType.SOURCE,
					AuthorizationType.INPUT_BASED,
					"http://localhost:9000/assets/images/icons/datastore_moodle_32.png"
				));
			
			singleton.sources.put("org.backmeup.plugins.connectors.mail.MailDatasource", 
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.mail.MailDatasource", 
					"E-Mail", 
					"E-Mail Datasource",
					DatastoreType.SOURCE,
					AuthorizationType.INPUT_BASED,
					"http://localhost:9000/assets/images/icons/datasource_neutral_32.png"
				));
			
			/** SINKS **/
			
			singleton.sinks.put("org.backmeup.plugins.connectors.dummy.DummyDatasink",
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.dummy.DummyDatasink",
					"DummySink",
					"A dummy Datasink",
					DatastoreType.SINK,
					AuthorizationType.NONE,
					"http://localhost:9000/assets/images/icons/datasink_neutral_32.png"
				));
				
			singleton.sinks.put("org.backmeup.plugins.connectors.zip.ZipDatasink",
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.zip.ZipDatasink",
					"ZIP Download",
					"A sink that downloads to a ZIP archive on the local hard drive",
					DatastoreType.SINK,
					AuthorizationType.NONE,
					"http://localhost:9000/assets/images/icons/datastore_zip_32.png"
				));
			
			singleton.sinks.put("org.backmeup.plugins.connectors.dropbox.DropboxDatasink",
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.dropbox.DropboxDatasink",
					"Dropbox Upload",
					"A sink that uploads to a Dropbox Account",
					DatastoreType.SINK,
					AuthorizationType.OAUTH,
					"http://localhost:9000/assets/images/icons/datastore_dropbox_32.png"
				));
			
			singleton.sinks.put("org.backmeup.plugins.connectors.localfs.LocalFilesystemDatasink",
				new DatastoreDescription(
					"org.backmeup.plugins.connectors.localfs.LocalFilesystemDatasink",
					"Local Filesystem",
					"A sink that dowloads to your harddisk",
					DatastoreType.SINK,
					AuthorizationType.INPUT_BASED,
					"http://localhost:9000/assets/images/icons/datasink_neutral_32.png"
				));
			
			/** ACTIONS **/
			
			singleton.actions.put("org.backmeup.plugins.actions.indexing.IndexAction",
				new ActionDescription(
					"org.backmeup.plugins.actions.indexing.IndexAction",
					"Indexing",
					"Adds the backup to the central search index"));

			singleton.actions.put("org.backmeup.plugins.actions.thumbnail.ThumbnailAction",
				new ActionDescription(
					"org.backmeup.plugins.actions.thumbnail.ThumbnailAction",
					"Thumbnails",
					"Creates thumbnails for backed-up media"));
			
			/** Authorizer mappings **/
			
			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.twitter.TwitterDatasource", 
					"org.backmeup.plugins.connectors.twitter.TwitterAuthenticator");
			
			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.dropbox.DropboxDatasource", 
					"org.backmeup.plugins.connectors.dropbox.DropboxAuthenticator");
			
			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.dropbox.DropboxDatasink", 
					"org.backmeup.plugins.connectors.dropbox.DropboxAuthenticator");
			
			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.facebook.FacebookDatasource", 
					"org.backmeup.plugins.connectors.facebook.FacebookAuthenticator");
			
			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.moodle.MoodleDatasource", 
					"org.backmeup.plugins.connectors.moodle.MoodleAuthenticator");

			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.localfs.LocalFilesystemDatasink", 
					"org.backmeup.plugins.connectors.localfs.LocalFilesystemAuthorizable");
			
			singleton.authorizers.put(
					"org.backmeup.plugins.connectors.mail.MailDatasource", 
					"org.backmeup.plugins.connectors.mail.MailAuthenticator");
		}
		
		return singleton;
	}

	public List<DatastoreDescription> listAvailableDatasinks() {
		return new ArrayList<DatastoreDescription>(sinks.values());
	}
	
	public List<DatastoreDescription> listAvailableDatasources() {
		return new ArrayList<DatastoreDescription>(sources.values());
	}
	
	public List<ActionDescription> listAvailableActions() {
		return new ArrayList<ActionDescription>(actions.values());
	}
	
	public DatastoreDescription getSinkDescription(String pluginClass) {
		return sinks.get(pluginClass);
	}
	
	public DatastoreDescription getSourceDescription(String pluginClass) {
		return sources.get(pluginClass);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Authorizable> T getAuthorizable(String pluginClass, Class<T> type) {
		String authClass = authorizers.get(pluginClass);
		if (authClass == null)
			return null;
		
		// Instantiate authorizer class
		T authorizable = null;
		try{
			Class<? extends Authorizable> clazz =
					Class.forName(authClass).asSubclass(Authorizable.class);
				
			authorizable = (T) clazz.newInstance();
		} catch (Throwable t) {
			Logger.warn(COULD_NOT_LOAD_AUTHORIZABLE + pluginClass);
			Logger.warn(t.getMessage());
		}
		
		return authorizable;
	}
	
}
