/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Hyperlink button
 */
public class HyperlinkButton extends JButton
{
	private static final long serialVersionUID = -7350707913667809059L;

	private URI uri;

	/**
	 * Constructor
	 *
	 * @param image image
	 * @param uri   url
	 */
	public HyperlinkButton(final Icon image, @NonNull final String uri)
	{
		super(image);
		init(uri);
	}

	/**
	 * Constructor
	 *
	 * @param label label
	 * @param uri   url
	 */
	public HyperlinkButton(final String label, @NonNull final String uri)
	{
		super(label);
		init(uri);
	}

	void init(@NonNull final String uri)
	{
		try
		{
			this.uri = new URI(uri);
		}
		catch (URISyntaxException exception)
		{
			//
		}

		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setBorderPainted(false);
		this.setBorder(null);
		this.setContentAreaFilled(false);
		this.setOpaque(false);

		this.setToolTipText(this.uri.toString());
		this.addActionListener(e -> {
			if (Desktop.isDesktopSupported())
			{
				if (HyperlinkButton.this.uri != null)
				{
					// we are likely to be on a handler
					SwingUtilities.invokeLater(() -> {
						try
						{
							System.out.println(HyperlinkButton.this.uri);
							Desktop.getDesktop().browse(HyperlinkButton.this.uri);
						}
						catch (IOException e1)
						{
							System.err.println(e1.getMessage() + ':' + HyperlinkButton.this.uri);
						}
					});
					return;
				}
			}
			System.err.println(HyperlinkButton.this.uri);
		});
	}

	/**
	 * Make URI label
	 *
	 * @param text text
	 * @return label
	 */
	static public String makeURILabel(final String text)
	{
		// final String format = "<HTML><FONT color=\"#000099\"><U>%s</U></FONT></HTML>";
		@NonNull final String format = "<HTML><U>%s</U></HTML>";
		return String.format(format, text);
	}
}
