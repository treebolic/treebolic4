/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * HTML view
 *
 * @author Bernard Bou
 */
public class HtmlView extends JEditorPane implements HyperlinkListener
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public HtmlView()
	{
		setText(Messages.getString("HtmlView.init")); 
		setContentType("text/html; charset=UTF-8"); 
		setForeground(Color.blue);
		setEditable(false);
		addHyperlinkListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	@Override
	public void hyperlinkUpdate(final HyperlinkEvent event)
	{
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			final URL uRL = event.getURL();
			try
			{
				setPage(uRL);
			}
			catch (final IOException e)
			{
				setText(Messages.getString("HtmlView.err_url") + event.getURL()); 
			}
		}
	}
}