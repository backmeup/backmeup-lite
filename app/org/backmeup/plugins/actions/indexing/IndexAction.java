package org.backmeup.plugins.actions.indexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import models.Backup;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.backmeup.api.Progressable;
import org.backmeup.api.actions.Action;
import org.backmeup.api.actions.ActionException;
import org.backmeup.api.storage.DataObject;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.api.storage.metadata.Metainfo;
import org.backmeup.api.storage.metadata.MetainfoContainer;
import org.backmeup.job.JobProperties;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.StringText;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.xml.sax.ContentHandler;

import play.Logger;

public class IndexAction implements Action {
	
	private static final String COULD_NOT_EXTRACT_FULLTEXT = "Could not extract fulltext for file: ";

	@Override
	public void doAction(Properties parameters, Storage storage, Backup backup, Progressable progressor)
			throws ActionException {
		
		Client client = null;
		try {
			client = new TransportClient().addTransportAddress(
					new InetSocketTransportAddress(IndexProperties.getHost(), IndexProperties.getPort()));
			Iterator<DataObject> dataObjects = storage.getDataObjects();
			while (dataObjects.hasNext()) {
				DataObject dob = dataObjects.next();
				progressor.progress("Indexing " + dob.getPath());
				updateIndex(parameters, backup, dob, client);
			}
		} catch (StorageException e) {
			throw new ActionException(e);
		} finally {
			if (client != null)
				client.close();
		}
		
		progressor.progress("Indexing complete");
	}
	
	private void updateIndex(Properties parameters, Backup backup, DataObject dataObject, Client client) throws ActionException {
		try {
			// Add standard Backmeup metadata
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
			contentBuilder.field(IndexProperties.FIELD_OWNER_ID, backup.job.user.id);
			// contentBuilder.field(IndexProperties.FIELD_OWNER_NAME, backup.job.user.username);
			contentBuilder.field(IndexProperties.FIELD_FILENAME, getFilename(dataObject.getPath()));
			
			String basePath = parameters.getProperty(JobProperties.PROP_SINK_BASE_PATH);
			if (basePath != null) {
				String path = "file://" + (basePath + dataObject.getPath()).replace("//", "/");
				Logger.info("Indexing full path location: " + path);
				contentBuilder.field(IndexProperties.FIELD_PATH, path);
			} else {
				contentBuilder.field(IndexProperties.FIELD_PATH, dataObject.getPath());
			}
				
			contentBuilder.field(IndexProperties.FIELD_FILE_HASH, dataObject.getMD5Hash());
			contentBuilder.field(IndexProperties.FIELD_BACKUP_SINK, backup.job.sinkProfile.pluginClass);
			contentBuilder.field(IndexProperties.FIELD_BACKUP_AT, new Date().getTime());
			contentBuilder.field(IndexProperties.FIELD_JOB_ID, backup.job.id);
			contentBuilder.field(IndexProperties.FIELD_BACKUP_ID, backup.id);
			contentBuilder.field(IndexProperties.FIELD_BACKUP_SOURCE, backup.job.sourceProfile.pluginClass);
	
			// Add fulltext
			String fulltext = getFullText(dataObject);
			if (fulltext != null)
				contentBuilder.field(IndexProperties.FIELD_FULLTEXT, fulltext);
			
			// Add custom metadata from plugins
			MetainfoContainer metainfoContainer = dataObject.getMetainfoContainer();
			if (metainfoContainer != null) {
				Iterator<Metainfo> it = metainfoContainer.iterator();
				while (it.hasNext()) {
					Properties metainfo = it.next().getAttributes();
					for (Object key : metainfo.keySet()) {
						Logger.info("Adding custom property: " + key + "=" + metainfo.get(key) + " (" + metainfo.get(key).getClass().getName() + ")");
						contentBuilder.field(key.toString(), new StringText(metainfo.get(key).toString()));
					}
				}
			}
	
			contentBuilder = contentBuilder.endObject();
			client.prepareIndex(IndexProperties.getIndexName(), IndexProperties.getDocumentType())
				.setSource(contentBuilder)
				.setRefresh(true).execute().actionGet();
		} catch (IOException e) {
			throw new ActionException(e);
		}
	}
	
	private String getFilename(String path) {
		if (path.indexOf('/') > -1)
			return path.substring(path.lastIndexOf('/') + 1);

		return path;
	}
	
	private String getFullText(DataObject dataObject) {
		try {
			ContentHandler handler = new BodyContentHandler(10*1024*1024);
			Metadata metadata = new Metadata();
			AutoDetectParser parser = new AutoDetectParser();
			parser.parse(new ByteArrayInputStream(dataObject.getBytes()), handler, metadata, new ParseContext());
			return handler.toString();
		} catch (Exception e) {
			Logger.info(COULD_NOT_EXTRACT_FULLTEXT + dataObject.getPath());
		}
		return null;
	}

}
