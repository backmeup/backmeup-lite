package org.backmeup.plugins.connectors.moodle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.backmeup.api.RequiredInputField;
import org.backmeup.api.RequiredInputField.InputFieldType;
import org.backmeup.api.auth.InputBased;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MoodleAuthenticator implements InputBased {

	@Override
	public AuthorizationType getAuthType() {
		return AuthorizationType.INPUT_BASED;
	}

	@Override
	public String postAuthorize(Properties inputProperties) {
	  // TODO return the username of the moodle account
	  return inputProperties.getProperty("Username");
	}

	@Override
	public List<RequiredInputField> getRequiredInputFields() {
		List<RequiredInputField> requiredFields = new LinkedList<RequiredInputField>();
		
		requiredFields.add(new RequiredInputField ("Username", "Username", "Username", true, InputFieldType.STRING));
		requiredFields.add(new RequiredInputField ("Password", "Password", "Password", true, InputFieldType.PASSWORD));
		requiredFields.add(new RequiredInputField ("MoodleServerUrl", "Moodle Server Url", "Moodle Server Url", true, InputFieldType.STRING));

		return requiredFields;
	}

	@Override
	public Map<String, InputFieldType> getTypeMapping() {
		Map<String, InputFieldType> mapping = new TreeMap<String, InputFieldType>();
		mapping.put("Username", InputFieldType.STRING);
		mapping.put("Password", InputFieldType.PASSWORD);
		mapping.put("Moodle Server Url", InputFieldType.STRING);

		return mapping;
	}

	@Override
	public boolean isValid(Properties inputProperties) {
		String serverurl = inputProperties.getProperty("Moodle Server Url");
		String username = inputProperties.getProperty("Username");
		String password = inputProperties.getProperty("Password");
		
		serverurl = serverurl.endsWith("/") ? serverurl : serverurl+"/";
		
		try {
			String authUrl = serverurl
					+ "blocks/backmeup/service.php?username=" + username
					+ "&password=" + password + "&action=auth";
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(authUrl);
			NodeList nodes = doc.getElementsByTagName("result");
			Element result = (Element) nodes.item(0);
			if (result.getTextContent().compareTo("true") == 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

}
