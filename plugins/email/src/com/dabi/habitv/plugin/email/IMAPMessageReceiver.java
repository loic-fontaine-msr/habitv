package com.dabi.habitv.plugin.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.google.common.base.Joiner;
import com.sun.mail.imap.IMAPFolder;

public class IMAPMessageReceiver implements MessageReceiver {

	private String host;
	private String username;
	private String password;

	public IMAPMessageReceiver(String host, String username, String password) {
		super();
		this.host = host;
		this.username = username;
		this.password = password;
	}

	@Override
	public List<Map<String, String>> getAllMessages() {
		IMAPFolder folder = null;
		Store store = null;
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);

			store = session.getStore("imaps");
			store.connect(host, username, password);

			folder = (IMAPFolder) store.getFolder("inbox"); // This works for
			                                                // both email
			                                                // account

			if (!folder.isOpen()) {
				folder.open(Folder.READ_WRITE);
			}
			Message[] messages = folder.getMessages();
			List<Map<String, String>> messageList = new ArrayList<>();
			for (int i = 0; i < messages.length; i++) {
				Map<String, String> message = new HashMap<>();
				Message msg = messages[i];
				message.put(EmailConf.SUBJECT, msg.getSubject());
				message.put(EmailConf.FROM, Joiner.on(",").join(msg.getFrom()));
				message.put(EmailConf.TO, Joiner.on(",").join(msg.getAllRecipients()));
				message.put(EmailConf.DATE, msg.getSentDate().toString());

				if (msg.isMimeType("text/plain")) {
					message.put(EmailConf.CONTENT, (String) msg.getContent());
				} else {
					message.put(EmailConf.CONTENT, EmailUtils.readMultipart((Multipart) msg.getContent()));
				}

				messageList.add(message);
			}
			return messageList;
		} catch (MessagingException | IOException e) {
			throw new TechnicalException(e);
		} finally {
			if (folder != null && folder.isOpen()) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
				}
			}
		}
	}

}
