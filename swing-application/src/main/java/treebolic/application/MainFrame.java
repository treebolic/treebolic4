/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.application;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import treebolic.Widget;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Application
 *
 * @author Bernard Bou
 */
public class MainFrame extends JFrame implements HyperlinkListener
{
	@NonNull
	protected Widget widget;

	/**
	 * Parameters
	 */
	@Nullable
	protected Properties parameters;

	/**
	 * Constructor
	 *
	 * @param args arguments
	 */
	public MainFrame(final String[] args)
	{
		//System.out.println("CLASSPATH=<" + System.getProperty("java.class.path", ".") + ">");
		this.parameters = makeParameters(args);
		setTitle("Treebolic");

		// menu
		@Nullable final JMenuBar menu = makeMenuBar();
		if (menu != null)
		{
			setJMenuBar(menu);
		}

		// container
		Container container = getContentPane();
		container.setLayout(new BorderLayout());

		// toolbar
		@Nullable final JToolBar toolbar = makeToolBar();
		if (toolbar != null)
		{
			container.add(toolbar, BorderLayout.NORTH);
		}

		// widget
		this.widget = makeWidget();

		// assemble
		// casting is valid within a Swing runtime where
		// Widget extends Container
		// which extends JPanel
		// which extends JComponent
		// which extends Component
		//noinspection ConstantConditions
		assert this.widget instanceof Component;
		container.add((Component) this.widget, BorderLayout.CENTER);

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
	@NonNull
	protected Widget makeWidget()
	{
		// context parameters
		final String source = this.parameters == null ? null : this.parameters.getProperty("source", getSource());
		final String base = this.parameters == null ? null : this.parameters.getProperty("base", null);
		final String imageBase = this.parameters == null ? null : this.parameters.getProperty("images", null);
		final String provider = this.parameters == null ? null : this.parameters.getProperty("provider", getProvider());

		// context parameters
		@NonNull final Context context = makeContext(source, base, imageBase);

		// widget
		@NonNull final Widget widget = new Widget(context, null);

		// init
		final String serFile = this.parameters == null ? null : this.parameters.getProperty("ser");
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
	@NonNull
	public Widget getWidget()
	{
		return this.widget;
	}

	/**
	 * Get provider
	 *
	 * @return default provider
	 */
	@Nullable
	@SuppressWarnings("SameReturnValue")
	protected String getProvider()
	{
		return null;
	}

	/**
	 * Get source
	 *
	 * @return default source
	 */
	@Nullable
	@SuppressWarnings("SameReturnValue")
	protected String getSource()
	{
		return null;
	}

	/**
	 * Make context
	 *
	 * @param source    source
	 * @param base      base
	 * @param imageBase image base
	 * @return application context
	 */
	@NonNull
	protected Context makeContext(final String source, final String base, final String imageBase)
	{
		return new Context(this, source, base, imageBase);
	}

	/**
	 * Make parameters
	 *
	 * @param args command-line arguments
	 * @return parameters
	 */
	@Nullable
	protected Properties makeParameters(@Nullable final String[] args)
	{
		// param1=<val> param2=<"val with spaces"> ...
		if (args == null)
		{
			return null;
		}

		@NonNull final Properties parameters = new Properties();
		for (@NonNull final String arg : args)
		{
			@NonNull final String[] pair = arg.split("=");
			if (pair.length != 2)
			{
				continue;
			}
			final String key = pair[0];
			String value = pair[1];
			if (value.startsWith("\""))
			{
				value = value.substring(1);
				if (value.endsWith("\""))
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
	@Nullable
	public Properties getParameters()
	{
		return this.parameters;
	}

	@Override
	public void hyperlinkUpdate(@NonNull HyperlinkEvent event)
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
				editorPane.setText(Messages.getString("MainFrame.linkerror") + event.getURL());
			}
		}
	}

	/**
	 * Make menu bar
	 *
	 * @return toolbar
	 */
	@Nullable
	@SuppressWarnings("SameReturnValue")
	protected JToolBar makeToolBar()
	{
		return null;
	}

	/**
	 * Make menu bar
	 *
	 * @return menu bar
	 */
	@Nullable
	@SuppressWarnings("SameReturnValue")
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
		// helpMenu.setText(Messages.getString("MainFrame.help"));
		// helpMenu.add(aboutMenuItem);
		//
		// final JMenuBar menuBar = new JMenuBar();
		// menuBar.add(helpMenu);
		// return menuBar;
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	public static void main(final String[] args)
	{
		new MainFrame(args);
	}
}
