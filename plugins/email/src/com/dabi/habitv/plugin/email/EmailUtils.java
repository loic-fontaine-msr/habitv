package com.dabi.habitv.plugin.email;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.apache.log4j.Logger;

public class EmailUtils {

	private static final Logger LOG = Logger.getLogger(EmailUtils.class.getName());
	
	public static String readMultipart(Multipart multipart) throws MessagingException, IOException {
		for (int j = 0; j < multipart.getCount(); j++) {

			BodyPart bodyPart = multipart.getBodyPart(j);

			String disposition = bodyPart.getDisposition();

			if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { // BodyPart.ATTACHMENT
			                                                                           // doesn't
			                                                                           // work
			                                                                           // for
			                                                                           // gmail
				LOG.debug("Mail have some attachment");

				DataHandler handler = bodyPart.getDataHandler();
				LOG.debug("file name : " + handler.getName());
			} else {
				LOG.debug("Body: " + bodyPart.getContent());
				return bodyPart.getContent().toString();
			}
		}
		return null;
	}

}
