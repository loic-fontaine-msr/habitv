package ted;

import java.io.File;
import java.util.StringTokenizer;

public class TedSystemInfo
{
	private static boolean isHeadless=false;
	private static boolean saveLocal;
	private static final String osname = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
	private static final String jrearch =System.getProperty("os.arch").toLowerCase();
	
	public static final String MINIMUM_JAVA = "1.7";
	
	/**
	 * Check if the operating system ted is running on supports trayicons
	 * @return If the OS supports trayicons
	 */
	public static boolean osSupportsJDICTray() 
	{
		// return if the tray program supports the current os ted is running on	
		if ((osIsWindows()|| osIsMac() || osIsLinux() || osIsSolaris()) && !isJre64Bit()) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the operating system ted is running on supports balloons
	 * @return If the OS supports balloons
	 */
	public static boolean osSupportsBalloon() 
	{
		// return if the tray program supports the current os ted is running on
		if ((osIsWindows() || osIsLinux() || osIsSolaris()) && !isJre64Bit()) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean isJre64Bit()
	{
		return jrearch.contains("64");
	}
	
	/**
	 * create user directory from user.home property
	 * @return The user directory
	 */
	public static String getUserDirectory()
	{
		// if user wants to save local, return empty string
		if (saveLocal)
		{
			return "";
		}
		String userdir;
		String seperator = System.getProperty("file.separator");
		String teddir = "Torrent Episode Downloader";
		String windows = "Application Data";
		String mac = "Library" + seperator + "Application Support";
		
		userdir = System.getProperty("user.home");
		
		// if windows:
		if (TedSystemInfo.osIsWindows())
		{
			userdir = userdir + seperator + windows + seperator + teddir + seperator;
		}
		else if (TedSystemInfo.osIsMac())
		{
			userdir = userdir + seperator + mac + seperator + teddir + seperator;
		}
		else if (TedSystemInfo.osIsLinux())
		{
			userdir = userdir + seperator + "." + teddir + seperator;
		}
		else
		{
			// linux??
			return "";
		}	
		
		// if the directory doesn't already exist, create it
	    File dir = new File( userdir );
	    if (!dir.exists()) 
	    {
	      dir.mkdirs();
	    }
		
		return userdir;
	}

	public static void setSaveInLocalDir(boolean saveInLocalDir) 
	{
		// set whether user wants to save in local dir
		saveLocal = saveInLocalDir;	
	}

	/**
	 * @return If ted currently runs on linux
	 */
	public static boolean osIsLinux()
	{
		String osname = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		if (osname.startsWith("linux")) //$NON-NLS-1$
		{
			return true;
		}
		return false;
	}
	
	/**
	 * @return If ted currently runs on solaris
	 */
	public static boolean osIsSolaris()
	{
		String osname = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		if (osname.startsWith("solaris")) //$NON-NLS-1$
		{
			return true;
		}
		return false;
	}
	
	/**
	 * @return If ted currently runs on MacOs
	 */
	public static boolean osIsMac() 
	{
		if (osname.startsWith("mac")) //$NON-NLS-1$
		{
			return true;
		}
		return false;
	}
	
	public static boolean osIsMacLeopardOrBetter()
	{
		int minMainVersion = 10;
		int minSubVersion = 5;
		boolean result = false;
		
		if (osIsMac())
		{
			String version = System.getProperty("os.version").toLowerCase();
			StringTokenizer tokenizer = new StringTokenizer(version, ".");
			// check first and second token
			if (tokenizer.countTokens() >= 2)
			{
				int mainVersion = Integer.parseInt(tokenizer.nextToken());
				int subVersion = Integer.parseInt(tokenizer.nextToken());
				if (mainVersion >= minMainVersion && subVersion >= minSubVersion)
				{
					result = true;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @return If ted currently runs on windows
	 */
	public static boolean osIsWindows() 
	{
		if (osname.startsWith("windows")) //$NON-NLS-1$
		{
			return true;
		}
		return false;
	}
	
	public static String getJavaVersion()
	{
		return java.lang.System.getProperty("java.version");
	}
	
	public static String getJavaVendor()
	{
		return java.lang.System.getProperty("java.vendor");
	}
	
	/**
	 * @return If the current version of java is supported by ted
	 */
	public static boolean isSupportedJavaVersion()
	{
		String version = TedSystemInfo.getJavaVersion();
		
		if (version.compareTo(MINIMUM_JAVA) >= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean hasNativeSystemTray()
	{
		String version = TedSystemInfo.getJavaVersion();
		StringTokenizer tokenizer = new StringTokenizer(version, ".");

		int major = Integer.parseInt(tokenizer.nextToken());
		int minor = Integer.parseInt(tokenizer.nextToken());

		if (major >= 1 && minor >= 6) {
			return true;
		}

		return false;

	}

	/**
	 * @return If the current Java vendor is supported by ted
	 */
	public static boolean isSupportedJavaVendor()
	{
		// DISABLED
		// could be more supported vendors than just sun.
		// like microsoft, apple, etc.
		// first investigate before blocking them all.
		
		/*String vendor = TedSystemInfo.getJavaVendor();
		vendor.toLowerCase();
		System.out.println(vendor);
		
		if (vendor.contains("sun"))
		{
			return true;
		}
		else
		{
			return false;
		}*/
		return true;
	}
	
	/**
	 * @return If the current Java is supported by ted
	 */
	public static boolean isSupportedJava()
	{
		if(isSupportedJavaVersion())
			if(osIsWindows() || osIsMac())
				return true;
			else if(isSupportedJavaVendor())
				return true;
		
		return false;			
	}

	public static boolean isHeadless()
	{
		return isHeadless;
	}

	public static void setHeadless(boolean isHeadless)
	{
		TedSystemInfo.isHeadless = isHeadless;
	}
	
	

}
