/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.application;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import treebolic.Widget;

/**
 * Application
 *
 * @author Bernard Bou
 */
public class MainFrame extends JFrame implements HyperlinkListener
{
	private static final long serialVersionUID = 1L;

	protected Widget widget;

	/**
	 * Parameters
	 */
	protected Properties parameters;

	/**
	 * Constructor
	 *
	 * @param args
	 *        arguments
	 */
	public MainFrame(final String[] args)
	{
		//System.out.println("CLASSPATH=<" + System.getProperty("java.class.path", ".") + ">");
		this.parameters = makeParameters(args);
		setTitle("Treebolic"); //$NON-NLS-1$

		// menu
		final JMenuBar menu = makeMenuBar();
		if (menu != null)
			setJMenuBar(menu);

		// container
		Container container = getContentPane();
		container.setLayout(new BorderLayout());

		// toolbar
		final JToolBar toolbar = makeToolBar();
		if (toolbar != null)
			container.add(toolbar, BorderLayout.NORTH);

		// widget
		this.widget = makeWidget();

		// assemble
		container.add((Component)this.widget, BorderLayout.CENTER);

		// show
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Make widget
	 * 
	 * @return widget
	 */
	protected Widget makeWidget()
	{
		// context parameters
		final String source = this.parameters.getProperty("source", getSource()); //$NON-NLS-1$
		final String base = this.parameters.getProperty("base", null); //$NON-NLS-1$
		final String imageBase = this.parameters.getProperty("images", null); //$NON-NLS-1$
		final String provider = this.parameters.getProperty("provider", getProvider()); //$NON-NLS-1$

		// context parameters
		final Context context = makeContext(source, base, imageBase);

		// widget
		final Widget widget = new Widget(context, null);

		// init
		final String serFile = getParameters().getProperty("ser"); //$NON-NLS-1$
		if (serFile == null)
		{
			widget.init(provider, source);
		}
		else
		{
			widget.initSerialized(serFile);
		}
		return widget;
	}

	/**
	 * Get widget
	 * 
	 * @return widget
	 */
	public Widget getWidget()
	{
		return this.widget;
	}

	/**
	 * Get provider
	 * 
	 * @return default provider
	 */
	protected String getProvider()
	{
		return null;
	}

	/**
	 * Get source
	 * 
	 * @return default source
	 */
	protected String getSource()
	{
		return null;
	}

	/**
	 * Make context
	 * 
	 * @param source
	 *        source
	 * @param base
	 *        base
	 * @param imageBase
	 *        image base
	 * @return application context
	 */
	protected Context makeContext(final String source, final String base, final String imageBase)
	{
		return new Context(this, source, base, imageBase);
	}

	/**
	 * Make parameters
	 *
	 * @param args
	 *        command-line arguments
	 * @return parameters
	 */
	protected Properties makeParameters(final String[] args)
	{
		// param1=<val> param2=<"val with spaces"> ...
		if (args == null)
			return null;

		final Properties parameters = new Properties();
		for (final String arg : args)
		{
			final String[] pair = arg.split("="); //$NON-NLS-1$
			if (pair.length != 2)
			{
				continue;
			}
			final String key = pair[0];
			String value = pair[1];
			if (value.startsWith("\"")) //$NON-NLS-1$
			{
				value = value.substring(1);
				if (value.endsWith("\"")) //$NON-NLS-1$
				{
					value = value.substring(0, value.length() - 1);
				}
			}
			parameters.setProperty(key, value);
		}
		return parameters;
	}

	/**
	 * Get parameters
	 * 
	 * @return parameters
	 */
	public Properties getParameters()
	{
		return this.parameters;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent event)
	{
		if (event.getEventType() == EventType.ACTIVATED)
		{
			// follow link
			final JEditorPane editorPane = (JEditorPane) event.getSource();
			try
			{
				editorPane.setPage(event.getURL());
			}
			catch (final IOException e)
			{
				editorPane.setText(Messages.getString("MainFrame.linkerror") + event.getURL()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Make menu bar
	 *
	 * @return toolbar
	 */
	protected JToolBar makeToolBar()
	{
		return null;
	}

	/**
	 * Make menu bar
	 *
	 * @return menu bar
	 */
	protected JMenuBar makeMenuBar()
	{
		return null;

		// final JMenuItem aboutMenuItem = new JMenuItem();
		// aboutMenuItem.setText(Messages.getString("MainFrame.about"));
		// aboutMenuItem.addActionListener(new java.awt.event.ActionListener()
		// {
		// @Override
		// public void actionPerformed(final ActionEvent e)
		// {
		// final AboutDialog dialog = new AboutDialog();
		// dialog.setModal(true);
		// dialog.setVisible(true);
		// dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// }
		// });
		//
		// final JMenu helpMenu = new JMenu();
		// helpMenu.setText(Messages.getString("MainFrame.help")); //$NON-NLS-1$
		// helpMenu.add(aboutMenuItem);
		//
		// final JMenuBar menuBar = new JMenuBar();
		// menuBar.add(helpMenu);
		// return menuBar;
	}

	/**
	 * Main
	 *
	 * @param args
	 *        arguments
	 */
	public static void main(final String[] args)
	{
		new MainFrame(args);
	}
}
