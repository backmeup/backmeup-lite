package org.backmeup.plugin.api.storage;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.backmeup.api.storage.metadata.Metainfo;
import org.backmeup.api.storage.metadata.MetainfoContainer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

public class MetainfoTest {
	
	private static String TEST_JSON =
			"[{\"id\":null,\"type\":null,\"modified\":0,\"destination\":null," +
	          "\"source\":null,\"backupedAt\":1352905021502,\"parent\":null}," +
			 "{\"id\":null,\"type\":null,\"modified\":0,\"destination\":\"destination\"," +
	          "\"source\":null,\"backupedAt\":0,\"parent\":\"myParent\"}]";
		
		private static MetainfoContainer container = new MetainfoContainer();
		
		static {
			Metainfo meta01 = new Metainfo();
			meta01.setBackupedAt(new Date().getTime());
			meta01.setAttribute("foo", "bar");			
			container.addMetainfo(meta01);
			
			Metainfo meta02 = new Metainfo();
			meta02.setDestination("destination");
			meta02.setParent("myParent");
			container.addMetainfo(meta02);
		}
		
		@Test
		public void testToJSON() {
			String json = MetainfoContainer.toJSON(container);
			System.out.println("JSON: " + json);
		}
		
		@Test
		public void testFromJSON() throws JsonParseException, JsonMappingException, IOException {
			ObjectMapper mapper = new ObjectMapper();
			List<Metainfo> metainfo = mapper.readValue(TEST_JSON, new TypeReference<List<Metainfo>>() { });
			for (Metainfo m : metainfo) {
				System.out.println(m.getBackupedAt());
				System.out.println(m.getParent());
			}
		}
	
}
