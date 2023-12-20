/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import treebolic.annotations.NonNull;

/**
 * HTML view
 *
 * @author Bernard Bou
 */
public class HtmlView extends JEditorPane implements HyperlinkListener
{
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

	@Override
	public void hyperlinkUpdate(@NonNull final HyperlinkEvent event)
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