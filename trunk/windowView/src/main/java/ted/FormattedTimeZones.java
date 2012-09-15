package ted;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Class to provide a mapping between Windows Style Timezone descriptions to Java Timezone names
 * @author jmitchel
 *
 */
public class FormattedTimeZones {
	private static LinkedHashMap<String,String> timezones = new LinkedHashMap<String, String>();
	static{
		timezones.put("(GMT+13:00) Nuku'alofa","Pacific/Tongatapu");
		timezones.put("(GMT+12:00) Fiji, Kamchatka, Marshall Is.","Pacific/Fiji");
		timezones.put("(GMT+12:00) Auckland, Wellington","Pacific/Auckland");
		timezones.put("(GMT+11:00) Magadan, Solomon Is., New Caledonia","Asia/Magadan");
		timezones.put("(GMT+10:00) Vladivostok","Asia/Vladivostok");
		timezones.put("(GMT+10:00) Hobart","Australia/Hobart");
		timezones.put("(GMT+10:00) Guam, Port Moresby","Pacific/Guam");
		timezones.put("(GMT+10:00) Canberra, Melbourne, Sydney","Australia/Sydney");
		timezones.put("(GMT+10:00) Brisbane","Australia/Brisbane");
		timezones.put("(GMT+09:30) Adelaide","Australia/Adelaide");
		timezones.put("(GMT+09:00) Yakutsk","Asia/Yakutsk");
		timezones.put("(GMT+09:00) Seoul","Asia/Seoul");
		timezones.put("(GMT+09:00) Osaka, Sapporo, Tokyo","Asia/Tokyo");
		timezones.put("(GMT+08:00) Taipei","Asia/Taipei");
		timezones.put("(GMT+08:00) Perth","Australia/Perth");
		timezones.put("(GMT+08:00) Kuala Lumpur, Singapore","Asia/Kuala_Lumpur");
		timezones.put("(GMT+08:00) Irkutsk, Ulaan Bataar","Asia/Irkutsk");
		timezones.put("(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi","Asia/Hong_Kong");
		timezones.put("(GMT+07:00) Krasnoyarsk","Asia/Krasnoyarsk");
		timezones.put("(GMT+07:00) Bangkok, Hanoi, Jakarta","Asia/Bangkok");
		timezones.put("(GMT+06:30) Rangoon","Asia/Rangoon");
		timezones.put("(GMT+06:00) Sri Jayawardenepura","Asia/Colombo");
		timezones.put("(GMT+06:00) Astana, Dhaka","Asia/Dhaka");
		timezones.put("(GMT+06:00) Almaty, Novosibirsk","Asia/Almaty");
		timezones.put("(GMT+05:45) Kathmandu","Asia/Katmandu");
		timezones.put("(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi","Asia/Calcutta");
		timezones.put("(GMT+05:00) Islamabad, Karachi, Tashkent","Asia/Karachi");
		timezones.put("(GMT+05:00) Ekaterinburg","Asia/Yekaterinburg");
		timezones.put("(GMT+04:30) Kabul","Asia/Kabul");
		timezones.put("(GMT+04:00) Baku, Tbilisi, Yerevan","Asia/Baku");
		timezones.put("(GMT+04:00) Abu Dhabi, Muscat","Asia/Dubai");
		timezones.put("(GMT+03:30) Tehran","Asia/Tehran");
		timezones.put("(GMT+03:00) Nairobi","Africa/Nairobi");
		timezones.put("(GMT+03:00) Moscow, St. Petersburg, Volgograd","Europe/Moscow");
		timezones.put("(GMT+03:00) Kuwait, Riyadh","Asia/Kuwait");
		timezones.put("(GMT+03:00) Baghdad","Asia/Baghdad");
		timezones.put("(GMT+02:00) Jerusalem","Asia/Jerusalem");
		timezones.put("(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius","Europe/Helsinki");
		timezones.put("(GMT+02:00) Harare, Pretoria","Africa/Harare");
		timezones.put("(GMT+02:00) Cairo","Africa/Cairo");
		timezones.put("(GMT+02:00) Bucharest","Europe/Bucharest");
		timezones.put("(GMT+02:00) Athens, Istanbul, Minsk","Europe/Athens");
		timezones.put("(GMT+01:00) West Central Africa","Africa/Lagos");
		timezones.put("(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb","Europe/Warsaw");
		timezones.put("(GMT+01:00) Brussels, Copenhagen, Madrid, Paris","Europe/Brussels");
		timezones.put("(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague","Europe/Belgrade");
		timezones.put("(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna","Europe/Amsterdam");
		timezones.put("(GMT) Greenwich Mean Time","GMT");
		timezones.put("(GMT) Casablanca, Monrovia","Africa/Casablanca");
		timezones.put("(GMT) Dublin","Europe/Dublin");
		timezones.put("(GMT) Edinburgh, London","Europe/London");
		timezones.put("(GMT) Lisbon","Europe/Lisbon");
		timezones.put("(GMT-01:00) Azores","Atlantic/Azores");
		timezones.put("(GMT-01:00) Cape Verde Is.","Atlantic/Cape_Verde");
		timezones.put("(GMT-02:00) Mid-Atlantic","Atlantic/South_Georgia");
		timezones.put("(GMT-03:00) Brasilia","America/Sao_Paulo");
		timezones.put("(GMT-03:00) Buenos Aires, Georgetown","America/Buenos_Aires");
		timezones.put("(GMT-03:00) Greenland","America/Thule");
		timezones.put("(GMT-03:30) Newfoundland","America/St_Johns");
		timezones.put("(GMT-04:00) Atlantic Time (Canada)","America/Halifax");
		timezones.put("(GMT-04:00) Caracas, La Paz","America/Caracas");
		timezones.put("(GMT-04:00) Santiago","America/Santiago");
		timezones.put("(GMT-05:00) Bogota, Lima, Quito","America/Bogota");
		timezones.put("(GMT-05:00) Eastern Time (US & Canada)","America/New_York");
		timezones.put("(GMT-05:00) Indiana (East)","America/Indianapolis");
		timezones.put("(GMT-06:00) Central America","America/Costa_Rica");
		timezones.put("(GMT-06:00) Central Time (US & Canada)","America/Chicago");
		timezones.put("(GMT-06:00) Guadalajara, Mexico City, Monterrey","America/Mexico_City");
		timezones.put("(GMT-06:00) Saskatchewan","America/Winnipeg");
		timezones.put("(GMT-07:00) Arizona","America/Phoenix");
		timezones.put("(GMT-07:00) Chihuahua, La Paz, Mazatlan","America/Tegucigalpa");
		timezones.put("(GMT-07:00) Mountain Time (US & Canada)","America/Denver");
		timezones.put("(GMT-08:00) Pacific Time (US & Canada); Tijuana","America/Los_Angeles");
		timezones.put("(GMT-09:00) Alaska","America/Anchorage");
		timezones.put("(GMT-10:00) Hawaii","Pacific/Honolulu");
		timezones.put("(GMT-11:00) Midway Island, Samoa","Pacific/Apia");
		timezones.put("(GMT-12:00) International Date Line West","Etc/GMT-12");
	}
	/****************************************************
     * PUBLIC METHODS
     ****************************************************/
	
	/**
	 *
	 * @return an Array of windows style TimeZone descriptions 
	 */
	public static String[] getDescriptionsArray(){
		return (String[])timezones.keySet().toArray(new String[timezones.size()]);
	}
	/**
	 * 
	 * @return an Array of Java TimeZone ID's
	 */
	public static String[] getTimeZonesArray(){
		return (String[])timezones.values().toArray(new String[timezones.size()]);
	}
	/**
	 * Get the matching TimeZone ID for the windows style description
	 * @param description
	 * @return a Java TimZone ID
	 */
	public static String getTimeZone(String description){
		return timezones.get(description);
	}
	/**
	 * 
	 * @param index
	 * @return a Java TimeZone ID
	 */
	public static String getTimeZone(int index){
		return getTimeZonesArray()[index];
	}
	/**
	 * 
	 * @param timeZoneID
	 * @return the index of a Java TimeZone ID
	 */
	public static int getIndexOfTimezone(String timeZoneID){
		List<String> tz = new ArrayList<String>(timezones.values());
		int index = tz.indexOf(timeZoneID);
		if(index == -1){
			String defaultTimeZoneID = getDefaultTimeZone();
			index = tz.indexOf(defaultTimeZoneID);
		}
		return index;
	}
	/**
	 * Get the matching windows style description for the Java TimeZone ID
	 * @param timeZoneID
	 * @return a windows style description
	 */
	public static String getDescription(String timeZoneID){
		return getDescriptionsArray()[getIndexOfTimezone(timeZoneID)];
	}
	/**
	 * 
	 * @param index
	 * @return a windows style description
	 */
	public static String getDescription(int index){
		return getDescriptionsArray()[index];
	}
	/**
	 * 
	 * @param description
	 * @return the index of a windows style description
	 */
	public static int getIndexOfDescription(String description){
		List<String> descs =  new ArrayList<String>(timezones.keySet());
		int index =  descs.indexOf(description);
		if(index == -1){
			List<String> tz = new ArrayList<String>(timezones.values());
			String defaultTimeZoneID = getDefaultTimeZone();
			index = tz.indexOf(defaultTimeZoneID);
		}
		return index;
	}
	/**
	 * 
	 * @param offset seconds offset from UTC time
	 * @return the first Java TimeZone ID found with the same offset 
	 */
	public static String getTimezoneByOffset(int offset){
		String timeZoneID = "GMT";
		for(Iterator<String> it = timezones.values().iterator(); it.hasNext();){
			String currentTimeZoneID = it.next();
			TimeZone tz = TimeZone.getTimeZone(currentTimeZoneID);
			if(tz.getRawOffset() == offset){
				timeZoneID = currentTimeZoneID;
				break;
			}
		}
		return timeZoneID;
	}
	/**
	 * Get the Default TimeZone ID
	 * if it doesnt exist it will return the closest match based on seconds offset from UTC
	 * @return the default TimeZone ID
	 */
	public static String getDefaultTimeZone(){
		String timeZoneName = "GMT";
		List<String> tz = new ArrayList<String>(timezones.values());
		TimeZone defaultTimeZone = TimeZone.getDefault();
		String defaultTimeZoneID = defaultTimeZone.getID();
		int defaultTimeZoneOffset = defaultTimeZone.getRawOffset();
		if(tz.contains(defaultTimeZoneID)){
				timeZoneName = defaultTimeZoneID;
		}
		else{
			timeZoneName = getTimezoneByOffset(defaultTimeZoneOffset);
		}
		return timeZoneName;
	}
	/**
	 * Get the Default TimeZone ID
	 * if it doesnt exist it will return the closest match based on offset parameter
	 * @param offset seconds offset from UTC time 
	 * @return the default TimeZone ID
	 */
	public static String getDefaultTimeZone(int offset){
		String timeZoneName = "GMT";
		List<String> tz = new ArrayList<String>(timezones.values());
		TimeZone defaultTimeZone = TimeZone.getDefault();
		String defaultTimeZoneID = defaultTimeZone.getID();
		int defaultTimeZoneOffset = defaultTimeZone.getRawOffset();
		if(defaultTimeZoneOffset == offset && tz.contains(defaultTimeZoneID)){
				timeZoneName = defaultTimeZoneID;
		}
		else{
			timeZoneName = getTimezoneByOffset(offset);
		}
		return timeZoneName;
	}
}
