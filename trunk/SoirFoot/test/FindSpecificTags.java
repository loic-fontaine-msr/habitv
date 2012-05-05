import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.provider.soirfoot.SoirFootCategoriesFinder;
import com.dabi.habitv.provider.soirfoot.SoirFootRetriever;

import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;

public class FindSpecificTags {
	public static void main(String[] args) throws Exception {
		
		SoirFootCategoriesFinder.findCategory();
		SoirFootRetriever.findEpisodeByCategory(new CategoryDTO("", "browse-Ligue-1-Ligue1-videos-1-date.html"));
		
		String sourceUrlString = "http://www.soirfoot.com/browse-Ligue-1-Ligue1-videos-1-date.html";
		if (args.length == 0)
			System.err.println("Using default argument of \"" + sourceUrlString + '"');
		else
			sourceUrlString = args[0];
		if (sourceUrlString.indexOf(':') == -1)
			sourceUrlString = "file:" + sourceUrlString;
		MicrosoftConditionalCommentTagTypes.register();
		MasonTagTypes.register();
		Source source = new Source(new URL(sourceUrlString));
		System.out.println("\n*******************************************************************************\n");

		System.out.println("XML Declarations:");
		displaySegments(source.getAllTags(StartTagType.XML_DECLARATION));

		System.out.println("XML Processing instructions:");
		displaySegments(source.getAllTags(StartTagType.XML_PROCESSING_INSTRUCTION));

		PHPTagTypes.register(); // register PHPTagTypes after searching for XML
								// processing instructions, otherwise PHP short
								// tags override them.
		StartTagType.XML_DECLARATION.deregister(); // deregister XML
													// declarations so they are
													// recognised as PHP short
													// tags, consistent with the
													// real PHP parser.
		source = new Source(source); // have to create a new Source object after
										// changing tag type registrations
										// otherwise cache might contain tags
										// found with previous configuration.
		System.out.println("##################### PHP tag types now added to register #####################\n");

		System.out.println("H2 Elements:");
		// displaySegments(source.getAllElements(HTMLElementName.DIV));
		displaySegments(source.getAllStartTags("div class=\"video_i\""));

		System.out.println(source.getCacheDebugInfo());
	}

	private static void displaySegments(List<? extends Segment> segments) {
		for (Segment segment : segments) {
			System.out.println("-------------------------------------------------------------------------------");
			System.out.println(segment.getDebugInfo());
			String url = segment.getChildElements().get(0).getChildElements().get(0).getAttributeValue("href");
			System.out.println(url);
			try {
				Source source = new Source(new URL(url));
				for (Segment segment2 : source.getAllStartTags("param name=\"flashvars\"")) {
					String param = segment2.getChildElements().get(0).getAttributeValue("value");
					String archiveId = param.split("&")[0].split("=")[1];
					String xmlUrl = "http://api.justin.tv/api/broadcast/by_archive/" + archiveId + ".xml?onsite=true";
					Source source2 = new Source(new URL(xmlUrl));
					for (Segment segment3 : source2.getAllStartTags("video_file_url")) {
						String flvUrl = segment3.getChildElements().get(0).getContent().toString();
						saveUrl("D:/tmp/test.flv",flvUrl);
//						URL google = new URL(flvUrl);
//						ReadableByteChannel rbc = Channels.newChannel(google.openStream());
//						FileOutputStream fos = new FileOutputStream("D:/tmp/test.flv");
//						fos.getChannel().transferFrom(rbc, 0, 1 << 24);

					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(segment.getSource());
			// System.out.println(segment.getTextExtractor());
		}
		System.out.println("\n*******************************************************************************\n");
	}

	public static void saveUrl(String filename, String urlString) throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(urlString).openStream());
			fout = new FileOutputStream(filename);

			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		}
	}

}
