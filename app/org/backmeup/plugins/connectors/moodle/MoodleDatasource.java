package org.backmeup.plugins.connectors.moodle;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.connectors.FilesystemLikeDatasource;
import org.backmeup.api.connectors.FilesystemURI;
import org.backmeup.api.exceptions.PluginException;
import org.backmeup.api.storage.metadata.Metainfo;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * This class processes the xml file generated by the server side moodle plugin
 * and downloads the content
 * 
 * @author florianjungwirth
 * 
 */
public class MoodleDatasource extends FilesystemLikeDatasource {

	public MoodleDatasource(DatastoreProfile profile, Backup backup) {
		super(profile, backup);
	}

	@Override
	public InputStream getFile(Properties items, List<String> options, FilesystemURI uri) {
		try {
			return uri.getUri().toURL().openStream();
		} catch (Exception e) {
			throw new PluginException(MoodleDescriptor.MOODLE_ID, String.format("Download failed: \"%s\"", e.getMessage()), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FilesystemURI> list(Properties items, List<String> options, FilesystemURI uri) {
		options = new ArrayList<String>();
		options.add("Wiki");
		options.add("Url");
		options.add("Page");
		options.add("Assignment");
		options.add("Resource");
		options.add("Folder");
		
		List<FilesystemURI> results = new ArrayList<FilesystemURI>();

		for (Entry<Object, Object> entry : items.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		
		String serverurl = items.getProperty("MoodleServerUrl");
		String username = items.getProperty("Username");
		String password = items.getProperty("Password");

		serverurl = serverurl.endsWith("/") ? serverurl : serverurl + "/";
		
		String httpOptions = "";
		Iterator<String> optionsIterator = options.iterator();
		while(optionsIterator.hasNext()) {
			httpOptions = httpOptions + optionsIterator.next();
			if(optionsIterator.hasNext())
				httpOptions = httpOptions + ",";
		}
		
		try {
			String authUrl = serverurl
					+ "blocks/backmeup/service.php?username=" + username
					+ "&password=" + password + "&action=list";

			if(!options.isEmpty()) {
				authUrl = authUrl + "&options=" + httpOptions;
			}
			// System.out.println(authUrl);
			Document doc = new SAXBuilder().build(authUrl);

			List<Element> courses = doc.getRootElement().getChild("courses")
					.getChildren("course");

			Iterator<Element> courseIterator = courses.iterator();
			while (courseIterator.hasNext()) {
				Element course = courseIterator.next();
				List<Element> sections = course.getChildren("section");
				Iterator<Element> sectionIterator = sections.iterator();

				Metainfo courseMeta = new Metainfo();
				courseMeta.setId(course.getAttributeValue("id"));
				courseMeta.setAttribute("name",
						course.getAttributeValue("name"));
				courseMeta.setType("course");
				courseMeta.setBackupedAt(new Date().getTime());

				while (sectionIterator.hasNext()) {
					Element section = sectionIterator.next();
					List<Element> sequences = section.getChildren("sequence");
					Iterator<Element> sequenceIterator = sequences.iterator();

					Metainfo sectionMeta = new Metainfo();
					sectionMeta.setParent(courseMeta.getId());
					sectionMeta.setId(courseMeta.getId() + "_"
							+ section.getAttributeValue("id"));
					sectionMeta.setType("section");
					sectionMeta.setAttribute("name",
							section.getAttributeValue("name"));
					if (section.getAttributeValue("summary").length() > 0)
						sectionMeta.setAttribute("summary",
								section.getAttributeValue("summary"));

					while (sequenceIterator.hasNext()) {
						Element sequence = sequenceIterator.next();
						
						if(!options.contains(sequence.getChildText("type")))
							continue;
						
						Metainfo sequenceMeta = new Metainfo();
						sequenceMeta.setParent(sectionMeta.getId());
						sequenceMeta.setId(sectionMeta.getId() + "_"
								+ sequence.getAttributeValue("id"));
						sequenceMeta.setAttribute("name",
								sequence.getChildText("name"));
						sequenceMeta.setAttribute(
								"intro",
								sequence.getChildText("intro").replaceAll(
										"\\<.*?\\>", ""));
						sequenceMeta.setType(sequence.getChildText("type"));

						Element data = sequence.getChild("data");
						if (data != null) {
							List<Element> files = data.getChildren("file");
							Iterator<Element> fileIterator = files.iterator();
							while (fileIterator.hasNext()) {
								Element file = fileIterator.next();
								String mappedPath = (file.getAttribute("path") != null) ? course
										.getAttributeValue("name")
										+ "/"
										+ file.getAttributeValue("path") + "/"
										: course.getAttributeValue("name")
												+ "/";
								String mappedUrl = java.net.URLEncoder.encode(
										mappedPath, "UTF-8")
										.replace("+", "%20")
										+ new File(file.getText()).getName();
								FilesystemURI filesystemUri = new FilesystemURI(
										new URI(file.getText()), false);
								filesystemUri.setMappedUri(new URI(mappedUrl));

								Metainfo fileMeta = new Metainfo();
								fileMeta.setParent(sequenceMeta.getId());
								
								if (file.getAttributes().size() > 0) {
									fileMeta.setModified(MoodleDatasource.parse(file.getAttributeValue("modified")).getTime());
									fileMeta.setType(file
											.getAttributeValue("mime"));
								}
								
								fileMeta.setBackupedAt(new Date().getTime());
								fileMeta.setDestination(mappedPath);
								fileMeta.setSource("moodle");
								fileMeta.setAttribute("name",
										new File(file.getText()).getName()
												.replace("%20", " "));

								filesystemUri.addMetainfo(courseMeta);
								filesystemUri.addMetainfo(sectionMeta);
								filesystemUri.addMetainfo(sequenceMeta);
								filesystemUri.addMetainfo(fileMeta);

								results.add(filesystemUri);
							}
						}
					}
				}
			}
			// add scorm files
			List<Element> scormfiles = doc.getRootElement().getChild("scorm")
					.getChildren("file");

			Iterator<Element> scormIterator = scormfiles.iterator();
			while (scormIterator.hasNext()) {
				Element file = scormIterator.next();
				FilesystemURI filesystemUri = new FilesystemURI(new URI(
						file.getText()), false);
				filesystemUri.setMappedUri(new URI(new File(file.getText())
						.getName()));
				results.add(filesystemUri);
			}
			// add theme files
			List<Element> themefiles = doc.getRootElement().getChild("theme")
					.getChildren("file");

			Iterator<Element> themeIterator = themefiles.iterator();
			while (themeIterator.hasNext()) {
				Element file = themeIterator.next();
				FilesystemURI filesystemUri = new FilesystemURI(new URI(
						file.getText()), false);
				filesystemUri.setMappedUri(new URI("theme/"
						+ new File(file.getText()).getName()));
				results.add(filesystemUri);
			}
		} catch (Exception e) {
			throw new PluginException(MoodleDescriptor.MOODLE_ID, String.format("Error while receiving file list: \"%s\"", e.getMessage()), e);
		}
		return results;
	}

	/*
	@Override
	public List<String> getAvailableOptions(Properties accessData) {
		List<String> availableOptions = new ArrayList<String>();
		availableOptions.add("Wiki");
		availableOptions.add("Url");
		availableOptions.add("Page");
		availableOptions.add("Assignment");
		availableOptions.add("Resource");
		availableOptions.add("Folder");
		return availableOptions;
	}
	*/
	
	/*
	@Override
	public String getStatistics(Properties items) {
		return null;
	}
	*/
	
	/**
	 * Used for Conversion into ISO8601 Date Format, which the Indexer needs
	 * @param input
	 * @return
	 * @throws java.text.ParseException
	 */
    private static Date parse( String input ) throws java.text.ParseException {
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
        
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }
        
        return df.parse( input );
    }
    
}
