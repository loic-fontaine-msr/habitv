package ted.ui.addshowdialog;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;

public class ImageCanvas extends Canvas {
  /**
	 * 
	 */
	private static final long serialVersionUID = 7793640817922536876L;
Image image;

  public ImageCanvas(String name) {
    MediaTracker media = new MediaTracker(this);
    image = Toolkit.getDefaultToolkit().getImage(name);
    media.addImage(image, 0);
    try {
      media.waitForID(0);  
      }
    catch (Exception e) {}
    }

  public ImageCanvas(ImageProducer imageProducer) {
    image = createImage(imageProducer);
    }

  public void paint(Graphics g) {
    g.drawImage(image, 0,0, this);
    }
}