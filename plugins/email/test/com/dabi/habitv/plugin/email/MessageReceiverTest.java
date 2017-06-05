package com.dabi.habitv.plugin.email;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;

public class MessageReceiverTest {

	@Test
	public void test_pop3() throws Exception {
		testEmail(new Pop3MessageReceiver("pop.gmail.com", "995", "testhabitv", "HabiTV410"));
	}
	
	@Test
	public void test_imap() throws Exception {
		testEmail(new IMAPMessageReceiver("pop.gmail.com", "testhabitv", "HabiTV410"));
	}

	private void testEmail(MessageReceiver messageReceiver) throws MessagingException {
		List<Map<String, String>> messages = messageReceiver.getAllMessages();
		System.out.println(messages);
	}

}
