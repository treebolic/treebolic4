/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
	public HyperlinkButton(final Icon image, final String uri)
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
	public HyperlinkButton(final String label, final String uri)
	{
		super(label);
		init(uri);
	}

	void init(final String uri)
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
		final String format = "<HTML><U>%s</U></HTML>";
		return String.format(format, text);
	}

	// /**
	// * Teest
	// * @param args
	// * @throws URISyntaxException
	// */
	// public static void main(String[] args)
	// {
	// Laf.lookAndFeel(args);
	// final String uri = "https://play.google.com/store/apps/details?id=org.treebolic.wordnet.browser"; 
	// final String label = makeURILabel("Treebolic WordNet"); 
	// final Icon icon = new ImageIcon(HyperlinkButton.class.getResource("images/logo.png")); 
	// JFrame frame = new JFrame("Links"); 
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.setSize(400, 400);
	// Container container = frame.getContentPane();
	// container.setLayout(new GridBagLayout());
	// JButton button = new HyperlinkButton(label, uri);
	// JButton button2 = new HyperlinkButton(icon, uri);
	// container.add(button);
	// container.add(button2);
	// frame.setVisible(true);
	// }
}
