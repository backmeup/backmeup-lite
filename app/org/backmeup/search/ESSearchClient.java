package org.backmeup.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.backmeup.plugins.actions.indexing.IndexProperties;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import models.User;

public class ESSearchClient {
	
	public Map<String, Object> getItemByFileKey(User user, String fileId) {
		// IDs in backmeup are "owner:hash:timestamp"
		String[] id = fileId.split(":");
		
		if (id.length != 3)
			throw new IllegalArgumentException("Invalid file ID: " + fileId);

		Long ownerId = Long.parseLong(id[0]);
		String fileHash = id[1];
		Long timestamp = Long.parseLong(id[2]);

		QueryBuilder qBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery(IndexProperties.FIELD_OWNER_ID, ownerId))
				.must(QueryBuilders.matchQuery(IndexProperties.FIELD_FILE_HASH, fileHash))
				.must(QueryBuilders.matchQuery(IndexProperties.FIELD_BACKUP_AT, timestamp));
		
		Client client = new TransportClient()
			.addTransportAddress(new InetSocketTransportAddress(IndexProperties.getHost(), IndexProperties.getPort()));
		SearchResponse response = 
				client.prepareSearch(IndexProperties.getIndexName()).setQuery(qBuilder).execute().actionGet();	
		client.close();
		
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length == 0)
			return null;

		// This should never happen
		if (hits.length > 1)
			throw new RuntimeException("Index has two files with the same ID: " + fileId);
			
		return hits[0].getSource(); 	
	}
	
	public SearchResult queryIndex(User user, String query) {
		String queryString = null;
		String[] tokens = query.split(" ");
		if (tokens.length == 0) {
			queryString = "*";
		} else if (tokens.length == 1) {
			queryString = "+" + query + "*";
		} else {
			StringBuffer sb = new StringBuffer("*");
			for (int i=0; i<tokens.length; i++) {
				sb.append(tokens[i]);
				if (i < tokens.length - 1)
					sb.append("* OR *");
			}
			queryString = sb.toString() + "*";
		}

		QueryBuilder qBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery(IndexProperties.FIELD_OWNER_ID, user.id))
				.must(QueryBuilders.queryString(queryString));

		Client client = new TransportClient()
			.addTransportAddress(new InetSocketTransportAddress(IndexProperties.getHost(), IndexProperties.getPort()));
		SearchResponse response = client.prepareSearch(IndexProperties.getIndexName())
				.setQuery(qBuilder).setSize(10000).execute().actionGet();
		client.close();
		
		List<FileItem> fileItems = new ArrayList<FileItem>();
		for (SearchHit hit : response.getHits()) {
			fileItems.add(toFileItem(hit));
		}
		
		return new SearchResult(response.getTookInMillis(), fileItems);
	}
	
	public static FileItem toFileItem(SearchHit searchHit) {
		Map<String, Object> source = searchHit.getSource();
		
		FileItem item = new FileItem(
				// Filename
				source.get(IndexProperties.FIELD_FILENAME).toString(), 
				
				// Filepath
				source.get(IndexProperties.FIELD_PATH).toString(),
				
				// TODO Mimetype 
				"unknown", 
				
				// Timestamp
				new Date(((Long) source.get(IndexProperties.FIELD_BACKUP_AT)).longValue()),
				
				// Backup ID
				Long.valueOf(((Integer) source.get(IndexProperties.FIELD_BACKUP_ID)).longValue()),
				
				// Job ID
				Long.valueOf(((Integer) source.get(IndexProperties.FIELD_JOB_ID)).longValue()),
				
				// Backup source
				source.get(IndexProperties.FIELD_BACKUP_SOURCE).toString(),
				
				// Backup sink
				source.get(IndexProperties.FIELD_BACKUP_SINK).toString()); 
		
		if (source.get(IndexProperties.FIELD_THUMBNAIL_PATH) != null) {
			// Generate unique file key
			String hash = source.get(IndexProperties.FIELD_FILE_HASH).toString();
	    	Integer owner = (Integer) source.get(IndexProperties.FIELD_OWNER_ID);
	    	Long timestamp = (Long) source.get(IndexProperties.FIELD_BACKUP_AT);
	    	String fileKey = owner + ":" + hash + ":" + timestamp;
			item.setThumbnailURL("thumbnails/" + owner + "/" + fileKey);
		}
		
		return item;
	}

}
