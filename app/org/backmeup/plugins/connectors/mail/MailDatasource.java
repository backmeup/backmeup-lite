package org.backmeup.plugins.connectors.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

import models.Backup;
import models.DatastoreProfile;

import org.backmeup.api.Progressable;
import org.backmeup.api.connectors.Datasource;
import org.backmeup.api.connectors.DatasourceException;
import org.backmeup.api.exceptions.PluginException;
import org.backmeup.api.storage.Storage;
import org.backmeup.api.storage.StorageException;
import org.backmeup.api.storage.metadata.Metainfo;
import org.backmeup.api.storage.metadata.MetainfoContainer;

import play.Logger;

/** 
 * @author fschoeppl
 */
public class MailDatasource extends Datasource {
	
	private static final String MESSAGE_HTML_ATTACHMENT_ENTRY = "<a class=\"bmu-attachment\" href=\"{0}\">{1}</a>";
	private static final String MESSAGE_HTML_ATTACHMENT_WRAP = "<h1>Attachments:</h1>\n<div>{0}</div>";
	private static final String MESSAGE_HTML_WRAP=
			"<!DOCTYPE html>" +
	"<html>"+ 
	  "<head>" +
	    "<meta http-equiv=\"content-type\" content=\"text/html; charset={8}\">" +
	    "<title>{2}</title>" +
	    "<style type=\"text/css\">\n" +
	      ".bmu-message-details '{ background-color: #FFFF00 }' \n" +
	      ".bmu-attachment '{display:block;clear:both;}'\n" +
	    "</style>" +
	    "{0}" +
	  "</head>" +
	  "<body>" +
	    "<div class=\"bmu-message-details\">" +
	      "<h1>Details:</h1>" +
	      "<p>From: {3}</p>" +
	      "<p>To: {6}</p>" +
	      "<p>Subject: {2}</p>" +
	      "<p>Sent at: {4}</p>" +
	      "<p>Received at: {5}</p>" +
	    "</div>" +
	    "<h1>Original Message:</h1>" +
	    "{1}" +
	    "{7}" +
	  "</body>" +
	"</html>";
	
	private static final String INDEX_HTML_ENTRY = "<tr><td>{1}</td><td>{3}</td><td><a href=\"{5}\">{0}</a></td><td>{4}</td><td>{2}</td></tr>";
	private static final String INDEX_HTML_WRAP =
			"<!DOCTYPE html>" +
	"<html>" +
	  "<head>" +
	    "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">" +
	    "<title>Your e-mail backup</title>" +
	    "<style type=\"text/css\">\n" +
	      ".bmu-message-details '{ background-color: #FFFF00 }'\n" +
	      ".bmu-attachment '{display:block;clear:both;}'\n" +
	    "</style>" +
	  "</head>" +
	  "<body>" +
	    "<h1>Index:</h1>" +
	    "<table>" +
	      "<tr><th>From</th><th>Received at</th><th>Subject</th><th>To</th><th>Sent at</th></tr>" +
	      "{0}" +
	    "</table>" +
	  "</body>" +
	"</html>";
 
 private static class Content {    
   public String contentId;
   public String filename;
   public InputStream content;     
 }
 
 private static class MessageInfo {
   private String fileName;
   private String subject;
   private String from;
   private String to;
   private String sentAt;
   private Date receivedAt;
   
   
   public MessageInfo(String fileName, String subject, String from, String to,
       String sentAt, Date receivedAt) {
     this.fileName = fileName;
     this.subject = subject;
     this.from = from;
     this.to = to;
     this.sentAt = sentAt;
     this.receivedAt = receivedAt;
   }
   
   public String getFileName() {
     return fileName;
   }
   public void setFileName(String fileName) {
     this.fileName = fileName;
   }
   public String getSubject() {
     return subject;
   }
   public void setSubject(String subject) {
     this.subject = subject;
   }
   public String getFrom() {
     return from;
   }
   public void setFrom(String from) {
     this.from = from;
   }
   public String getTo() {
     return to;
   }
   public void setTo(String to) {
     this.to = to;
   }
   public String getSentAt() {
     return sentAt;
   }
   public void setSentAt(String sentAt) {
     this.sentAt = sentAt;
   }
   public Date getReceivedAt() {
     return receivedAt;
   }
   public void setReceivedAt(Date receivedAt) {
     this.receivedAt = receivedAt;
   }       
 }

 private SimpleDateFormat folderFormat;  
 
 // private static final String MESSAGE_FOLDER_FORMAT = "org.backmeup.mail.MailDatasource.MESSAGE_FOLDER_FORMAT";
 // private static final String MESSAGE_HTML_WRAP = "org.backmeup.mail.MailDatasource.MESSAGE_HTML_WRAP";
 // private static final String MESSAGE_HTML_ATTACHMENT_ENTRY = "org.backmeup.mail.MailDatasource.MESSAGE_HTML_ATTACHMENT_ENTRY";
 // private static final String MESSAGE_HTML_ATTACHMENT_WRAP = "org.backmeup.mail.MailDatasource.MESSAGE_HTML_ATTACHMENT_WRAP";
 // private static final String INDEX_HTML_WRAP = "org.backmeup.mail.MailDatasource.INDEX_HTML_WRAP";
 // private static final String INDEX_HTML_ENTRY = "org.backmeup.mail.MailDatasource.INDEX_HTML_ENTRY";
 
 /*
 private ResourceBundle textBundle = ResourceBundle
     .getBundle(MailDatasource.class.getSimpleName());
 */
 
 private Pattern bodyRegex = Pattern.compile("<body.*?>(.*?)</body>", Pattern.DOTALL);
 private Pattern headRegex = Pattern.compile("<head.*?>(.*?)</head>", Pattern.DOTALL);
 private Pattern htmlRegex = Pattern.compile("<html.*?>(.*?)</html>", Pattern.DOTALL);
 
 public MailDatasource(DatastoreProfile profile, Backup backup) {
	 super(profile, backup);
   this.folderFormat = new SimpleDateFormat("yyyy/MM/dd");
 }

 private String getCharset(Part bp) throws MessagingException {
   Pattern pattern = Pattern.compile(".*?charset=(.*?)$");
   Matcher matcher = pattern.matcher(bp.getContentType());
   if (matcher.find()) {
     String result = matcher.group(1).toUpperCase();
     if (result.contains(";"))
       result = result.split(";")[0];
     return result;
   }
   return "UTF-8";
 }
 
 private static class Attachment {
   public InputStream stream;
   public String filename;
 }

 private static class TextContent {
   public String text;
   public boolean isHtml;
   public String charset;
 }

 private TextContent getText(Part part) throws MessagingException, IOException {
   String charset = getCharset(part);
   if (part.isMimeType("text/*")) {
     Object o = part.getContent();
     TextContent tc = new TextContent();
     if (o instanceof String) {        
       tc.isHtml = part.isMimeType("text/html");
       tc.charset = charset;
       tc.text = (String) o;
       return tc;
     } else if (o instanceof InputStream) {
       InputStream i = (InputStream) o;
       try {          
         tc.text = new Scanner(i, getCharset(part)).useDelimiter("\\A").next();
         tc.charset = charset;
         tc.isHtml = part.isMimeType("text/html");
         return tc;
       } catch (NoSuchElementException nee) {
         return null;
       }
     }      
   } else if (part.isMimeType("multipart/alternative")) {
     Multipart mp = (Multipart) part.getContent();
     TextContent text = new TextContent();
     for (int i = 0; i < mp.getCount(); i++) {
       Part bp = mp.getBodyPart(i);
       if (bp.isMimeType("text/plain")) {
         if (text == null)
           text = getText(bp);
         continue;
       } else if (bp.isMimeType("text/html")) {
         TextContent s = getText(bp);
         if (s != null)
           return s;
       } else {
         return getText(bp);
       }
     }
     return text;
   } else if (part.isMimeType("multipart/*")) {
     Multipart mp = (Multipart) part.getContent();
     for (int i = 0; i < mp.getCount(); i++) {
       TextContent s = getText(mp.getBodyPart(i));
       if (s != null)
         return s;
     }
   } 
   return null;
 }

 public List<Attachment> getAttachments(Part part) throws MessagingException,
     IOException {
   List<Attachment> attachments = new ArrayList<Attachment>();
   if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
     Attachment a = new Attachment();
     a.filename = MimeUtility.decodeText(part.getFileName());
     a.stream = part.getInputStream();
     attachments.add(a);
   } else if (part.isMimeType("multipart/*")) {
     Multipart mp = (Multipart) part.getContent();
     for (int i = 0; i < mp.getCount(); i++) {
       attachments.addAll(getAttachments(mp.getBodyPart(i)));
     }
   }
   return attachments;
 }

 public List<Part> getNestedMessages(Part p) throws MessagingException,
     IOException {
   List<Part> nested = new ArrayList<Part>();
   if (p.isMimeType("multipart/*")) {
     Multipart mp = (Multipart) p.getContent();
     int count = mp.getCount();
     for (int i = 0; i < count; i++)
       nested.addAll(getNestedMessages(mp.getBodyPart(i)));
   } else if (p.isMimeType("message/rfc822")) {
     nested.add(p);
   }
   return nested;
 }  
 
 private String join(Object[] arr, String pattern) {
   if (arr == null)
     return "";
   
   StringBuilder sb = new StringBuilder();
   for (int i=0; i < arr.length; i++) {
     sb.append(arr[i]).append(pattern);      
   }
   
   if (arr.length > 0) {
     sb.delete(sb.length() - pattern.length(), sb.length());
   }
   return sb.toString();
 }
   
 private void handlePart(Part m, String folderName, Storage storage, Set<String> alreadyInspected, List<MessageInfo> indexDetails) 
		 throws StorageException, MessagingException, IOException {
   
   String from="N/A";
   String to="N/A";
   String subject="N/A";
   String sentAt="N/A";
   Date receivedAt=new Date();
   int msgNmbr = 0;
   
   if (m instanceof Message) {
     Message mesg = (Message)m;
     if (mesg.getFrom() != null) 
       from = join(mesg.getFrom(), ", ");        
     
     if (mesg.getRecipients(Message.RecipientType.TO) != null)
       to = join(mesg.getAllRecipients(), ", ");
     msgNmbr = mesg.getMessageNumber();  
     subject = mesg.getSubject();
     sentAt = mesg.getSentDate().toString();
     receivedAt = mesg.getReceivedDate();           
   }
   
   String destinationFileName = folderName + "content" + msgNmbr + ".html";
   
   if (alreadyInspected.contains(destinationFileName))
     return;
   
   TextContent text = getText(m);
   // nothing to do; message is empty
   if (text == null)
     return;
   
   String appendToHead = "";
   String appendToBody = "";
   
   if (text.isHtml) {
     Matcher matcher = headRegex.matcher(text.text);
     if (matcher.find()) {
       appendToHead = matcher.group(1);
     } 
     matcher = bodyRegex.matcher(text.text);
     if (matcher.find()) {
       appendToBody = matcher.group(1);
     } else {        
       matcher = htmlRegex.matcher(text.text);
       if (matcher.find()) {
         appendToBody = matcher.group(1);
       } else {
         Logger.warn("Couldn't find html element / falling back to content of string");
         appendToBody = text.text;
       }
     }
   }
   
   if (!text.isHtml) {
     appendToBody = text.text;    
   }
   
   String attachmentFolder = folderName + "attachments" + msgNmbr + "/";
   List<Attachment> attachments = getAttachments(m);
   StringBuilder attachmentLinks = new StringBuilder();
   
   for (Attachment a : attachments) {
     attachmentLinks.append(MessageFormat.format(MESSAGE_HTML_ATTACHMENT_ENTRY, "attachments" + msgNmbr + "/" + a.filename, a.filename));
     Logger.info("Downloading attachment " + a.filename);
     storage.addFile(a.stream, attachmentFolder + a.filename, new MetainfoContainer());
     Logger.info("Done.");
   }
   
   // get embedded images
   List<Content> contentIds = getContentIds(m);
   for (Content c : contentIds) {
	   Logger.info("Downloading embedded resources " + c.filename);
     appendToBody = appendToBody.replace("cid:" + c.contentId, "attachments" + msgNmbr + "/" + c.filename);
     storage.addFile(c.content, attachmentFolder + c.filename, new MetainfoContainer());
     Logger.info("Done.");
   }
   
   String attachmentString = attachmentLinks.length() == 0 ? "" : MessageFormat.format(MESSAGE_HTML_ATTACHMENT_WRAP, attachmentLinks.toString());
   
   String htmlText = MessageFormat.format(MESSAGE_HTML_WRAP, 
       appendToHead,
       appendToBody,
       subject,
       from,
       sentAt,
       receivedAt,
       to,
       attachmentString,
       text.charset
   );
   
   alreadyInspected.add(destinationFileName);
   indexDetails.add(new MessageInfo(destinationFileName, subject, from, to, sentAt, receivedAt));
   MetainfoContainer infos = new MetainfoContainer();
   Metainfo metaData = new Metainfo();
   metaData.setBackupedAt(new Date().getTime());    
   metaData.setDestination(destinationFileName);
   metaData.setAttribute("from", from);
   metaData.setAttribute("to", to);
   metaData.setAttribute("receivedAt", receivedAt.toString());
   metaData.setAttribute("sentAt", sentAt);
   metaData.setAttribute("subject", subject);
   infos.addMetainfo(metaData);
   storage
       .addFile(
           new ByteArrayInputStream(htmlText.toString().getBytes(
               text.charset)), destinationFileName, infos);
   
   // handle nested messages
   List<Part> nested = getNestedMessages(m);
   for (int i=0; i < nested.size(); i++) {
     handlePart(nested.get(i), folderName + "/nested/", storage, alreadyInspected, indexDetails);
   }
 }

 private List<Content> getContentIds(Part m) throws MessagingException, IOException {
   List<Content> contentIds = new ArrayList<Content>();
   Stack<Part> parts = new Stack<Part>();
   parts.push(m);
   while (!parts.empty()) {
     Part current = parts.pop();
     
     // analyze current part
     String[] header = current.getHeader("Content-ID");
     if (header != null && header.length > 0) {
       Content c = new Content();
       c.contentId = header[0];
       if (c.contentId.startsWith("<"))
         c.contentId = c.contentId.substring(1);
       if (c.contentId.endsWith(">"))
         c.contentId = c.contentId.substring(0, c.contentId.length() - 1);
       c.filename = current.getFileName();
       c.content = (InputStream) current.getDataHandler().getContent();
       contentIds.add(c);
     }
     
     // push children on stack
     if (current.isMimeType("multipart/*")) {
       Multipart mp = (Multipart) current.getContent();
       int count = mp.getCount();
       for (int i = 0; i < count; i++) {
         parts.push(mp.getBodyPart(i));
       }
     }
   }
   return contentIds;
 }

 private void handleFolder(Folder folder, Storage storage, Set<String> alreadyInspected, List<MessageInfo> indexDetails, int retryCount)
     throws IOException, MessagingException, StorageException {
   try {
     folder.open(Folder.READ_ONLY);
     
     Message[] messages = folder.getMessages();
     Logger.info("Folder: " + folder.getFullName());      
     double prev = 0;
     for (int i=0; i < messages.length; i++) {      
       String folderName = folder.getFullName() + "/"
           + folderFormat.format(messages[i].getReceivedDate()) + "/";
       
       handlePart(messages[i], folderName, storage, alreadyInspected, indexDetails);
       double percent = i * 100 / (double)messages.length;
       if (percent - 10 > prev) {         
    	   Logger.info(String.format("%3.2f%%", percent));          
         prev = percent;
       }
     }
     folder.close(false);
   } catch (FolderClosedException fce) {
     Logger.warn("Retrying folder " + folder, fce);
     if (retryCount < 10) {        
       handleFolder(folder, storage, alreadyInspected, indexDetails, retryCount + 1);
     } else {
       throw new PluginException(MailDescriptor.MAIL_ID, "Failed to download folder", fce);
     }
   } catch (MessagingException me) {
	   Logger.info(me.getMessage(), me);    
   } 
 }

 public void handleDownloadAll(Folder current, Properties accessData,
     Storage storage, Set<String> alreadyInspected, List<MessageInfo> indexDetails) throws IOException, MessagingException,
     StorageException {
   if (alreadyInspected.contains(current.getFullName()))
     return;
   int retryCount = 0;
   handleFolder(current, storage, alreadyInspected, indexDetails, retryCount);
   alreadyInspected.add(current.getFullName());

   Folder[] subFolders = current.list("*");
   for (Folder sub : subFolders) {
     handleDownloadAll(sub, accessData, storage, alreadyInspected, indexDetails);
   }
 }
 
 private void generateIndex(Storage storage, List<MessageInfo> indexDetails) throws UnsupportedEncodingException, StorageException {
   StringBuilder sb = new StringBuilder();
   Collections.sort(indexDetails, new Comparator<MessageInfo>() {
     @Override
     public int compare(MessageInfo o1, MessageInfo o2) {
       return o2.getReceivedAt().compareTo(o1.getReceivedAt());
     }
   });
   
   for (MessageInfo mi : indexDetails) {
     sb.append(MessageFormat.format(INDEX_HTML_ENTRY, mi.getSubject(), mi.getFrom(), mi.getSentAt(), mi.getReceivedAt(), mi.getTo(), mi.getFileName()));
   }
   String indexHtml = MessageFormat.format(INDEX_HTML_WRAP, sb.toString());
   
   storage.addFile(new ByteArrayInputStream(indexHtml.getBytes("UTF-8")), "index.html", new MetainfoContainer());
 }

 @Override
 public void downloadAll(Properties accessData, List<String> options, Storage storage,
     Progressable progressor) throws DatasourceException, StorageException {
	 
	 accessData.putAll(profile.getProperties());
   try {
     Session session = Session.getInstance(accessData);
     for (Entry<Object, Object> entry : accessData.entrySet()) {
    	 System.out.println(entry.getKey() + "=" + entry.getValue());
     }
     Store store = session.getStore();
     
     Logger.info("Connecting to mail provider " + accessData.getProperty("mail.host"));
     store.connect(accessData.getProperty("mail.host"),
         accessData.getProperty("mail.user"),
         accessData.getProperty("mail.password"));
     Set<String> alreadyInspected = new HashSet<String>();
     Logger.info("Connected! Downloading folders...");
     Folder[] folders = store.getDefaultFolder().list("*");
     List<MessageInfo> indexDetails = new ArrayList<MessageInfo>();
     if (options.size() > 0) {        
       List<Folder> toVisit = new ArrayList<Folder>();
       for (Folder f : folders) {
         if (options.contains(f.getFullName())) {
           toVisit.add(f);
         }
       }
       folders = toVisit.toArray(new Folder[]{});
     }      
     for (Folder folder : folders) {
       handleDownloadAll(folder, accessData, storage, alreadyInspected, indexDetails);
     }
     Logger.info("Download completed; creating index...");
     // generate index based on message info structs
     generateIndex(storage, indexDetails);
     store.close();
   } catch (NoSuchProviderException e) {
	   Logger.error(e.getMessage(), e);
     throw new PluginException(MailDescriptor.MAIL_ID, "No such provider", e);
   } catch (MessagingException e) {
	   Logger.error(e.getMessage(), e);
     throw new PluginException(MailDescriptor.MAIL_ID, "An error occured during the backup", e);
   } catch (IOException e) {
	   Logger.error(e.getMessage(), e);
     throw new PluginException(MailDescriptor.MAIL_ID, "An error occured during the backup", e);
   }
 }

 /*
 @Override
 public List<String> getAvailableOptions(Properties accessData) {
   List<String> availOpts = new ArrayList<String>();
   try {
     Session session = Session.getInstance(accessData);
     Store store = session.getStore();
     store.connect(accessData.getProperty("mail.host"),
         accessData.getProperty("mail.user"),
         accessData.getProperty("mail.password"));
     Folder[] folders = store.getDefaultFolder().list("*");
     for (Folder folder : folders) {
       String folderName = folder.getFullName();
       availOpts.add(folderName);
     }
     
     Collections.sort(availOpts, new Comparator<String>() {
       @Override
       public int compare(String o1, String o2) {
         return o1.compareTo(o2);
       }
     });
     
     store.close();
   } catch (NoSuchProviderException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
   } catch (MessagingException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
   }
   return availOpts;
 }
 */
}
