package org.backmeup.plugins.actions.thumbnail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import models.Backup;

import org.backmeup.api.Progressable;
import org.backmeup.api.actions.Action;
import org.backmeup.api.actions.ActionException;
import org.backmeup.api.storage.DataObject;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.metadata.Metainfo;
import org.backmeup.api.storage.metadata.MetainfoContainer;
import org.backmeup.plugins.actions.indexing.IndexProperties;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import play.Logger;

public class ThumbnailAction implements Action {
	        
	// TODO make configurable!
	private static File THUMBNAIL_DIR = new File("thumbnails"); 
	private static Integer THUMBNAIL_DIMENSIONS = Integer.valueOf(120);
	
	static {
		if (!THUMBNAIL_DIR.exists())
			THUMBNAIL_DIR.mkdirs();
	}
	
	/**
	 * The GraphicsMagick command we need to emulate is this:
	 * 
	 * gm convert -size 120x120 original.jpg -resize 120x120 +profile "*" thumbnail.jpg
	 * 
	 * @return the name of the thumbnail file
	 * @throws IM4JavaException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private String convert(File original) throws IOException, 
		InterruptedException, IM4JavaException {
		
		String thumbnailPath = original.getAbsolutePath() + "_thumb.jpg";
		
		IMOperation op = new IMOperation();
		op.size(THUMBNAIL_DIMENSIONS, THUMBNAIL_DIMENSIONS);
		op.quality(80.0);
		op.resize(THUMBNAIL_DIMENSIONS, THUMBNAIL_DIMENSIONS);
		op.p_profile("*");
		op.addImage(original.getAbsolutePath() + "[0]");
		op.addImage(thumbnailPath);
		new ConvertCmd(true).run(op);
		
		return thumbnailPath;
	}
	
	@Override
	public void doAction(Properties parameters, Storage storage, Backup backup, Progressable progressor)
			throws ActionException {
		
		progressor.progress("Starting thumbnail rendering");
		
		File thumbnailDir = new File(THUMBNAIL_DIR, "backup-" + backup.id);
		if (!thumbnailDir.exists())
			thumbnailDir.mkdir();
		
		try {
			Iterator<DataObject> dobs = storage.getDataObjects();
			while (dobs.hasNext()) {
				DataObject dataobject = dobs.next();
				progressor.progress("Processing " + dataobject.getPath());
				
				// Write file to temp dir
				String tempFilename = dataobject.getPath();
				if (tempFilename.startsWith("/"))
					tempFilename = tempFilename.substring(1);
				
				tempFilename = tempFilename.replace("/", "$").replace(" ", "_").replace("#", "_");
				File tempFile = new File(thumbnailDir, tempFilename);
				FileOutputStream fos = new FileOutputStream(tempFile);
				fos.write(dataobject.getBytes());
				fos.close();
				
				try {
					// Generate thumbnails using GraphicsMagick
					String thumbPath = convert(tempFile);
					
					// Store thumbnail path
					MetainfoContainer container = dataobject.getMetainfoContainer();
					Metainfo meta = new Metainfo();
					meta.setAttribute(IndexProperties.FIELD_THUMBNAIL_PATH, thumbPath);
					container.addMetainfo(meta);
					dataobject.setMetainfoContainer(container);
				} catch (Throwable t) {
					Logger.warn("Failed to render thumbnail for: " + dataobject.getPath());
					Logger.warn(t.getClass().getName() + ": " + t.getMessage());
				}
				
				// Remove temporary file
				tempFile.delete();
			}
		} catch (Exception e) {
			throw new ActionException(e);
		}
		
		progressor.progress("Thumbnail rendering complete");
	}

}