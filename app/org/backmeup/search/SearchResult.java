package org.backmeup.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	
	private long took;
	
	private List<FileItem> items = new ArrayList<FileItem>();
	
	public SearchResult(long took, List<FileItem> items) {
		this.took = took;
		this.items = items;
	}

	public long getTook() {
		return took;
	}

	public void setTook(long took) {
		this.took = took;
	}

	public List<FileItem> getItems() {
		return items;
	}

	public void setItems(List<FileItem> items) {
		this.items = items;
	}

}
