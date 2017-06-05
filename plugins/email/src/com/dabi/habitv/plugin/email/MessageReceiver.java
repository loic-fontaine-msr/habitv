package com.dabi.habitv.plugin.email;

import java.util.List;
import java.util.Map;

public interface MessageReceiver {

	List<Map<String, String>> getAllMessages();

}