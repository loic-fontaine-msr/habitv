package ted;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.AbstractAction;
import javax.swing.Timer;

public class WindowComponentListener implements ComponentListener
{
	private Timer timer;
	private static final int DELAY = 2000;

	public WindowComponentListener()
	{
		timer = new Timer(DELAY, new AbstractAction()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				//TODO save pref ?
			}
		});

		timer.setRepeats(false);
	}

	/**
	 * compoment resized actually works normally with a single event after the mouse is released,
	 * but supposedly it might behave differently on different platforms.
	 *
	 * @param e
	 */
	public void componentResized(ComponentEvent e)
	{
		timer.start();
	}

	public void componentMoved(ComponentEvent e)
	{
		timer.start();
	}

	public void componentShown(ComponentEvent e)
	{
	}

	public void componentHidden(ComponentEvent e)
	{
	}
}
