/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.browser2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import treebolic.IWidget;
import treebolic.Widget;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.commons.ButtonTabComponent;
import treebolic.commons.ExternalBrowser;
import treebolic.commons.FileDialogs;
import treebolic.commons.Interact;
import treebolic.commons.Persist;
import treebolic.commons.SettingsDialog;
import treebolic.model.Model;
import treebolic.model.ModelWriter;

/**
 * Browser2 main frame
 *
 * @author Bernard Bou
 */
public class MainFrame extends JFrame implements HyperlinkListener
{
	private static final long serialVersionUID = 1L;

	// D A T A

	/**
	 * Settings (merged settings and command-line overrides)
	 */
	@NonNull
	protected final Properties settings;

	// C O M P O N E N T S

	/**
	 * Main pane
	 */
	@NonNull
	private final JComponent mainPane;

	/**
	 * Tabbed pane
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Editor
	 */
	private JComboBox<String> editor;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param args command line arguments
	 */
	public MainFrame(@NonNull final String[] args)
	{
		// settings
		this.settings = makeSettings(Persist.loadSettings(getPersistName()), args);

		// title
		setTitle(Messages.getString("MainFrame.title"));

		// components: toolbar
		@NonNull JToolBar toolbar = makeToolbar();

		// components: pane
		@NonNull final JComponent home = makeHome();
		home.setPreferredSize(Constants.DIM_APP);

		// assemble
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(toolbar, BorderLayout.NORTH);
		contentPane.add(home, BorderLayout.CENTER);
		this.mainPane = home;

		// default close behaviour
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// assemble and show
		pack();
		setVisible(true);

		// first query
		lookup0(args);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Window#processWindowEvent(java.awt.event.WindowEvent)
	 */
	@Override
	protected void processWindowEvent(@NonNull final WindowEvent event)
	{
		if (event.getID() == WindowEvent.WINDOW_CLOSING)
		{
			Persist.saveSettings(getPersistName(), this.settings);
		}
		super.processWindowEvent(event);
	}

	// M A K E . C O M P O N E N T S

	/**
	 * HTML home pane
	 */
	@NonNull
	private JComponent makeHome()
	{
		return makeBrowserPane(this.getClass().getResource("splash/index.html"), false);
	}

	/**
	 * HTML pane
	 *
	 * @param url    url
	 * @param scroll scroll flag
	 * @return component
	 */
	@NonNull
	public JComponent makeBrowserPane(final URL url, final boolean scroll)
	{
		@NonNull final JEditorPane panel = new JEditorPane();
		panel.setEditable(false);
		try
		{
			panel.setPage(url);
		}
		catch (final IOException e)
		{
			panel.setText(Messages.getString("MainFrame.badurl") + url);
		}
		panel.addHyperlinkListener(this);
		return scroll ? new JScrollPane(panel) : panel;
	}

	static private final boolean HIERARCHIZEMENU = false;

	/**
	 * Make menu
	 *
	 * @return menu
	 */
	@NonNull
	protected JPopupMenu makeMenu()
	{
		@NonNull final JMenuItem closeAllMenuItem = makeItem(Messages.getString("MainFrame.close"), "close.png", Code.CLOSEALL, 0);
		@NonNull final JMenuItem serializeMenuItem = makeItem(Messages.getString("MainFrame.serialize"), "serialize.png", Code.SERIALIZE, 0);
		@NonNull final JMenuItem settingsMenuItem = makeItem(Messages.getString("MainFrame.settings"), "settings.png", Code.SETTINGS, 0);
		@NonNull final JMenuItem aboutMenuItem = makeItem(Messages.getString("MainFrame.about"), "about.png", Code.ABOUT, 0);
		@NonNull final JMenuItem helpMenuItem = makeItem(Messages.getString("MainFrame.help"), "help.png", Code.HELP, 0);

		@NonNull final JPopupMenu menu = new JPopupMenu();
		if (MainFrame.HIERARCHIZEMENU)
		{
			@NonNull final JMenu filesMenu = new JMenu();
			filesMenu.setText(Messages.getString("MainFrame.menu_files"));
			filesMenu.add(serializeMenuItem);
			filesMenu.add(closeAllMenuItem);

			@NonNull final JMenu helpMenu = new JMenu();
			helpMenu.setText(Messages.getString("MainFrame.menu_help"));
			helpMenu.add(aboutMenuItem);
			helpMenu.add(helpMenuItem);

			@NonNull final JMenu optionsMenu = new JMenu();
			optionsMenu.setText(Messages.getString("MainFrame.menu_options"));
			optionsMenu.add(settingsMenuItem);

			menu.add(filesMenu);
			menu.add(optionsMenu);
			menu.add(helpMenu);
		}
		else
		{
			menu.add(closeAllMenuItem);
			menu.add(serializeMenuItem);
			menu.add(settingsMenuItem);
			menu.add(aboutMenuItem);
			menu.add(helpMenuItem);
		}
		return menu;
	}

	/**
	 * Make menu item
	 *
	 * @param text           text
	 * @param image          image
	 * @param command        command code
	 * @param acceleratorKey accelerator key
	 * @return menu item
	 */
	@NonNull
	private JMenuItem makeItem(final String text, @Nullable final String image, @NonNull final Code command, @SuppressWarnings("SameParameterValue") final int acceleratorKey)
	{
		@NonNull final JMenuItem item = new JMenuItem();
		item.setText(text);
		if (image != null)
		{
			@Nullable final URL url = MainFrame.class.getResource("images/" + image);
			assert url != null;
			item.setIcon(new ImageIcon(url));
		}
		if (acceleratorKey != 0)
		{
			item.setAccelerator(javax.swing.KeyStroke.getKeyStroke(acceleratorKey, InputEvent.CTRL_DOWN_MASK, false));
		}
		item.addActionListener(e -> execute(command));
		return item;
	}

	/**
	 * Make toolbar
	 */
	@NonNull
	private JToolBar makeToolbar()
	{
		@NonNull final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(true);
		toolbar.setPreferredSize(Constants.DIM_TOOLBAR);
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.add(Box.createHorizontalStrut(16));

		// search icon
		@Nullable final URL url = MainFrame.class.getResource("images/search.png");
		assert url != null;
		@NonNull final JLabel label = new JLabel(new ImageIcon(url));

		// listener
		@NonNull final ActionListener lookupActionListener = e -> MainFrame.this.lookup();

		// edit combo
		this.editor = new JComboBox<>();
		this.editor.setPreferredSize(Constants.DIM_INPUT);
		this.editor.setBorder(new EmptyBorder(2, 2, 2, 2));
		this.editor.setToolTipText(Messages.getString("MainFrame.tooltip_source"));
		this.editor.setEditable(true);
		this.editor.getEditor().addActionListener(lookupActionListener);

		// buttons
		@NonNull final JButton lookupButton = makeButton(Messages.getString("MainFrame.run"), Messages.getString("MainFrame.run"), "images/run.png", lookupActionListener);

		// menu
		@NonNull final JButton menuButton = makeButton(null, Messages.getString("MainFrame.menu"), "images/menu.png", null);
		menuButton.setComponentPopupMenu(makeMenu());
		menuButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(@NonNull final MouseEvent e)
			{
				@NonNull final JPopupMenu popup = makeMenu();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		// extras
		@Nullable final Component[] extras = makeExtras();

		// assemble
		toolbar.add(label);
		toolbar.add(this.editor);
		toolbar.add(lookupButton);
		if (extras != null)
		{
			for (Component extra : extras)
			{
				toolbar.add(extra);
			}
		}
		else
		{
			toolbar.add(Box.createHorizontalGlue());
		}
		toolbar.add(menuButton);
		toolbar.validate();

		return toolbar;
	}

	/**
	 * Make extra components to be added to toolbar
	 *
	 * @return list of components
	 */
	@Nullable
	@SuppressWarnings("SameReturnValue")
	protected Component[] makeExtras()
	{
		return null;
	}

	/**
	 * Make button
	 *
	 * @param text     text
	 * @param tooltip  tooltip
	 * @param image    image
	 * @param listener action listener
	 * @return button
	 */
	@NonNull
	private JButton makeButton(final String text, final String tooltip, @NonNull final String image, final ActionListener listener)
	{
		@Nullable final URL url = MainFrame.class.getResource(image);
		assert url != null;
		@NonNull final JButton button = new JButton(text);
		button.setToolTipText(tooltip);
		button.setIcon(new ImageIcon(url));
		button.addActionListener(listener);
		return button;
	}

	// T A B

	/**
	 * Add tab
	 *
	 * @param component component
	 * @param title     title
	 * @param toolTip   tooltip
	 */
	public void addTab(final Component component, final String title, final String toolTip)
	{
		if (this.tabbedPane == null)
		{
			this.tabbedPane = new JTabbedPane();
			final Container contentPane = getContentPane();
			contentPane.remove(this.mainPane);
			// .removeAll();

			contentPane.add(this.tabbedPane, BorderLayout.CENTER);
			validate();
		}

		// tab
		this.tabbedPane.addTab(title, null, component, toolTip);
		final int index = this.tabbedPane.indexOfComponent(component);
		this.tabbedPane.setTabComponentAt(index, new ButtonTabComponent(this.tabbedPane));
		this.tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Get selected component
	 *
	 * @return selected component
	 */
	@Nullable
	protected Component getSelected()
	{
		if (this.tabbedPane != null)
		{
			final int index = this.tabbedPane.getSelectedIndex();
			if (index != -1)
			{
				return this.tabbedPane.getComponentAt(index);
			}
		}
		return null;
	}

	// C O N T E X T

	/**
	 * Make context
	 *
	 * @param source    source
	 * @param base      base
	 * @param imageBase image base
	 * @param urlScheme url scheme
	 */
	@NonNull
	protected Context makeContext(final String source, final String base, final String imageBase, final String urlScheme)
	{
		return new Context(this, source, base, imageBase, urlScheme);
	}

	// A R G U M E N T S

	/**
	 * Make parameters
	 *
	 * @param settings settings
	 * @param args     command-line arguments
	 * @return parameters
	 */
	@NonNull
	static public Properties makeSettings(@NonNull final Properties settings, @Nullable final String[] args)
	{
		// param1=<val> param2=<"val with spaces"> ...
		if (args != null && args.length >= 1)
		{
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
			settings.putAll(parameters);
		}
		return settings;
	}

	/**
	 * Get parameters
	 *
	 * @return parameters
	 */
	public Properties getParameters()
	{
		return this.settings;
	}

	/**
	 * Get provider
	 *
	 * @return provider class name
	 */
	@Nullable
	protected String getProvider()
	{
		@Nullable String provider = this.settings.getProperty("provider", null);
		if (provider != null && provider.isEmpty())
		{
			provider = null;
		}
		return provider;
	}

	// C O M M A N D

	/**
	 * Command codes
	 */
	private enum Code
	{
		SERIALIZE, CLOSEALL, SETTINGS, ABOUT, HELP
	}

	/**
	 * Run command
	 *
	 * @param code command code
	 */
	private void execute(@NonNull final Code code)
	{
		switch (code)
		{
			case SERIALIZE:
			{
				if (this.tabbedPane == null)
				{
					return;
				}

				// active component/widget
				final Component component = this.tabbedPane.getSelectedComponent();
				//noinspection ConstantConditions
				if (component != null && component instanceof Widget)
				{
					final IWidget widget = (IWidget) component;

					// serialized file
					final String filePath = FileDialogs.getSer(this.settings.getProperty("base", "."));
					if (filePath == null)
					{
						return;
					}
					@NonNull final File file = new File(filePath);
					if (file.exists() && !Interact.confirm(new String[]{filePath, Messages.getString("MainFrame.exists"), Messages.getString("MainFrame.overwrite")}))
					{
						return;
					}

					// open
					try
					{
						serialize(filePath, widget);
					}
					catch (final Exception exception)
					{
						final String[] lines = {exception.toString(), exception.getMessage()};
						JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.serializeerror"), JOptionPane.WARNING_MESSAGE);
					}
				}
				break;
			}
			case CLOSEALL:
			{
				if (this.tabbedPane != null)
				{
					this.tabbedPane.removeAll();
				}
				break;
			}
			case ABOUT:
			{
				about();
				break;
			}
			case SETTINGS:
			{
				settings();
				break;
			}
			case HELP:
			{
				help();
				break;
			}
		}
	}

	/**
	 * Lookup
	 */
	private void lookup()
	{
		@NonNull final String lookup = getLookup();
		lookup(lookup);
	}

	/**
	 * Lookup (first from command line)
	 *
	 * @param args command line arguments
	 */
	private void lookup0(@NonNull final String[] args)
	{
		final String source = this.settings.getProperty("source");
		lookup(source);

		for (@NonNull final String arg : args)
		{
			if (arg.contains("="))
			{
				continue;
			}
			lookup(arg);
		}
	}

	/**
	 * Lookup
	 *
	 * @param source source
	 */
	public void lookup(@Nullable final String source)
	{
		if (source == null || source.isEmpty())
		{
			return;
		}
		@Nullable final String provider = getProvider();
		if (provider == null || provider.isEmpty())
		{
			Interact.warn(Messages.getString("MainFrame.providernull"));
			return;
		}
		final String base = this.settings.getProperty("base", null);
		final String imageBase = this.settings.getProperty("images", null);
		final String urlScheme = this.settings.getProperty("urlscheme", null);

		SwingUtilities.invokeLater(() -> lookup(source, provider, base, imageBase, urlScheme));
	}

	/**
	 * Lookup source
	 *
	 * @param source    source
	 * @param provider  provider
	 * @param base      base
	 * @param imageBase image base
	 * @param urlScheme url scheme
	 */
	private void lookup(final String source, final String provider, final String base, final String imageBase, final String urlScheme)
	{
		System.out.println(Messages.getString("MainFrame.run") + " <" + source + ">");

		// widget
		@NonNull final Context context = makeContext(source, base, imageBase, urlScheme);
		@NonNull final IWidget widget = new Widget(context, null);
		context.connect(widget);

		// widget init
		widget.init(provider, source);

		// tabbed pane
		@NonNull final String toolTip = "<html><body><strong>" + source + "</strong><br>" + provider + "</body></html>";
		final String title = mangle(source);
		//noinspection ConstantConditions
		assert widget instanceof Component;
		addTab((Component) widget, title, toolTip);

		// history
		addHistory(source);

		// focus
		this.editor.requestFocus();
	}

	/**
	 * Help
	 */
	protected void help()
	{
		ExternalBrowser.help(this.settings.getProperty("browser"), this.settings.getProperty("help"));
	}

	/**
	 * About
	 */
	protected void about()
	{
		@NonNull final JDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Settings
	 */
	protected void settings()
	{
		@NonNull final SettingsDialog dialog = new SettingsDialog(this.settings, SettingsDialog.PROVIDER | SettingsDialog.BASE | SettingsDialog.IMAGEBASE | SettingsDialog.HELP | SettingsDialog.BROWSER);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings(getPersistName(), this.settings);
		}
	}

	// H E L P E R S

	/**
	 * Get lookup from editor
	 *
	 * @return lookup
	 */
	@NonNull
	protected String getLookup()
	{
		String lookup = (String) this.editor.getEditor().getItem();
		lookup = lookup.trim();
		return lookup.replaceAll(" +", " ");
	}

	/**
	 * Get persist name
	 *
	 * @return persist name
	 */
	@NonNull
	@SuppressWarnings("SameReturnValue")
	protected String getPersistName()
	{
		return "treebolic-browser2";
	}

	/**
	 * Add history
	 *
	 * @param lookup lookup
	 */
	protected void addHistory(final String lookup)
	{
		this.editor.addItem(lookup);
	}

	/**
	 * Mangle string
	 *
	 * @param str0 string to mangle
	 * @return mangled string
	 */
	private String mangle(@Nullable final String str0)
	{
		if (str0 == null)
		{
			return null;
		}

		@NonNull String str = str0;
		final int index = Math.max(str.lastIndexOf('/'), str.lastIndexOf('\\'));
		if (index != -1)
		{
			str = str.substring(index + 1);
		}
		return str;
	}

	// S E R I A L I Z E

	/**
	 * Serialize
	 *
	 * @param destination destination file
	 * @param widget      widget
	 * @throws IOException io exception
	 */
	private void serialize(final String destination, @NonNull final IWidget widget) throws IOException
	{
		@Nullable final Model model = ((Widget) widget).getModel();
		if (model != null)
		{
			new ModelWriter(destination).serialize(model);
		}
	}

	// E V E N T

	/**
	 * Link to
	 *
	 * @param linkUrl    link url
	 * @param linkTarget linkTarget link target
	 * @param widget     widget
	 * @param context    widget's context
	 */
	public void linkTo(final String linkUrl, @SuppressWarnings("unused") final String linkTarget, @SuppressWarnings("unused") final IWidget widget, @NonNull final Context context)
	{
		@Nullable final URL url = context.makeURL(linkUrl);
		if (url == null)
		{
			return;
		}
		if (this.settings != null)
		{
			// try internal browser
			if ("true".equals(this.settings.getProperty("internal.browser")))
			{
				@NonNull final JComponent component = makeBrowserPane(url, true);
				addTab(component, mangle(linkUrl), linkUrl);
				return;
			}

			// try browser in settings
			final String browser = this.settings.getProperty("browser");
			if (browser != null && !browser.isEmpty())
			{
				ExternalBrowser.browse(browser, url.toString());
				return;
			}
		}

		// default
		ExternalBrowser.browse(linkUrl);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	@Override
	public void hyperlinkUpdate(@NonNull final HyperlinkEvent event)
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
}
