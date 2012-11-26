package controllers;

import java.io.File;
import java.util.Map;

import org.backmeup.plugins.actions.indexing.IndexProperties;
import org.backmeup.search.ESSearchClient;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;

public class Thumbnails extends Controller {
	
	public static Result getThumbnail(Long userId, String fileId) {
		User user = User.find.byId(userId);
		
		ESSearchClient client = new ESSearchClient();
		Map<String, Object> item = client.getItemByFileKey(user, fileId);
		
		if (item == null)
			return notFound();
		  
		Object thumbnailPath = item.get(IndexProperties.FIELD_THUMBNAIL_PATH);
		if (thumbnailPath == null)
			return notFound();
		
		return ok(new File(thumbnailPath.toString()));
	}

}
