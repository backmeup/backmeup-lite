package org.backmeup.api.storage.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import play.libs.Json;

public class MetainfoContainer implements Iterable<Metainfo> {
	
	private static ObjectMapper mapper = new ObjectMapper();

	private List<Metainfo> metainfo;
	
	private MetainfoContainer(List<Metainfo> metainfo) {
		this.metainfo = metainfo;
	}
	
	public MetainfoContainer() {
		this.metainfo = new ArrayList<Metainfo>();
	}

	public void addMetainfo(Metainfo info) {
		this.metainfo.add(info);
	}

	public void removeMetainfo(Metainfo info) {
		this.metainfo.remove(info);
	}

	@Override
	public Iterator<Metainfo> iterator() {
		return metainfo.iterator();
	}

	public Metainfo get(int index) {
		return index < metainfo.size() ? metainfo.get(index) : null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Metainfo m : metainfo) {
			sb.append(m.toString());
		}
		sb.append(" - " + metainfo.size() + " metainfo records");
		return sb.toString();
	}

	public static String toJSON(MetainfoContainer container) {
		return Json.toJson(container.metainfo).toString();
	}

	public static MetainfoContainer fromJSON(String json) {
		try {
			List<Metainfo> metainfo = mapper.readValue(json, new TypeReference<List<Metainfo>>() { });
			return new MetainfoContainer(metainfo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}