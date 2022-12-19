/**
 * Title : Treebolic browser
 * Description : Treebolic browser
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.wordnet.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.wordnet.browser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import treebolic.IWidget;
import treebolic.commons.Persist;
import treebolic.commons.SettingsDialog;
import treebolic.glue.component.SearchTool;
import treebolic.glue.component.Statusbar;
import treebolic.provider.wordnet.jwi.DataManager;

/**
 * Browser main frame
 *
 * @author Bernard Bou
 */
public class MainFrame extends treebolic.browser2.MainFrame
{
	// C O N S T R U C T

	/**
	 * Constructor
	 *
	 * @param args command-line arguments
	 */
	public MainFrame(final String[] args)
	{
		super(args);
		setTitle(Messages.getString("MainFrame.app"));
	}

	@Override
	protected String getPersistName()
	{
		return getStaticPersistName();
	}

	/**
	 * Get persist name
	 *
	 * @return persist name
	 */
	public static String getStaticPersistName()
	{
		return "treebolic-wordnet-browser";
	}

	@Override
	protected String getProvider()
	{
		String provider = super.getProvider();
		if (provider != null && !provider.isEmpty())
		{
			return provider;
		}
		provider = "treebolic.provider.wordnet.jwi.compact.Provider";
		this.settings.put("provider", provider);
		return provider;
	}

	// M E N U

	@Override
	protected JPopupMenu makeMenu()
	{
		final JPopupMenu menu = super.makeMenu();
		menu.addSeparator();

		// help
		final JMenu referenceMenu = new JMenu();
		referenceMenu.setText(Messages.getString("MainFrame.reference"));
		referenceMenu.setIcon(new ImageIcon(treebolic.browser2.MainFrame.class.getResource("images/help.png")));
		for (final LinkReference reference : LinkReference.values())
		{
			final String key = reference.getHelpKey();
			if (key == null)
			{
				continue;
			}

			final String label = reference.getLabel();
			final JMenuItem item = new JMenuItem();
			item.setText(label);
			item.setIcon(new ImageIcon(treebolic.browser2.MainFrame.class.getResource("images/help.png")));
			item.addActionListener(new java.awt.event.ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					help(key);
				}
			});
			referenceMenu.add(item);
		}
		menu.add(referenceMenu);
		menu.addSeparator();

		// data
		final JMenuItem dataSettingsItem = new JMenuItem();
		dataSettingsItem.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.ALT_DOWN_MASK));
		dataSettingsItem.setText(Messages.getString("MainFrame.data"));
		dataSettingsItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/data.png")));
		dataSettingsItem.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				dataSettings();
			}
		});
		menu.add(dataSettingsItem);
		menu.addSeparator();

		// colors
		final JMenuItem colorSettingsItem = new JMenuItem();
		colorSettingsItem.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.ALT_DOWN_MASK));
		colorSettingsItem.setText(Messages.getString("MainFrame.colors"));
		colorSettingsItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/palette.png")));
		colorSettingsItem.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				colorSettings();
			}
		});
		menu.add(colorSettingsItem);

		// filter
		final JMenuItem filterSettingsItem = new JMenuItem();
		filterSettingsItem.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.ALT_DOWN_MASK));
		filterSettingsItem.setText(Messages.getString("MainFrame.links"));
		filterSettingsItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/filter.png")));
		filterSettingsItem.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				filterSettings();
			}
		});
		menu.add(filterSettingsItem);
		menu.addSeparator();

		// max
		final JMenuItem maxLinksItem = new JMenuItem();
		maxLinksItem.setAccelerator(KeyStroke.getKeyStroke('M', InputEvent.ALT_DOWN_MASK));
		maxLinksItem.setText(Messages.getString("MainFrame.maxlinks"));
		maxLinksItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/max.png")));
		maxLinksItem.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final String string = ask(Messages.getString("MainFrame.maxlinks"), MainFrame.this.settings.getProperty("link_maxlinks"));//$NON-NLS-2$
				try
				{
					Integer.parseInt(string);
					MainFrame.this.settings.setProperty("link_maxlinks", string);
				}
				catch (final NumberFormatException e2)
				{
					//
				}
			}
		});
		menu.add(maxLinksItem);
		final JMenuItem maxRecurseItem = new JMenuItem();
		maxRecurseItem.setAccelerator(KeyStroke.getKeyStroke('R', InputEvent.ALT_DOWN_MASK));
		maxRecurseItem.setText(Messages.getString("MainFrame.maxrecurse"));
		maxRecurseItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/max.png")));
		maxRecurseItem.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final String string = ask(Messages.getString("MainFrame.maxrecurse"), MainFrame.this.settings.getProperty("link_maxrecurse"));//$NON-NLS-2$
				try
				{
					Integer.parseInt(string);
					MainFrame.this.settings.setProperty("link_maxrecurse", string);
				}
				catch (final NumberFormatException e2)
				{
					//
				}
			}
		});
		menu.add(maxRecurseItem);
		menu.addSeparator();

		// max lines
		final JMenuItem maxLinesItem = new JMenuItem();
		maxLinesItem.setAccelerator(KeyStroke.getKeyStroke('K', InputEvent.ALT_DOWN_MASK));
		maxLinesItem.setText(Messages.getString("MainFrame.labelmaxlines"));
		maxLinesItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/maxlines.png")));
		maxLinesItem.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final Integer value = askRange(Messages.getString("MainFrame.labelmaxlinesprompt"), MainFrame.this.settings.getProperty("label_max_lines"), 0, 20);//$NON-NLS-2$
				if (value != null)
				{
					MainFrame.this.settings.setProperty("label_max_lines", value.toString());
				}
			}
		});
		menu.add(maxLinesItem);

		// font
		final JMenuItem fontSizeItem = new JMenuItem();
		fontSizeItem.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.ALT_DOWN_MASK));
		fontSizeItem.setText(Messages.getString("MainFrame.fontsize"));
		fontSizeItem.setIcon(new ImageIcon(MainFrame.class.getResource("images/fontsize.png")));
		fontSizeItem.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final Integer fontSize = askRange(Messages.getString("MainFrame.fontsizeprompt"), MainFrame.this.settings.getProperty("fontsize"), 12, 64);//$NON-NLS-2$
				if (fontSize != null)
				{
					MainFrame.this.settings.setProperty("fontsize", fontSize.toString());
				}
			}
		});
		menu.add(fontSizeItem);
		return menu;
	}

	// E X T R A S

	@Override
	protected Component[] makeExtras()
	{
		// search tool
		Statusbar.hasSearch = false;
		final SearchTool searchTool = new SearchTool();
		searchTool.addListener(new treebolic.glue.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public boolean onAction(Object... params)
			{
				// for (Object param : params) { System.out.print(param + " "); } System.out.println();
				final Component component = getSelected();
				if (component instanceof IWidget)
				{
					final IWidget widget = (IWidget) component;
					String command = (String) params[0];
					if (IWidget.SEARCH.equals(command))
					{
						widget.search(command, (String) params[1], (String) params[2], (String) params[3]);
					}
					else
					{
						widget.search(command);
					}
				}
				return true;
			}
		});

		return new Component[]{Box.createHorizontalGlue(), searchTool};
	}

	// H A N D L E R S

	@Override
	protected void settings()
	{
		final SettingsDialog dialog = new SettingsDialog(this.settings, SettingsDialog.PROVIDER);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings(getPersistName(), this.settings);
		}
	}

	/**
	 * Data settings
	 */
	protected void dataSettings()
	{
		final DataSettingsDialog dialog = new DataSettingsDialog(this.settings);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.ok)
		{
			if (dialog.changed)
			{
				final String which = this.settings.getProperty("data");
				final String base = this.settings.getProperty("base");
				final String userHomeStr = this.settings.getProperty("userhome", null);
				final boolean userHome = userHomeStr == null || Boolean.parseBoolean(userHomeStr);
				final File cacheHome = Context.makeDataDir(base, userHome);

				try
				{
					DataManager.getInstance().deploy(which, cacheHome);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Color settings
	 */
	protected void colorSettings()
	{
		final ColorSettingsDialog dialog = new ColorSettingsDialog(this.settings, null);
		dialog.setModal(false);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Filter settings
	 */
	private void filterSettings()
	{
		final LinkFilterSettingsDialog dialog = new LinkFilterSettingsDialog(this.settings, null);
		dialog.setModal(false);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	@Override
	protected void about()
	{
		final String data = this.settings.getProperty("data", null);
		final JDialog dialog = new AboutDialog(data);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	@Override
	protected void help()
	{
		final JComponent pane = makeBrowserPane(this.getClass().getResource("doc/index.html"), true);
		addTab(pane, Messages.getString("MainFrame.help"), Messages.getString("MainFrame.helplinks"));//$NON-NLS-2$
	}

	/**
	 * Help
	 *
	 * @param key key
	 */
	protected void help(final String key)
	{
		final JComponent pane = makeBrowserPane(this.getClass().getResource("doc/" + key + ".html"), true);
		addTab(pane, key, key);
	}

	// H E L P E R S

	/**
	 * Ask
	 *
	 * @param message message
	 * @param value initial value
	 * @return input
	 */
	protected String ask(final String message, final String value)
	{
		final String[] lines = message.split("\n");
		return JOptionPane.showInputDialog(null, lines, value);
	}

	/**
	 * Ask int value in range
	 *
	 * @param message0 message
	 * @param value initial value
	 * @param min min
	 * @param max max
	 * @return value
	 */
	protected Integer askRange(final String message0, final String value, final int min, final int max)
	{
		final String message = String.format(message0, min, max);
		final String[] lines = message.split("\n");
		final String string = JOptionPane.showInputDialog(null, lines, value);
		try
		{
			final int newValue = Integer.parseInt(string);
			return newValue >= min && newValue <= max ? newValue : null;
		}
		catch (final NumberFormatException e2)
		{
			//
		}
		return null;
	}
}
