import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SimpleXPath2 {
  public static void main(String[] args) throws Exception {
    XPath xpath = XPathFactory.newInstance().newXPath();
    String topicExpression = "//div[@class=\"video_i\"]";
    InputSource inputSource = new InputSource((new URL("http://www.soirfoot.com/browse-Ligue-1-Ligue1-videos-1-date.html")).openStream());

    // get nodes with the topic PowerBuilder
    NodeList nodes = (NodeList) xpath.evaluate
       (topicExpression, inputSource, XPathConstants.NODESET);

    // output the text content of this node and its descendants.
    //   (includes empty LF because of empty comment (#text))
    System.out.println(nodes.item(0).getTextContent());
    /*
    output :
          http://www.rgagnon/pbhowto.htm
          http://www.rgagnon/pbhowtonew.htm
    */
    // display only the "url" nodes for PowerBuidler
    NodeList urls = nodes.item(0).getChildNodes();
    int j = urls.getLength();
    for (int i = 0; i < j ; i++) {
        if (urls.item(i).getNodeName().equals("url")) {
            System.out.println("url :" + urls.item(i).getTextContent());
        }
    }
    /*
    output :
       url :http://www.rgagnon/pbhowto.htm
       url :http://www.rgagnon/pbhowtonew.htm
    */
  }
}