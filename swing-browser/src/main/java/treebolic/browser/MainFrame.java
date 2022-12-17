/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.browser;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import treebolic.IWidget;
import treebolic.Widget;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.commons.*;
import treebolic.model.Model;
import treebolic.model.ModelWriter;

/**
 * Browser main frame
 *
 * @author Bernard Bou
 */
public class MainFrame extends JFrame implements HyperlinkListener
{
	// D A T A

	/**
	 * Command line arguments
	 */
	private final String[] args;

	/**
	 * Parameters
	 */
	private final Properties parameters;

	/**
	 * Persist
	 */
	@NonNull
	private final Properties settings;

	// C O M P O N E N T S

	/**
	 * Tabbed pane
	 */
	private JTabbedPane tabbedPane;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param args command line arguments
	 */
	public MainFrame(final String[] args)
	{
		// System.out.println("CLASSPATH=<" + System.getProperty("java.class.path", ".") + ">");

		// settings
		this.args = args;
		this.parameters = makeParameters(args);
		this.settings = Persist.getSettings("treebolic-browser");
		setTitle(Messages.getString("MainFrame.title"));

		// menu
		setJMenuBar(makeMenuBar());

		// components
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		@NonNull final JEditorPane home = makeHome();
		home.setPreferredSize(Constants.DIM_APP);
		contentPane.add(home);

		// show
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);

		// open doc 0
		open();
	}

	@Override
	protected void processWindowEvent(@NonNull final WindowEvent event)
	{
		if (event.getID() == WindowEvent.WINDOW_CLOSING)
		{
			Persist.saveSettings("treebolic-browser", this.settings);
		}
		super.processWindowEvent(event);
	}

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
	 * Replace tab
	 *
	 * @param component    component to add
	 * @param oldComponent component to replace
	 * @param title        title
	 * @param toolTip      tooltip
	 */
	public void replaceTab(final Component component, final Component oldComponent, final String title, final String toolTip)
	{
		if (this.tabbedPane == null)
		{
			this.tabbedPane = new JTabbedPane();
			final Container contentPane = getContentPane();
			contentPane.add(this.tabbedPane, BorderLayout.CENTER);
			validate();
		}

		final int index = this.tabbedPane.indexOfComponent(oldComponent);
		if (index == -1)
		{
			addTab(component, title, toolTip);
			return;
		}
		this.tabbedPane.setComponentAt(index, component);
		this.tabbedPane.setTitleAt(index, mangle(title));
		this.tabbedPane.setToolTipTextAt(index, toolTip);
	}

	// M A K E . C O M P O N E N T S

	/**
	 * HTML home pane
	 */
	@NonNull
	private JEditorPane makeHome()
	{
		return makeBrowserPane(MainFrame.class.getResource("splash/home.html"));
	}

	/**
	 * HTML pane
	 */
	@NonNull
	private JEditorPane makeBrowserPane(final URL url)
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
		return panel;
	}

	/**
	 * Make menu bar
	 *
	 * @return menu bar
	 */
	@NonNull
	protected JMenuBar makeMenuBar()
	{
		@NonNull final JMenuItem openMenuItem = makeItem(Messages.getString("MainFrame.open"), "open.png", Code.OPEN, 79);
		@NonNull final JMenuItem openUrlMenuItem = makeItem(Messages.getString("MainFrame.openurl"), "openurl.png", Code.OPENURL, 0);
		@NonNull final JMenuItem openProviderMenuItem = makeItem(Messages.getString("MainFrame.openprovider"), "openprovider.png", Code.OPENPROVIDER, 0);
		@NonNull final JMenuItem openBundleMenuItem = makeItem(Messages.getString("MainFrame.openzip"), "unzip.png", Code.OPENBUNDLE, 0);
		@NonNull final JMenuItem openDeSerializeMenuItem = makeItem(Messages.getString("MainFrame.openser"), "deserialize.png", Code.OPENDESERIALIZE, 0);
		@NonNull final JMenuItem serializeMenuItem = makeItem(Messages.getString("MainFrame.serialize"), "serialize.png", Code.SERIALIZE, 0);

		@NonNull final JMenuItem settingsMenuItem = makeItem(Messages.getString("MainFrame.settings"), "settings.png", Code.SETTINGS, 0);

		@NonNull final JMenuItem aboutMenuItem = makeItem(Messages.getString("MainFrame.about"), "about.png", Code.ABOUT, 0);
		@NonNull final JMenuItem helpMenuItem = makeItem(Messages.getString("MainFrame.help"), "help.png", Code.HELP, 0);

		@NonNull final JMenu filesMenu = new JMenu();
		filesMenu.setText(Messages.getString("MainFrame.menu_files"));
		filesMenu.add(openMenuItem);
		filesMenu.add(openUrlMenuItem);
		filesMenu.add(openProviderMenuItem);
		filesMenu.add(openBundleMenuItem);
		filesMenu.add(openDeSerializeMenuItem);
		filesMenu.addSeparator();
		filesMenu.add(serializeMenuItem);

		@NonNull final JMenu helpMenu = new JMenu();
		helpMenu.setText(Messages.getString("MainFrame.menu_help"));
		helpMenu.add(aboutMenuItem);
		helpMenu.add(helpMenuItem);

		@NonNull final JMenu optionsMenu = new JMenu();
		optionsMenu.setText(Messages.getString("MainFrame.menu_options"));
		optionsMenu.add(settingsMenuItem);

		@NonNull final JMenuBar menuBar = new JMenuBar();
		menuBar.add(filesMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);
		return menuBar;
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
	private JMenuItem makeItem(final String text, @Nullable final String image, @NonNull final Code command, final int acceleratorKey)
	{
		@NonNull final JMenuItem item = new JMenuItem();
		item.setText(text);
		if (image != null)
		{
			@Nullable URL url = MainFrame.class.getResource("images/" + image);
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

	// C O M M A N D

	/**
	 * Command codes
	 */
	private enum Code
	{
		OPEN, OPENURL, OPENPROVIDER, OPENBUNDLE, OPENDESERIALIZE, SERIALIZE, SETTINGS, ABOUT, HELP
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
			case OPEN:
			{
				@Nullable final String url = FileDialogs.getAnyUrl(this.settings.getProperty("base"));
				if (url == null || url.isEmpty())
				{
					return;
				}
				open(null, url, this.settings.getProperty("base", null), this.settings.getProperty("images", null));
				break;
			}
			case OPENURL:
			{
				@NonNull final UrlDialog dialog = new UrlDialog(this.settings);
				dialog.setModal(true);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				if (dialog.ok)
				{
					final String source = this.settings.getProperty("openurl");
					if (source == null || source.isEmpty())
					{
						@NonNull final String[] lines = {Messages.getString("MainFrame.nullsource")};
						JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
						return;
					}
					open(null, source, null, null);
				}
				break;
			}
			case OPENPROVIDER:
			{
				@NonNull final OpenDialog dialog = new OpenDialog(this.settings.getProperty("provider"), this.settings.getProperty("source"), this.settings.getProperty("base", "."));
				dialog.setModal(true);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				if (dialog.ok)
				{
					// provider
					final String provider = dialog.provider;
					if (provider == null || provider.isEmpty())
					{
						@NonNull final String[] lines = {Messages.getString("MainFrame.nullprovider")};
						JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
						return;
					}

					// source
					final String source = dialog.source;
					if (source == null || source.isEmpty())
					{
						@NonNull final String[] lines = {Messages.getString("MainFrame.nullsource")};
						JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
						return;
					}
					open(provider, source, null, null);
				}
				break;
			}
			case OPENBUNDLE:
			{
				// provider
				final String provider = this.settings.getProperty("provider");
				if (provider == null || provider.isEmpty())
				{
					@NonNull final String[] lines = {Messages.getString("MainFrame.nullprovider")};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				// archive
				@Nullable final String source = FileDialogs.getZip(this.settings.getProperty("base", "."));
				if (source == null || source.isEmpty())
				{
					return;
				}
				@NonNull final File archive = new File(source);
				if (!archive.exists())
				{
					@NonNull final String[] lines = {Messages.getString("MainFrame.nullsource")};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				// entry
				String entry;
				try
				{
					@NonNull final ZipEntryDialog dialog = new ZipEntryDialog(archive);
					dialog.setModal(true);
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					entry = dialog.value;
				}
				catch (final IOException exception1)
				{
					return;
				}

				// open
				try
				{
					openBundle(provider, archive, entry);
				}
				catch (final Exception exception)
				{
					@NonNull final String[] lines = {exception.toString(), exception.getMessage()};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
				}
				break;
			}
			case OPENDESERIALIZE:
			{
				// serialized file
				@Nullable final String source = FileDialogs.getSer(this.settings.getProperty("base", "."));
				if (source == null)
				{
					return;
				}

				// open
				try
				{
					openDeserialize(source, this.settings.getProperty("base"), this.settings.getProperty("images"));
				}
				catch (final Exception exception)
				{
					@NonNull final String[] lines = {exception.toString(), exception.getMessage()};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
				}
				break;
			}
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
					@Nullable final String filePath = FileDialogs.getSer(this.settings.getProperty("base", "."));
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
						JOptionPane.showMessageDialog(null, lines, Messages.getString("MainFrame.title"), JOptionPane.WARNING_MESSAGE);
					}
				}
				break;
			}
			case ABOUT:
			{
				@NonNull final JDialog dialog = new AboutDialog();
				dialog.setModal(true);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				break;
			}
			case SETTINGS:
			{
				@NonNull final SettingsDialog dialog = new SettingsDialog(this.settings, SettingsDialog.PROVIDER | SettingsDialog.BASE | SettingsDialog.IMAGEBASE | SettingsDialog.HELP | SettingsDialog.BROWSER);
				dialog.setModal(true);
				dialog.setVisible(true);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				if (dialog.ok)
				{
					Persist.saveSettings("treebolic-browser", this.settings);
				}
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
	 * Open
	 */
	private void open()
	{
		final List<ContextData> contextDataList = ContextData.getContextData(this.args);
		if (contextDataList != null && !contextDataList.isEmpty())
		{
			for (@NonNull final ContextData contextData : contextDataList)
			{
				runContextData(contextData);
			}
		}
		else
		{
			@NonNull final ContextData contextData = ContextData.getDefaultContextData(this.settings);
			runContextData(contextData);
		}
	}

	/**
	 * Open source
	 *
	 * @param provider0 provider
	 * @param source    source
	 * @param base      base
	 * @param imageBase image base
	 */
	private void open(final String provider0, final String source, final String base, final String imageBase)
	{
		@Nullable String provider = provider0;

		// widget
		@NonNull final Context context = new Context(this, source, base, imageBase);
		@NonNull final IWidget widget = new Widget(context, null);
		context.connect(widget);

		// provider
		if (provider != null && provider.isEmpty())
		{
			provider = null;
		}
		if (provider == null)
		{
			provider = this.settings.getProperty("provider");
		}

		// init
		widget.init(provider, source);

		// tabbed pane
		if (this.tabbedPane == null)
		{
			this.tabbedPane = new JTabbedPane();
			final Container contentPane = getContentPane();
			contentPane.removeAll();
			contentPane.add(this.tabbedPane);
			validate();
		}

		// tab
		@NonNull final String toolTip = "<html><body><strong>" + source + "</strong><br>" + provider + "</body></html>";
		//noinspection ConstantConditions
		assert widget instanceof Component;
		this.tabbedPane.addTab(mangle(source), null, (Component) widget, toolTip);
		final int index = this.tabbedPane.indexOfComponent((Component) widget);
		this.tabbedPane.setTabComponentAt(index, new ButtonTabComponent(this.tabbedPane));
		this.tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Open bundle
	 *
	 * @param source source
	 * @param base   base
	 * @param entry  entry
	 * @throws MalformedURLException malformed URL exception
	 */
	private void openBundle(final String provider, @NonNull final String source, @Nullable final String base, final String entry) throws MalformedURLException
	{
		@NonNull File file = new File(source);
		if (!file.exists() && base != null)
		{
			file = new File(new File(base), source);
		}
		openBundle(provider, file, entry);
	}

	/**
	 * Open bundle
	 *
	 * @param archive archive
	 * @param entry   entry
	 * @throws MalformedURLException malformed URL exception
	 */
	private void openBundle(final String provider, @NonNull final File archive, final String entry) throws MalformedURLException
	{
		final String archiveUrl = archive.toURI().toURL().toString();
		// final String urlString = String.format("jar:%s!/%s", archiveUrl, entry);
		final String base = String.format("jar:%s!/", archiveUrl);
		final String imageBase = String.format("jar:%s!/", archiveUrl);
		open(provider, entry, base, imageBase);
	}

	/**
	 * Open deserialize
	 *
	 * @param source    serialized file path
	 * @param base      base
	 * @param imageBase image base
	 * @throws IOException io exception
	 */
	private void openDeserialize(@NonNull final String source, @Nullable final String base, final String imageBase) throws IOException
	{
		@NonNull File file = new File(source);
		if (!file.exists() && base != null)
		{
			file = new File(new File(base), source);
		}

		// widget
		@NonNull final Context context = new Context(this, source, base, imageBase);
		@NonNull final IWidget widget = new Widget(context, null);
		context.connect(widget);

		// model
		// final Model model = new ModelReader(file.getCanonicalPath()).deserialize();
		// widget.init(model);
		widget.initSerialized(file.getCanonicalPath());

		// tabbed pane
		if (this.tabbedPane == null)
		{
			this.tabbedPane = new JTabbedPane();
			final Container contentPane = getContentPane();
			contentPane.removeAll();
			contentPane.add(this.tabbedPane);
			validate();
		}

		// tab
		@NonNull final String toolTip = "<html><body><strong>" + source + "</strong></body></html>";
		//noinspection ConstantConditions
		assert widget instanceof Component;
		this.tabbedPane.addTab(mangle(source), null, (Component) widget, toolTip);
		final int index = this.tabbedPane.indexOfComponent((Component) widget);
		this.tabbedPane.setTabComponentAt(index, new ButtonTabComponent(this.tabbedPane));
		this.tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Run from context date
	 *
	 * @param contextData context data
	 */
	private void runContextData(@NonNull final ContextData contextData)
	{
		if (contextData.source != null)
		{
			SwingUtilities.invokeLater(() -> {
				final String base = contextData.base != null ? contextData.base : MainFrame.this.settings.getProperty("base", ".");
				final String imageBase = contextData.imageBase != null ? contextData.imageBase : MainFrame.this.settings.getProperty("images", ".");
				try
				{
					if (contextData.isSer)
					{
						openDeserialize(contextData.source, base, imageBase);
					}
					else if (contextData.isZip)
					{
						final String provider = contextData.provider != null ? contextData.provider : MainFrame.this.settings.getProperty("provider", null);
						openBundle(provider, contextData.source, base, contextData.entry);
					}
					else
					{
						final String provider = contextData.provider != null ? contextData.provider : MainFrame.this.settings.getProperty("provider", null);
						open(provider, contextData.source, base, imageBase);
					}
				}
				catch (final Exception e)
				{
					//
				}
			});
		}
	}

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

	/**
	 * Help
	 */
	private void help()
	{
		ExternalBrowser.help(this.settings.getProperty("browser"), this.settings.getProperty("help"));
	}

	// A R G U M E N T S

	/**
	 * Make parameters
	 *
	 * @param args command-line arguments
	 * @return parameters
	 */
	private Properties makeParameters(@Nullable final String[] args)
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
	public Properties getParameters()
	{
		return this.parameters;
	}

	/**
	 * Context data
	 */
	static private class ContextData
	{
		/**
		 * Provider
		 */
		public final String provider;

		/**
		 * Source
		 */
		public final String source;

		/**
		 * Document base
		 */
		public final String base;

		/**
		 * Image base
		 */
		public final String imageBase;

		/**
		 * Entry
		 */
		public final String entry;

		/**
		 * Is bundle
		 */
		public final boolean isZip;

		/**
		 * Is serialized
		 */
		public final boolean isSer;

		/**
		 * Constructor
		 *
		 * @param provider  provider class
		 * @param source    document source
		 * @param base      document base
		 * @param imageBase image base
		 * @param entry     entry
		 * @param isZipFlag is zip
		 * @param isSerFlag is ser
		 */
		public ContextData(final String provider, final String source, final String base, final String imageBase, final String entry, final boolean isZipFlag, final boolean isSerFlag)
		{
			this.provider = provider;
			this.source = source;
			this.base = base;
			this.imageBase = imageBase;
			this.entry = entry;
			this.isZip = isZipFlag;
			this.isSer = isSerFlag;
		}

		/**
		 * Parse context data from command line
		 *
		 * @return context data from command line
		 */
		static List<ContextData> getContextData(@Nullable final String[] args)
		{
			if (args == null)
			{
				return null;
			}
			boolean debug = false;
			@Nullable String defaultProvider = null;
			@Nullable String defaultBase = null;
			@Nullable String defaultImageBase = null;
			@Nullable String provider = null;
			@Nullable String base = null;
			@Nullable String imageBase = null;
			@Nullable String entry = null;
			@Nullable List<ContextData> sources = null;
			for (@NonNull final String arg : args)
			{
				if (arg.startsWith("base="))
				{
					defaultBase = arg.substring(5);
					continue;
				}
				if (arg.startsWith("images="))
				{
					defaultImageBase = arg.substring(7);
					continue;
				}
				if (arg.startsWith("provider="))
				{
					defaultProvider = arg.substring(9);
					continue;
				}
				if (arg.startsWith("provider1="))
				{
					provider = arg.substring(10);
					continue;
				}
				if (arg.startsWith("base1="))
				{
					base = arg.substring(6);
					continue;
				}
				if (arg.startsWith("images1="))
				{
					imageBase = arg.substring(8);
					continue;
				}
				if (arg.startsWith("entry1="))
				{
					entry = arg.substring(7);
					continue;
				}
				if (arg.startsWith("debug"))
				{
					debug = true;
					continue;
				}

				// source
				@NonNull final String[] fields = arg.split("=");
				String source = fields[0];
				boolean isZip = false;
				boolean isSer = false;
				if (fields.length > 1)
				{
					isZip = fields[0].equals("zip");
					isSer = fields[0].equals("ser");
					source = fields[1];
				}

				// record source
				if (sources == null)
				{
					sources = new ArrayList<>();
				}
				@NonNull final ContextData context = new ContextData(provider != null ? provider : defaultProvider, source, base != null ? base : defaultBase, imageBase != null ? imageBase : defaultImageBase, entry, isZip, isSer);
				sources.add(context);
				if (debug)
				{
					System.out.println("Context=" + context);
				}

				// clear
				provider = null;
				base = null;
				imageBase = null;
				entry = null;
			}
			return sources;
		}

		/**
		 * Get context data from settings
		 *
		 * @param settings settings
		 * @return context data from settings
		 */
		@NonNull
		static ContextData getDefaultContextData(@NonNull final Properties settings)
		{
			final String provider = settings.getProperty("provider", null);
			final String source = settings.getProperty("source", null);
			final String base = settings.getProperty("base", ".");
			final String imageBase = settings.getProperty("images", ".");
			@Nullable final String entry = null;
			final boolean isZip = false;
			final boolean isSer = false;
			//noinspection ConstantConditions
			return new ContextData(provider, source, base, imageBase, entry, isZip, isSer);
		}

		@NonNull
		@Override
		public String toString()
		{
			return "p=" + this.provider + " s=" + this.source + " b=" + " e=" + this.entry + " b=" + this.base + " i=" + this.imageBase + " zip=" + this.isZip + " ser=" + this.isSer;
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
	public void linkTo(@NonNull final String linkUrl, @SuppressWarnings("unused") final String linkTarget, @SuppressWarnings("unused") final IWidget widget, @NonNull final Context context)
	{
		@Nullable final URL url = context.makeURL(linkUrl);
		if (url == null)
		{
			return;
		}

		// try internal browser
		if ("true".equals(this.settings.getProperty("internal.browser")))
		{
			@NonNull final JComponent component = makeBrowserPane(url);
			addTab(component, mangle(linkUrl), linkUrl);
			return;
		}

		// try the browser in settings
		final String browser = this.settings.getProperty("browser");
		if (browser != null && !browser.isEmpty())
		{
			ExternalBrowser.browse(browser, url.toString());
			return;
		}

		// default
		ExternalBrowser.browse(linkUrl);
	}

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
