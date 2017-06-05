package com.dabi.habitv.plugin.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.google.common.base.Joiner;
import com.sun.mail.pop3.POP3SSLStore;

public class Pop3MessageReceiver implements MessageReceiver {

	private final Logger logguer = Logger.getLogger(this.getClass().getName());

	private String host;
	private String port;
	private String username;
	private String password;

	private Session session = null;
	private Store store = null;
	private Folder folder;

	public Pop3MessageReceiver(String host, String port, String username, String password) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public void connect() throws MessagingException {

		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		Properties pop3Props = new Properties();

		pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
		pop3Props.setProperty("mail.pop3.port", port);
		pop3Props.setProperty("mail.pop3.socketFactory.port", port);

		URLName url = new URLName("pop3", host, Integer.valueOf(port), "", username, password);

		session = Session.getInstance(pop3Props, null);
		store = new POP3SSLStore(session, url);
		store.connect();
	}

	public void openFolder(String folderName) throws MessagingException {

		// Open the Folder
		folder = store.getDefaultFolder();

		folder = folder.getFolder(folderName);

		if (folder == null) {
			throw new TechnicalException("Invalid folder");
		}

		folder.open(Folder.READ_WRITE);
	}

	public void closeFolder() throws MessagingException {
		folder.close(false);
	}

	public int getMessageCount() throws MessagingException {
		return folder.getMessageCount();
	}

	public int getNewMessageCount() throws MessagingException {
		return folder.getNewMessageCount();
	}

	public void disconnect() throws MessagingException {
		store.close();
	}

	public List<Map<String, String>> getAllMessages() {
		try {
			connect();
			openFolder("INBOX");
			return findAllMessages();
		} catch (MessagingException | IOException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				closeFolder();
				disconnect();
			} catch (MessagingException e) {
			}
		}
	}

	private List<Map<String, String>> findAllMessages() throws MessagingException, IOException {
		List<Map<String, String>> messages = new ArrayList<>();

		// Attributes & Flags for all messages ..
		Message[] msgs = folder.getMessages();

		// Use a suitable FetchProfile
		FetchProfile fp = new FetchProfile();
		fp.add(FetchProfile.Item.ENVELOPE);
		folder.fetch(msgs, fp);

		for (int i = 0; i < msgs.length; i++) {
			logguer.debug("--------------------------");
			logguer.debug("MESSAGE #" + (i + 1) + ":");
			messages.add(dumpPart(msgs[i]));
		}
		return messages;
	}

	private Map<String, String> dumpPart(Part p) throws MessagingException, IOException {
		Map<String, String> message = new HashMap<>();
		if (p instanceof Message) {
			dumpEnvelope((Message) p, message);
		}

		String ct = p.getContentType();
		try {
			String contentType = (new ContentType(ct)).toString();
			logguer.debug("CONTENT-TYPE: " + contentType);
			message.put(EmailConf.CONTENT_TYPE, contentType);
		} catch (ParseException pex) {
			logguer.debug("BAD CONTENT-TYPE: " + ct);
		}

		/*
		 * Using isMimeType to determine the content type avoids fetching the
		 * actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			logguer.debug("This is plain text");
			logguer.debug("---------------------------");
			String content = (String) p.getContent();
			logguer.debug(content);
			message.put(EmailConf.CONTENT, content);
		} else {

			// just a separator
			logguer.debug("---------------------------");
			message.put(EmailConf.CONTENT, EmailUtils.readMultipart((Multipart) p.getContent()));
		}

		// if (p instanceof Message) {
		// ((Message) p).setFlag(Flag.SEEN, true);
		// }

		return message;
	}

	private void dumpEnvelope(Message m, Map<String, String> message) throws MessagingException {
		logguer.debug(" ");
		Address[] a;
		// FROM
		if ((a = m.getFrom()) != null) {
			for (int j = 0; j < a.length; j++) {
				logguer.debug("FROM: " + a[j].toString());
			}
			message.put(EmailConf.FROM, Joiner.on(",").join(a));
		}

		// TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++) {
				logguer.debug("TO: " + a[j].toString());
			}
			message.put(EmailConf.TO, Joiner.on(",").join(a));
		}

		// SUBJECT
		logguer.debug("SUBJECT: " + m.getSubject());
		message.put(EmailConf.SUBJECT, m.getSubject());

		// DATE
		Date d = m.getSentDate();
		String dateStr = d != null ? d.toString() : "UNKNOWN";
		logguer.debug("SendDate: " + dateStr);
		message.put(EmailConf.DATE, dateStr);

	}

}
