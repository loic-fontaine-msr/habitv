import java.net.URL;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class TestRome {
	public static void main(String[] args) {
		try {
			URL feedUrl = new URL("http://isohunt.com/js/rss/?iht=10&noSL");

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));

			List<?> entries = feed.getEntries();

			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = (SyndEntry) entries.get(i);
				System.out.println(entry.getTitle());
				for (Object enclosureObject : entry.getEnclosures()) {
					SyndEnclosure enclosure = (SyndEnclosure) enclosureObject;
					System.out.println(enclosure.getUrl());
				}
			}
			// System.out.println(feed);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ERROR: " + ex.getMessage());
		}
	}
}
