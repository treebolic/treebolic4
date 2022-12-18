/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.fungi.browser;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.Properties;

import javax.swing.*;

import treebolic.IWidget;
import treebolic.annotations.NonNull;
import treebolic.commons.Persist;
import treebolic.control.Commander;
import treebolic.fungi.browser.Context.SourceListener;
import treebolic.glue.component.SearchTool;
import treebolic.glue.component.Statusbar;
import treebolic.provider.sql.SqlProperties;

/**
 * Browser main frame
 *
 * @author Bernard Bou
 */
public class MainFrame extends treebolic.application.MainFrame implements SourceListener
{
	// D E F A U L T S

	/**
	 * Default source
	 */
	public static final String DEFAULTSOURCE = "query-en-wikipedia.properties";

	/**
	 * Default French source
	 */
	public static final String DEFAULTFRSOURCE = "query-fr-mycodb.properties";

	/**
	 * Default pruning
	 */
	public static final String DEFAULTPRUNE = "substr(id,1,1) NOT IN ('e')";

	// M E M B E R S

	/**
	 * Command-line arguments
	 */
	private final String[] args;

	/**
	 * Current source
	 */
	private String source;

	/**
	 * Persist properties
	 */
	private Properties persistProperties;

	// actions

	/**
	 * Restart action
	 */
	protected AbstractAction restartAction;

	/**
	 * Set source action
	 */
	protected AbstractAction setSourceAction;

	/**
	 * Source action
	 */
	protected AbstractAction sourceAction;

	/**
	 * Set truncate action
	 */
	protected AbstractAction setTruncateAction;

	/**
	 * Truncate action
	 */
	protected AbstractAction truncateAction;

	/**
	 * Reset truncate action
	 */
	protected AbstractAction resetTruncateAction;

	/**
	 * Set set prune action
	 */
	protected AbstractAction setPruneAction;

	/**
	 * Set prune action
	 */
	protected AbstractAction pruneAction;

	/**
	 * Colors action
	 */
	protected AbstractAction colorsAction;

	/**
	 * Font size action
	 */
	protected AbstractAction fontSizeAction;

	/**
	 * About action
	 */
	protected AbstractAction aboutAction;

	/**
	 * Search tool
	 */
	private SearchTool searchTool;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param args command-line arguments
	 */
	public MainFrame(final String[] args)
	{
		super(args);

		// global settings
		Commander.TOOLTIP_HTML = true; // depends on provider
		Commander.TOOLTIP_LINESPAN = 25;

		this.args = args;
		this.source = null;
		setTitle(Messages.getString("MainFrame.title"));
	}

	// A C C E S S
	@Override
	protected String getProvider()
	{
		return "treebolic.provider.sqlx.Provider";
	}
	@Override
	protected String getSource()
	{
		if (this.source == null)
		{
			this.source = getPersistSource();
		}
		return this.source;
	}

	/**
	 * Get persisted souce
	 *
	 * @return persisted source
	 */
	protected String getPersistSource()
	{
		return MainFrame.this.getPersistParameters().getProperty(SqlProperties.SOURCE, getDefaultSource());
	}

	/**
	 * Get default source
	 *
	 * @return default source
	 */
	protected String getDefaultSource()
	{
		if ("fr".equals(Locale.getDefault().getLanguage()))
		{
			return MainFrame.DEFAULTFRSOURCE;
		}
		return MainFrame.DEFAULTSOURCE;
	}

	/**
	 * Get persist parameters
	 *
	 * @return persisted parameters
	 */
	private Properties getPersistParameters()
	{
		return this.persistProperties;
	}

	@SuppressWarnings("SameReturnValue")
	protected String getPersistName()
	{
		return "treebolic-fungi-browser";
	}

	// S O U R C E L I S T E N E R

	@Override
	public void onUpdate(String source)
	{
		System.out.println(Messages.getString("MainFrame.updatesource") + source);
		this.source = source;
	}

	// F A C T O R I E S

	@NonNull
	@Override
	protected Context makeContext(final String source, final String base, final String imageBase)
	{
		File dir = base == null ? null : new File(base);
		if (dir == null || !dir.exists())
		{
			dir = Context.makeDataLocation();
		}

		final Deployer deployer = new Deployer(dir);
		if (!deployer.check())
		{
			deployer.expand();
		}

		SourceDialog.values = deployer.getQueryFiles();
		SourceDialog.labels = deployer.getQueryDescriptions(SourceDialog.values);
		//		for (String queryDescription : SourceDialog.labels)
		//		{
		//			System.out.println("QUERY " + queryDescription);
		//		}

		return new Context(this, source, dir.getAbsolutePath(), imageBase, this);
	}

	@Override
	protected Properties makeParameters(String[] args)
	{
		Properties properties = super.makeParameters(args);
		if (properties == null)
		{
			properties = new Properties();
		}

		// values set, diverging from provider
		properties.put(SqlProperties.PRUNE_NODES, MainFrame.DEFAULTPRUNE);
		properties.put(SqlProperties.PRUNE_TREEEDGES, MainFrame.DEFAULTPRUNE);

		// persist add/override
		this.persistProperties = Persist.loadSettings(getPersistName());
		properties.putAll(this.persistProperties);

		return properties;
	}

	/**
	 * Make menu bar
	 *
	 * @return tool bar
	 */
	@Override
	protected JToolBar makeToolBar()
	{
		makeActions();

		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(true);
		toolbar.setPreferredSize(Constants.DIM_TOOLBAR);
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.add(Box.createHorizontalStrut(16));

		// set truncate
		final JButton restartButton = makeButton(Messages.getString("MainFrame.update"), Messages.getString("MainFrame.update"), "images/update.png", this.restartAction);
		toolbar.add(restartButton);

		// set truncate
		final JButton setTruncateButton = makeButton(Messages.getString("MainFrame.settruncate"), Messages.getString("MainFrame.settruncate"), "images/truncate.png", this.setTruncateAction);
		toolbar.add(setTruncateButton);

		// reset truncate
		final JButton resetTruncateButton = makeButton(Messages.getString("MainFrame.resettruncate"), Messages.getString("MainFrame.resettruncate"), "images/tree.png", this.resetTruncateAction);
		toolbar.add(resetTruncateButton);

		// set prune
		final JButton pruneButton = makeButton(Messages.getString("MainFrame.setprune"), Messages.getString("MainFrame.setprune"), "images/prune.png", this.setPruneAction);
		toolbar.add(pruneButton);

		// search tool
		Statusbar.hasSearch = false;
		this.searchTool = new SearchTool();
		this.searchTool.addListener(new treebolic.glue.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public boolean onAction(Object... params)
			{
				// for (Object param : params) { System.out.print(param + " "); } System.out.println(); 
				String command = (String) params[0];
				if (IWidget.SEARCH.equals(command))
				{
					MainFrame.this.widget.search(command, (String) params[1], (String) params[2], (String) params[3]);
				}
				else
				{
					MainFrame.this.widget.search(command);
				}
				return true;
			}
		});
		toolbar.add(this.searchTool);

		// menu
		final JButton menuButton = new JButton();
		menuButton.setToolTipText(Messages.getString("MainFrame.menutooltip"));
		//noinspection ConstantConditions
		menuButton.setIcon(new ImageIcon(MainFrame.class.getResource("images/menu.png")));
		menuButton.setComponentPopupMenu(makePopupMenu());
		menuButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(final MouseEvent e)
			{
				final JPopupMenu popup = makePopupMenu();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(menuButton);
		toolbar.validate();
		return toolbar;
	}

	/**
	 * Make actions
	 */
	private void makeActions()
	{
		// restart
		if (this.restartAction == null)
		{
			this.restartAction = makeAction("restart", KeyStroke.getKeyStroke('R', InputEvent.ALT_DOWN_MASK), e -> restart());
		}

		// set source
		if (this.setSourceAction == null)
		{
			this.setSourceAction = makeAction("setsource", KeyStroke.getKeyStroke('S', InputEvent.ALT_DOWN_MASK), e -> setSource());
		}

		// set source
		if (this.sourceAction == null)
		{
			this.sourceAction = makeAction("source", null, e -> source());
		}

		// set truncate
		if (this.setTruncateAction == null)
		{
			this.setTruncateAction = makeAction("settruncate", KeyStroke.getKeyStroke('T', InputEvent.ALT_DOWN_MASK), e -> setTruncate());
		}

		// set truncate
		if (this.truncateAction == null)
		{
			this.truncateAction = makeAction("truncate", null, e -> truncate());
		}

		// reset truncate
		if (this.resetTruncateAction == null)
		{
			this.resetTruncateAction = makeAction("resettruncate", KeyStroke.getKeyStroke('Z', InputEvent.ALT_DOWN_MASK), e -> resetTruncate());
		}

		// set prune
		if (this.setPruneAction == null)
		{
			this.setPruneAction = makeAction("setprune", KeyStroke.getKeyStroke('P', InputEvent.ALT_DOWN_MASK), e -> setPrune());
		}

		// set prune
		if (this.pruneAction == null)
		{
			this.pruneAction = makeAction("prune", null, e -> prune());
		}

		// settings
		if (this.colorsAction == null)
		{
			this.colorsAction = makeAction("colors", KeyStroke.getKeyStroke('C', InputEvent.ALT_DOWN_MASK), e -> colors());
		}

		// font
		if (this.fontSizeAction == null)
		{
			this.fontSizeAction = makeAction("colors", KeyStroke.getKeyStroke('F', InputEvent.ALT_DOWN_MASK), e -> fontsize());
		}

		// about
		if (this.aboutAction == null)
		{
			this.aboutAction = makeAction("about", null, e -> about());
		}
	}

	/**
	 * Make action
	 *
	 * @param tag       text
	 * @param keyStroke tooltip
	 * @param listener  action listener
	 * @return button
	 */
	private AbstractAction makeAction(final String tag, final KeyStroke keyStroke, final ActionListener listener)
	{
		final AbstractAction action = new AbstractAction(tag)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				listener.actionPerformed(e);
			}
		};
		// configure the action with the accelerator
		action.putValue(Action.ACCELERATOR_KEY, keyStroke);
		return action;
	}

	/**
	 * Make button
	 *
	 * @param text    text
	 * @param tooltip tooltip
	 * @param image   image
	 * @param action  action
	 * @return button
	 */
	private JButton makeButton(final String text, final String tooltip, final String image, final AbstractAction action)
	{
		final JButton button = new JButton(action); // new JButton(text);
		button.setText(text);
		button.setToolTipText(tooltip);
		//noinspection ConstantConditions
		button.setIcon(new ImageIcon(MainFrame.class.getResource(image)));

		// manually register the accelerator in the button's component input map
		final Object key = action.getValue(Action.NAME);
		button.getActionMap().put(key, action);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), key);

		return button;
	}

	/**
	 * Make popup menu
	 *
	 * @return popup
	 */
	protected JPopupMenu makePopupMenu()
	{
		makeActions();

		// menu
		final JPopupMenu menu = new JPopupMenu();
		menu.setToolTipText(Messages.getString("MainFrame.menutooltip"));

		// restart
		final JMenuItem restartMenuItem = new JMenuItem(this.restartAction);
		restartMenuItem.setText(Messages.getString("MainFrame.update"));
		//noinspection ConstantConditions
		restartMenuItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/update.png")));
		menu.add(restartMenuItem);

		// source
		final JMenuItem setSourceMenuItem = new JMenuItem(this.setSourceAction);
		setSourceMenuItem.setText(Messages.getString("MainFrame.setsource"));
		//noinspection ConstantConditions
		setSourceMenuItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/settings.png")));
		menu.add(setSourceMenuItem);

		final JMenuItem sourceMenuItem = new JMenuItem(this.sourceAction);
		sourceMenuItem.setText(Messages.getString("MainFrame.source"));
		menu.add(sourceMenuItem);

		// truncate
		final JMenuItem setTruncateMenuItem = new JMenuItem(this.setTruncateAction);
		setTruncateMenuItem.setText(Messages.getString("MainFrame.settruncate"));
		//noinspection ConstantConditions
		setTruncateMenuItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/truncate.png")));
		menu.add(setTruncateMenuItem);

		final JMenuItem resetTruncateMenuItem = new JMenuItem(this.resetTruncateAction);
		resetTruncateMenuItem.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.ALT_DOWN_MASK));
		resetTruncateMenuItem.setText(Messages.getString("MainFrame.resettruncate"));
		//noinspection ConstantConditions
		resetTruncateMenuItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/tree.png")));
		menu.add(resetTruncateMenuItem);

		final JMenuItem truncateMenuItem = new JMenuItem(this.truncateAction);
		truncateMenuItem.setText(Messages.getString("MainFrame.truncate"));
		menu.add(truncateMenuItem);

		// prune
		final JMenuItem setPruneMenuItem = new JMenuItem(this.setPruneAction);
		setPruneMenuItem.setText(Messages.getString("MainFrame.setprune"));
		setPruneMenuItem.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.ALT_DOWN_MASK));
		//noinspection ConstantConditions
		setPruneMenuItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/prune.png")));
		menu.add(setPruneMenuItem);

		final JMenuItem pruneMenuItem = new JMenuItem(this.pruneAction);
		pruneMenuItem.setText(Messages.getString("MainFrame.prune"));
		menu.add(pruneMenuItem);

		// settings
		final JMenuItem settingsMenuItem = new JMenuItem();
		settingsMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.ALT_DOWN_MASK));
		settingsMenuItem.setText(Messages.getString("MainFrame.colors"));
		//noinspection ConstantConditions
		settingsMenuItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/palette.png")));
		settingsMenuItem.addActionListener(e -> colors());
		menu.add(settingsMenuItem);

		// font
		final JMenuItem fontSizeItem = new JMenuItem();
		fontSizeItem.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.ALT_DOWN_MASK));
		fontSizeItem.setText(Messages.getString("MainFrame.fontsize"));
		//noinspection ConstantConditions
		fontSizeItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/fontsize.png")));
		fontSizeItem.addActionListener(e -> fontsize());
		menu.add(fontSizeItem);

		// about
		final JMenuItem aboutItem = new JMenuItem();
		aboutItem.setText(Messages.getString("MainFrame.about"));
		//noinspection ConstantConditions
		aboutItem.setIcon(new ImageIcon(treebolic.fungi.browser.MainFrame.class.getResource("images/about.png")));
		aboutItem.addActionListener(e -> about());
		menu.add(aboutItem);

		return menu;
	}

	// H A N D L E R S

	/**
	 * Restart
	 */
	protected void restart()
	{
		// reset search
		this.searchTool.resetSearch();

		// reload params
		this.source = null;
		this.parameters = makeParameters(this.args);

		// remove
		//noinspection ConstantConditions
		getContentPane().remove((Component) this.widget);

		// widget
		this.widget = makeWidget();

		// assemble
		//noinspection ConstantConditions
		getContentPane().add((Component) this.widget);
	}

	/**
	 * Set source
	 */
	protected void setSource()
	{
		final String value = getSource();
		final SourceDialog dialog = new SourceDialog(value);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.ok)
		{
			System.err.println(dialog.value);
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.SOURCE, dialog.value);
			Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
		}
	}

	/**
	 * Choose source
	 */
	protected void source()
	{
		final String source = ask(Messages.getString("MainFrame.source"), getSource());
		if (source != null)
		{
			MainFrame.this.getPersistParameters().setProperty("source", source);
			Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
		}
	}

	/**
	 * Get prune statement
	 *
	 * @return prune statement
	 */
	private String getPrune()
	{
		return MainFrame.this.getPersistParameters().getProperty(SqlProperties.PRUNE_NODES, MainFrame.DEFAULTPRUNE);
	}

	/**
	 * Prune select
	 */
	protected void prune()
	{
		final String prune = ask(Messages.getString("MainFrame.prune"), getPrune());
		if (prune != null)
		{
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.PRUNE_NODES, prune);
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.PRUNE_TREEEDGES, prune);
			Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
		}
	}

	/**
	 * Truncate
	 */
	protected void truncate()
	{
		final String truncate = ask(Messages.getString("MainFrame.truncate"), MainFrame.this.getPersistParameters().getProperty(SqlProperties.TRUNCATE_NODES));
		if (truncate != null)
		{
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.TRUNCATE_NODES, truncate);
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.TRUNCATE_TREEEDGES, truncate);
			Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
		}
	}

	/**
	 * Set truncate
	 */
	protected void setTruncate()
	{
		if (this.source != null)
		{
			final String[] fields = this.source.split(",");
			if (fields.length > 1)
			{
				if (fields[1].startsWith("where:"))
				{
					final String whereClause = fields[1].substring(6);
					MainFrame.this.getPersistParameters().setProperty(SqlProperties.TRUNCATE_NODES, whereClause);
					MainFrame.this.getPersistParameters().setProperty(SqlProperties.TRUNCATE_TREEEDGES, whereClause);
					Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
				}
			}
		}
	}

	/**
	 * Reset truncate
	 */
	protected void resetTruncate()
	{
		this.source = null;
		MainFrame.this.getPersistParameters().remove(SqlProperties.TRUNCATE_NODES);
		MainFrame.this.getPersistParameters().remove(SqlProperties.TRUNCATE_TREEEDGES);
		Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
	}

	/**
	 * Set prune
	 */
	protected void setPrune()
	{
		final String value = getPrune();
		final PruneDialog dialog = new PruneDialog(value);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.ok)
		{
			// System.err.println(dialog.value);
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.PRUNE_NODES, dialog.value);
			MainFrame.this.getPersistParameters().setProperty(SqlProperties.PRUNE_TREEEDGES, dialog.value);
			Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
		}
	}

	/**
	 * Font size
	 */
	protected void fontsize()
	{
		final Integer fontSize = askRange(Messages.getString("MainFrame.fontsizeprompt"), MainFrame.this.getPersistParameters().getProperty("fontsize"), 12, 64);
		if (fontSize != null)
		{
			MainFrame.this.getPersistParameters().setProperty("fontsize", fontSize.toString());
			Persist.saveSettings(MainFrame.this.getPersistName(), MainFrame.this.getPersistParameters());
		}
	}

	/**
	 * Colors
	 */
	protected void colors()
	{
		final ColorSettingsDialog dialog = new ColorSettingsDialog(getPersistParameters(), this);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings(getPersistName(), getPersistParameters());
		}
	}

	/**
	 * About
	 */
	protected void about()
	{
		final JDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	// H E L P E R S

	/**
	 * Ask
	 *
	 * @param message message
	 * @param value   initial value
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
	 * @param value    initial value
	 * @param min      min
	 * @param max      max
	 * @return value
	 */
	protected Integer askRange(final String message0, final String value, @SuppressWarnings("SameParameterValue") final int min, @SuppressWarnings("SameParameterValue") final int max)
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
