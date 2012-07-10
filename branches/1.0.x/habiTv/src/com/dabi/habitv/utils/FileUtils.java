package com.dabi.habitv.utils;

public final class FileUtils {
	
	private FileUtils(){
		
	}
	
	 /**
	  * replace illegal characters in a filename with "_"
	  * illegal characters :
	  *           : \ / * ? | < >
	  * @param name
	  * @return
	  */
	  public static String sanitizeFilename(final String name) {
	    return name.replaceAll("[\\s,:\\\\/*?|<>]", "_");
	  }

}
