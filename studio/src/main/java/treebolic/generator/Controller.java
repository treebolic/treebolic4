/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.Component;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import treebolic.IContext;
import treebolic.IWidget;
import treebolic.Widget;
import treebolic.commons.ButtonTabComponent;
import treebolic.commons.ExternalBrowser;
import treebolic.commons.FileDialogs;
import treebolic.commons.Interact;
import treebolic.commons.OpenDialog;
import treebolic.commons.Persist;
import treebolic.commons.XslExportDialog;
import treebolic.commons.XslImportDialog;
import treebolic.commons.ZipEntryDialog;
import treebolic.dtd.Dtd;
import treebolic.generator.dialogs.XSettingsDialog;
import treebolic.generator.dialogs.XSiteDialog;
import treebolic.generator.dialogs.XUrlDialog;
import treebolic.generator.domtree.DomTreeView;
import treebolic.generator.model.ModelUtils;
import treebolic.generator.tree.TreeAdapter;
import treebolic.generator.tree.TreeView;
import treebolic.glue.component.Statusbar;
import treebolic.model.Model;
import treebolic.model.ModelDump;
import treebolic.model.ModelReader;
import treebolic.model.ModelWriter;
import treebolic.model.MutableEdge;
import treebolic.model.MutableNode;
import treebolic.model.Settings;
import treebolic.model.Tree;
import treebolic.model.TreeMutableEdge;
import treebolic.model.TreeMutableNode;
import treebolic.propertyview.SelectListener;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.xml.DocumentAdapter;
import treebolic.provider.xml.Parser;
import treebolic.site.MakeSiteDialog;
import treebolic.site.RunSiteDialog;
import treebolic.xml.transformer.DomTransformer;
import treebolic.zip.ZipMaker;

/**
 * Controller
 *
 * @author Bernard Bou
 */
public class Controller implements IContext, IProviderContext, SelectListener, ChangeListener, CellEditorListener, HyperlinkListener
{
	// T R E E B O L I C . W I D G E T

	/**
	 * Treebolic widget
	 */
	private IWidget widget;

	/**
	 * Treebolic model
	 */
	public Model model;

	/**
	 * Id to node map
	 */
	private Map<String, MutableNode> idToNodeMap;

	// D O C U M E N T

	/**
	 * Document
	 */
	private Document document;

	/**
	 * Document url
	 */
	private URL url;

	/**
	 * Document mode
	 */
	private Mode mode;

	/**
	 * XML validate
	 */
	static public boolean validate = true;

	// T R E E

	/**
	 * Tree model
	 */
	public TreeModel treeModel;

	/**
	 * Tree view
	 */
	private TreeView treeView;

	// P R O P E R T Y . V I E W

	private PropertyView propertyView;

	// O T H E R . V I E W S

	/**
	 * Text view
	 */
	private TextView textView;

	/**
	 * Dom tree view
	 */
	private DomTreeView domTreeView;

	// F R A M E

	/**
	 * Frame
	 */
	private JFrame frame;

	// T A B B E D P A N E

	/**
	 * Tabbed pane
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Tabbed pane selection index
	 */
	private int selectedTabIndex;

	// C O M M A N D L I N E

	/**
	 * Properties
	 */
	private final Properties parameters;

	// C O M M A N D . C O D E S

	/**
	 * Command code
	 */
	public enum Code
	{
		NEW, OPEN, OPENURL, IMPORTXSL, IMPORTPROVIDER, //
		EXPORT, SAVE, SAVEAS, //
		ZIP, UNZIP, SERIALIZE, DESERIALIZE, //
		NEWELEMENT, REMOVEELEMENT, //
		EXPORTSETTINGS, //
		NORMALIZEIDS, LISTIMAGES, LISTLINKS, LISTMOUNTS, LISTIDS, //
		MAKESITE, RUNSITE, //
		SETTINGS, SETTINGSBASE, SETTINGSURL, //
		UPDATE, //
		OPTIONTREEBOLICRENDERER, OPTIONVALIDATEXML, OPTIONFOCUSPARENT, //
		EXPANDTREE, COLLAPSETREE, //
		SELECTTOP, SELECTTREE, SELECTNODES, SELECTEDGES, //
		DTD, //
		ABOUT, HELP,
	}

	// O P E N . M O D E S

	/**
	 * Command code
	 */
	public enum Mode
	{
		NEW, OPEN, IMPORT, DESERIALIZE, UNZIP
	}

	// U P D A T E . R O U T I N E S

	/**
	 * Map component to update action
	 */
	public final Map<Component, Runnable> updateMap;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param args command line arguments
	 */
	public Controller(final String[] args)
	{
		this.parameters = makeParameters(args);
		this.document = null;
		this.url = null;
		this.mode = null;
		this.settings = Persist.getSettings("treebolic-generator");
		this.updateMap = new HashMap<>();
	}

	// C O N N E C T

	/**
	 * Connect to frame
	 *
	 * @param frame frame
	 */
	public void connect(final JFrame frame)
	{
		this.frame = frame;
	}

	/**
	 * Connect to tree view
	 *
	 * @param treeView treeview
	 */
	public void connect(final TreeView treeView)
	{
		this.treeView = treeView;
	}

	/**
	 * Connect to property view
	 *
	 * @param propertyView property view
	 */
	public void connect(final PropertyView propertyView)
	{
		this.propertyView = propertyView;
	}

	/**
	 * Connect to text view
	 *
	 * @param textView text view
	 */
	public void connect(final TextView textView)
	{
		this.textView = textView;
	}

	/**
	 * Connect to Dom tree view
	 *
	 * @param domTreeView DOM tree view
	 */
	public void connect(final DomTreeView domTreeView)
	{
		this.domTreeView = domTreeView;
	}

	/**
	 * Connect to widget
	 *
	 * @param widget widget
	 */
	public void connect(final IWidget widget)
	{
		this.widget = widget;
	}

	/**
	 * Connect to tabbed pane
	 *
	 * @param tabbedPane tabbed pane
	 */
	public void connect(final JTabbedPane tabbedPane)
	{
		this.tabbedPane = tabbedPane;
		this.selectedTabIndex = this.tabbedPane.getSelectedIndex();
	}

	// D O C U M E N T

	/**
	 * Make document
	 *
	 * @return document
	 */
	private Document makeDocument(final Model model)
	{
		return ModelToDocumentTransformer.transform(model);
	}

	/**
	 * Set document
	 *
	 * @param document document
	 * @param uRL      url
	 */
	private void setDocument(final Document document, final URL uRL)
	{
		this.document = document;
		this.url = this.document == null ? null : uRL;
		this.model = null;
		this.idToNodeMap = null;

		if (this.document == null)
		{
			return;
		}

		// document -> model
		final DocumentAdapter adapter = new DocumentAdapter()
		{
			@Override
			protected MutableNode makeNode(final MutableNode parent, final String id)
			{
				return new TreeMutableNode(parent, id);
			}

			@Override
			protected MutableEdge makeEdge(final MutableNode fromNode, final MutableNode toNode)
			{
				return new TreeMutableEdge(fromNode, toNode);
			}
		};
		this.model = adapter.makeModel(this.document);
		this.idToNodeMap = adapter.getIdToNodeMap();
	}

	// M O D E L

	/**
	 * Get model
	 *
	 * @return model
	 */
	public Model getModel()
	{
		return this.model;
	}

	/**
	 * Set model
	 *
	 * @param model       model
	 * @param idToNodeMap id to node map
	 */
	private void setModel(final Model model, final Map<String, MutableNode> idToNodeMap)
	{
		this.document = null;
		this.url = null;
		this.model = model;
		this.idToNodeMap = idToNodeMap;
	}

	// S E T T I N G S

	/**
	 * Settings
	 */
	private final Properties settings;

	// L I S T E N E R S

	// from tree

	/*
	 * (non-Javadoc)
	 * @see treebolic.propertyview.SelectListener#onSelected(java.lang.Object)
	 */
	@Override
	public void onSelected(final Object object)
	{
		// System.err.println("CONTROLLER: selected " + object);

		// relay to property view
		this.propertyView.onSelected(object);

		// propertyView.requestFocus();
	}

	// from tab

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(final ChangeEvent event)
	{
		// on leaving tab 0
		if (this.selectedTabIndex == 0)
		{
			this.document = makeDocument(this.model);
		}

		this.selectedTabIndex = this.tabbedPane.getSelectedIndex();
		updateView();
	}

	// from property view

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void editingStopped(final ChangeEvent event)
	{
		this.treeView.onEditingStopped();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.CellEditorListener#editingCanceled(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void editingCanceled(final ChangeEvent event)
	{
		// do nothing
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
			final JEditorPane view = (JEditorPane) event.getSource();
			final URL uRL = event.getURL();
			try
			{
				view.setPage(uRL);
			}
			catch (final IOException e)
			{
				view.setText(Messages.getString("Controller.linkto_err") + event.getURL());
			}
		}
	}

	// E X E C U T E

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.IController#execute(int, int)
	 */
	public void execute(final Code code, final int value)
	{
		switch (code)
		{
			case NEW:
				newDocument();
				break;

			case OPEN:
				open();
				break;

			case OPENURL:
				openUrl();
				break;

			case IMPORTXSL:
				importXsl();
				break;

			case IMPORTPROVIDER:
				importProvider();
				break;

			case SAVE:
				save();
				break;

			case SAVEAS:
				saveAs();
				break;

			case EXPORT:
				exportDocument();
				break;

			case ZIP:
				zipDocument();
				break;

			case UNZIP:
				unzipDocument();
				break;

			case SERIALIZE:
				serializeModel();
				break;

			case DESERIALIZE:
				deserializeModel();
				break;

			case EXPORTSETTINGS:
				exportSettings();
				break;

			case NEWELEMENT:
				newElement();
				break;

			case REMOVEELEMENT:
				removeElement();
				break;

			case NORMALIZEIDS:
				normalizeIds();
				break;

			case LISTIMAGES:
				listImages();
				break;

			case LISTLINKS:
				listLinks();
				break;

			case LISTMOUNTS:
				listMounts();
				break;

			case LISTIDS:
				listIds();
				break;

			case UPDATE:
				updateWidget();
				break;

			case MAKESITE:
				makeSite();
				break;

			case RUNSITE:
				runSite();
				break;

			case SETTINGS:
				settings();
				break;

			case SETTINGSBASE:
				base();
				break;

			case SETTINGSURL:
				url();
				break;

			case OPTIONTREEBOLICRENDERER:
				optionTreebolicRenderer();
				break;

			case OPTIONVALIDATEXML:
				optionValidateXml();
				break;

			case OPTIONFOCUSPARENT:
				optionFocusParent();
				break;

			case EXPANDTREE:
				this.treeView.expand();
				break;

			case COLLAPSETREE:
				this.treeView.collapse();
				break;

			case SELECTTOP:
				select("TopWrapper");
				break;

			case SELECTTREE:
				select("TreeWrapper");
				break;

			case SELECTNODES:
				select("NodesWrapper");
				break;

			case SELECTEDGES:
				select("EdgesWrapper");
				break;

			case ABOUT:
				about();
				break;

			case DTD:
				dtd();
				break;

			case HELP:
				help();
				break;

			default:
				System.err.println("Unhandled action: " + code);
				break;
		}
	}

	// C O M M A N D S

	/**
	 * Exit hook
	 */
	public void exit()
	{
		checkSave();
		Persist.saveSettings("treebolic-generator", this.settings);
	}

	/**
	 * New command
	 */
	private void newDocument()
	{
		checkSave();
		final Model model = new Model(new Tree(ModelUtils.makeDefaultTree(), null), new Settings());
		setModel(model, ModelUtils.makeIdToNodeMap(model));
		update(Mode.NEW);
	}

	/**
	 * Open
	 */
	private void open()
	{
		checkSave();
		final String url = FileDialogs.getXmlUrl(this.settings.getProperty("base", "."));
		if (url != null)
		{
			open(url); // $NON-NLS-1$
		}
	}

	/**
	 * Open Http
	 */
	private void openUrl()
	{
		checkSave();
		final XUrlDialog dialog = new XUrlDialog(this.settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			open(this.settings.getProperty("openurl"));
		}
	}

	/**
	 * Open Url string
	 *
	 * @param urlString Url string
	 */
	public void open(final String urlString)
	{
		final URL url = makeURL(urlString);
		if (url == null)
		{
			return;
		}
		open(url);
	}

	/**
	 * Open Url
	 *
	 * @param url Url
	 */
	private void open(final URL url)
	{
		final URL dtdUrl = Dtd.class.getResource("Treebolic.dtd");
		assert dtdUrl != null;
		try (InputStream dtdIs = dtdUrl.openStream())
		{
			InputSource dtdSource = new InputSource(dtdIs);
			EntityResolver entityResolver = (publicId, systemId) -> dtdSource;
			setDocument(new Parser(Controller.validate).makeDocument(url, entityResolver), url);
		}
		catch (ParserConfigurationException | IOException | SAXException exception)
		{
			exception.printStackTrace();
		}
		update(Mode.OPEN);
	}

	/**
	 * Get file from Url
	 *
	 * @param url Url
	 * @return file
	 */
	static private File getFile(final URL url)
	{
		try
		{
			return new File(url.toURI());
		}
		catch (final URISyntaxException exception)
		{
			// do nothing
		}
		return null;
	}

	/**
	 * Get file from Url
	 *
	 * @param urlString url string
	 * @return file
	 */
	static private File getFile(final String urlString)
	{
		try
		{
			return Controller.getFile(new URL(urlString));
		}
		catch (final MalformedURLException exception)
		{
			// do nothing
		}
		return null;
	}

	/**
	 * Save
	 */
	private void save()
	{
		if (this.mode != Mode.OPEN || this.url == null)
		{
			saveAs();
			return;
		}
		final File file = Controller.getFile(this.url);
		save(file);
	}

	/**
	 * Save as
	 */
	private void saveAs()
	{
		final String filePath = FileDialogs.getXml(this.settings.getProperty("base", "."));
		if (filePath == null)
		{
			return;
		}
		final File file = new File(filePath);
		if (file.exists() && !Interact.confirm(new String[]{filePath, Messages.getString("Controller.exists"), Messages.getString("Controller.prompt_overwrite")}))
		{
			return;
		}
		save(file);
	}

	/**
	 * Save as file
	 *
	 * @param file file
	 */
	private void save(final File file)
	{
		if (file != null)
		{
			try
			{
				this.document = makeDocument(this.model);
				if (this.document != null)
				{
					new DomTransformer(false, "Treebolic.dtd").documentToFile(this.document, file);
					this.treeView.dirty = false;
					this.propertyView.dirty = false;
				}
			}
			catch (final TransformerException exception)
			{
				System.err.println(Messages.getString("Controller.err_saving") + file + " " + exception);
			}
		}
	}

	/**
	 * Check if save is needed
	 */
	private void checkSave()
	{
		if (this.treeView.dirty || this.propertyView.dirty)
		{
			if (Interact.confirm(new String[]{this.url == null ? Messages.getString("Controller.unnamed") : this.url.toString(), Messages.getString("Controller.unsaved"), Messages.getString("Controller.prompt_save")}))
			{
				if (this.mode != Mode.OPEN || this.url == null)
				{
					saveAs();
				}
				else
				{
					save();
				}
			}
		}
	}

	/**
	 * Import document through Xsl
	 */
	private void importXsl()
	{
		checkSave();
		final XslImportDialog dialog = new XslImportDialog(this.settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			final String xml = this.settings.getProperty("importurl");
			final String xslt = this.settings.getProperty("importxsl");
			if (xml != null && xslt != null)
			{
				this.url = null;
				try
				{
					final URL xmlUrl = new URL(xml);
					final URL xsltUrl = new URL(xslt);
					setDocument(new Parser().makeDocument(xmlUrl, xsltUrl, null), xmlUrl);
					update(Mode.IMPORT);
				}
				catch (final MalformedURLException exception)
				{
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * Import document from provider
	 */
	private void importProvider()
	{
		checkSave();
		final OpenDialog dialog = new OpenDialog(this.settings.getProperty("provider"), this.settings.getProperty("source"), this.settings.getProperty("base"));
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.ok)
		{
			final String source = dialog.source;
			final String providerName = dialog.provider;
			if (providerName != null && !providerName.isEmpty() && source != null && !source.isEmpty())
			{
				// make provider
				final IProvider provider = makeProvider(providerName);
				if (provider == null)
				{
					final String[] lines = {Messages.getString("Controller.provider"), providerName, Messages.getString("Controller.provider_null")};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
					return;
				}
				provider.setContext(this);

				// make model
				final Model model = provider.makeModel(source, getBase(), null);
				if (model == null)
				{
					final String[] lines = {Messages.getString("Controller.provider"), providerName, Messages.getString("Controller.model_null_source"), source};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				// make model mutable
				final Pair<Model, Map<String, MutableNode>> result = ModelUtils.toMutable(model);
				if (result.first == null)
				{
					final String[] lines = {Messages.getString("Controller.provider"), providerName, Messages.getString("Controller.modelmutable_null_source"), source};
					JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				setModel(result.first, result.second);
				update(Mode.IMPORT);
			}
		}
	}

	/**
	 * Export document
	 */
	private void exportDocument()
	{
		final XslExportDialog dialog = new XslExportDialog(this.settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			final String exportUrl = this.settings.getProperty("exporturl");
			final String xsltUrl = this.settings.getProperty("exportxsl");
			if (exportUrl.startsWith("view:"))
			{
				transformToView(exportUrl, xsltUrl);
			}
			else
			{
				transformToFile(exportUrl, xsltUrl, exportUrl);
			}
		}
	}

	/**
	 * Zip document (xml, images) into bundle
	 */
	private void zipDocument()
	{
		final String filePath = FileDialogs.getZip(this.settings.getProperty("base", "."));
		if (filePath == null)
		{
			return;
		}
		final File archive = new File(filePath);
		if (archive.exists() && !Interact.confirm(new String[]{filePath, Messages.getString("Controller.archive"), Messages.getString("Controller.prompt_overwrite")}))
		{
			return;
		}
		final String entry = Interact.ask2(Messages.getString("Controller.prompt_zipentry"), "model.xml");
		if (entry == null || entry.isEmpty())
		{
			return;
		}
		final Document document = makeDocument(this.model);
		final URL imagesBase = getImagesBase();
		if (document != null)
		{
			new ZipMaker(document, imagesBase, archive, entry).make();
		}
	}

	/**
	 * UnZip document (xml, images) into bundle
	 */
	private void unzipDocument()
	{
		final String filePath = FileDialogs.getZip(this.settings.getProperty("base", "."));
		if (filePath == null)
		{
			return;
		}
		final File archive = new File(filePath);
		if (!archive.exists())
		{
			return;
		}

		// entry
		String entry;
		try
		{
			final ZipEntryDialog dialog = new ZipEntryDialog(archive);
			dialog.setModal(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			entry = dialog.value;
		}
		catch (final IOException exception1)
		{
			return;
		}
		if (entry == null || entry.isEmpty())
		{
			return;
		}

		// open
		try
		{
			final String archiveUrl = archive.toURI().toURL().toString();
			final String urlString = String.format("jar:%s!/%s", archiveUrl, entry);
			final URL url = new URL(urlString);

			final String imagesUrlString = String.format("jar:%s!/", archiveUrl);
			final URL imageUrl = new URL(imagesUrlString);
			this.propertyView.setImageRepository(imageUrl);

			open(url);
		}
		catch (final Exception exception)
		{
			final String[] lines = {exception.toString(), exception.getMessage()};
			JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Serialize model into bundle
	 */
	private void serializeModel()
	{
		final String filePath = FileDialogs.getSer(this.settings.getProperty("base", "."));
		if (filePath == null)
		{
			return;
		}
		final File file = new File(filePath);
		if (file.exists() && !Interact.confirm(new String[]{filePath, Messages.getString("Controller.file"), Messages.getString("Controller.prompt_overwrite")}))
		{
			return;
		}

		try
		{
			new ModelWriter(file.getCanonicalPath()).serialize(this.model);
		}
		catch (final IOException exception)
		{
			final String[] lines = {exception.toString(), exception.getMessage()};
			JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Serialize model from bundle
	 */
	private void deserializeModel()
	{
		checkSave();
		final String filePath = FileDialogs.getSer(this.settings.getProperty("base", "."));
		if (filePath == null)
		{
			return;
		}
		final File file = new File(filePath);

		try
		{
			this.propertyView.setImageRepository(null);

			final Model model = new ModelReader(file.getCanonicalPath()).deserialize();
			// TODO
			System.out.println(ModelDump.toString(model));
			setModel(model, ModelUtils.makeIdToNodeMap(model));
			update(Mode.DESERIALIZE);
		}
		catch (final Exception exception)
		{
			final String[] lines = {exception.toString(), exception.getMessage()};
			JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Export settings
	 */
	private void exportSettings()
	{
		if (this.model == null || this.settings == null)
		{
			return;
		}
		final Settings settings = this.model.settings;
		final String filePath = FileDialogs.getPropertyFile(this.settings.getProperty("base", "."));
		if (filePath != null)
		{
			ModelUtils.saveSettings(settings, filePath); // $NON-NLS-1$
		}
	}

	// E D I T I N G

	/**
	 * New element
	 */
	private void newElement()
	{
		this.treeView.editAdd();
	}

	/**
	 * Remove element
	 */
	private void removeElement()
	{
		this.treeView.editRemove();
	}

	/**
	 * Normalize ids
	 */
	private void normalizeIds()
	{
		final String prefix = Interact.ask2(Messages.getString("Controller.prompt_idprefix"), "id");
		if (prefix == null)
		{
			return;
		}
		this.idToNodeMap = ModelUtils.normalizeIds(this.model, prefix);
		this.document = makeDocument(this.model);
		updateView();
		this.treeView.dirty = true;
		this.propertyView.dirty = true;
	}

	// L I S T S

	/**
	 * List images
	 */
	private void listImages()
	{
		if (this.model == null)
		{
			return;
		}
		final ImageListDialog dialog = new ImageListDialog(this);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * List links
	 */
	private void listLinks()
	{
		if (this.model == null)
		{
			return;
		}
		final LinkListDialog dialog = new LinkListDialog(this);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * List mounts
	 */
	private void listMounts()
	{
		if (this.model == null)
		{
			return;
		}
		final MountListDialog dialog = new MountListDialog(this);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * List mounts
	 */
	private void listIds()
	{
		if (this.model == null)
		{
			return;
		}
		final IdListDialog dialog = new IdListDialog(this);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	// D T D

	/**
	 * Get Dtd
	 */
	public void dtd()
	{
		final TextView view = makeTextView();
		final JComponent component = new JScrollPane(view);
		this.tabbedPane.addTab("DTD", null, component, null);
		final int index = this.tabbedPane.indexOfComponent(component);
		this.tabbedPane.setTabComponentAt(index, new ButtonTabComponent(this.tabbedPane));
		this.tabbedPane.setSelectedIndex(index);

		final String text = Dtd.getString();
		view.setText(text);
		view.setCaretPosition(0);
	}

	// B E H A V I O U R

	/**
	 * Toggle treebolic rendering in Dom tree view
	 */
	private void optionTreebolicRenderer()
	{
		final TreeCellRenderer renderer0 = this.domTreeView.getCellRenderer();
		final boolean isSpecific = renderer0 instanceof treebolic.generator.domtree.treebolic.Renderer;
		final TreeCellRenderer renderer = isSpecific ? new treebolic.generator.domtree.Renderer() : new treebolic.generator.domtree.treebolic.Renderer();
		this.domTreeView.setCellRenderer(renderer);
		this.domTreeView.repaint();
	}

	/**
	 * Toggle XML validation
	 */
	private void optionValidateXml()
	{
		Controller.validate = !Controller.validate;
	}

	/**
	 * Toggle focus parent behaviour in tree view
	 */
	private void optionFocusParent()
	{
		treebolic.generator.tree.Tree.focusParent = !treebolic.generator.tree.Tree.focusParent;
	}

	// H E L P E R S

	/**
	 * Transform to view
	 */
	public void transformToView(final String exportUrl, final String xsltUrlString)
	{
		if (this.document != null)
		{
			// type of view
			final boolean isHtml = exportUrl.endsWith("html");
			final JTextComponent view = isHtml ? makeHtmlView(this) : makeTextView();
			final JComponent component = new JScrollPane(view);

			// tab
			this.tabbedPane.addTab(Messages.getString("Controller.export"), null, component, null);
			final int index = this.tabbedPane.indexOfComponent(component);
			this.tabbedPane.setTabComponentAt(index, new ButtonTabComponent(this.tabbedPane));
			this.tabbedPane.setToolTipTextAt(index, xsltUrlString);
			this.tabbedPane.setSelectedIndex(index);

			try
			{
				final URL xsltUrl = new URL(xsltUrlString);
				final DomTransformer transformer = new DomTransformer(isHtml, null);
				final String text = transformer.documentToString(this.document, xsltUrl);
				view.setText(text);
				view.setCaretPosition(0);
				return;
			}
			catch (final IOException | TransformerException exception)
			{
				final String[] lines = {exception.toString(), exception.getMessage()};
				JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
			}
			return;
		}
		final String[] lines = {Messages.getString("Controller.err_document_null")};
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Transform to file
	 */
	public void transformToFile(final String exportUrl, final String xsltUrlString, final String outputFileUrl)
	{
		if (this.document != null)
		{
			// type of view
			final boolean isHtml = exportUrl.endsWith("html");
			try
			{
				final File outputFile = Controller.getFile(outputFileUrl);
				if (outputFile.exists() && !Interact.confirm(new String[]{outputFileUrl, Messages.getString("Controller.file"), Messages.getString("Controller.prompt_overwrite")}))
				{
					return;
				}
				final URL xsltUrl = new URL(xsltUrlString);
				final DomTransformer transformer = new DomTransformer(isHtml, null);
				transformer.documentToFile(this.document, xsltUrl, outputFile);
			}
			catch (final IOException | TransformerException exception)
			{
				final String[] lines = {exception.toString(), exception.getMessage()};
				JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
			}
			return;
		}
		final String[] lines = {Messages.getString("Controller.err_document_null")};
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
	}

	// D I A L O G S

	/**
	 * Make site
	 */
	private void makeSite()
	{
		this.document = makeDocument(this.model);
		if (this.document != null)
		{
			final MakeSiteDialog dialog = new MakeSiteDialog(this.settings);
			dialog.setDocument(this.document);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
	}

	/**
	 * Run site
	 */
	private void runSite()
	{
		final RunSiteDialog dialog = new RunSiteDialog(this.settings);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Persist
	 */
	private void settings()
	{
		final XSettingsDialog dialog = new XSettingsDialog(this.settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			treebolic.commons.Persist.saveSettings("treebolic-generator", this.settings);
			this.propertyView.setImageRepository(makeImageRepositoryURL());
			updateView();
		}
	}

	/**
	 * Get base
	 */
	private void base()
	{
		final XSiteDialog dialog = new XSiteDialog(this.settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * Get Url
	 */
	private void url()
	{
		final XUrlDialog dialog = new XUrlDialog(this.settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**
	 * Help
	 */
	private void help()
	{
		ExternalBrowser.help(this.settings.getProperty("browser"), this.settings.getProperty("help"));
	}

	/**
	 * About
	 */
	private void about()
	{
		new AboutDialog().setVisible(true);
	}

	// S E L E C T

	/**
	 * Select tree node
	 *
	 * @param className class name of tree node to select
	 */
	private void select(final String className)
	{
		final DefaultMutableTreeNode node = this.treeView.search(className);
		if (node != null)
		{
			this.treeView.select(node);
		}
	}

	// U P D A T E

	/**
	 * Update
	 *
	 * @param mode mode
	 */
	public void update(final Mode mode)
	{
		this.mode = mode;

		// update view
		updateView();

		// title
		String name = "";
		String shortTitle = "";
		switch (this.mode)
		{
			case OPEN:
				if (this.url != null)
				{
					name = this.url.toString();
					shortTitle = this.url.getFile();
					final int index = shortTitle.lastIndexOf('/');
					if (index != -1)
					{
						shortTitle = shortTitle.substring(index + 1);
					}
					shortTitle = " - " + shortTitle;
				}
				break;
			case NEW:
				name = Messages.getString("Controller.new");
				shortTitle = name;
				break;
			case IMPORT:
				name = Messages.getString("Controller.imported");
				shortTitle = name;
				break;
			case UNZIP:
				name = Messages.getString("Controller.unzipped");
				shortTitle = name;
				break;
			case DESERIALIZE:
				name = Messages.getString("Controller.deserialized");
				shortTitle = name;
				break;
			default:
				break;
		}
		for (int i = 0; i < 4; i++)
		{
			this.tabbedPane.setToolTipTextAt(i, name);
		}
		this.frame.setTitle(Messages.getString("Controller.title") + " - " + shortTitle);
	}

	/**
	 * Update current view
	 */
	public void updateView()
	{
		final Component component = this.tabbedPane.getSelectedComponent();
		final Runnable action = this.updateMap.get(component);
		if (action != null)
		{
			action.run();
		}
	}

	/**
	 * Update tree view
	 */
	public void updateTree()
	{
		// model -> tree model
		this.treeModel = this.model == null ? null : new TreeAdapter(this.model);

		// tree view
		this.treeView.set(this.treeModel);
		this.treeView.repaint();

		// property view
		this.propertyView.setIdToNodeMap(this.idToNodeMap);

		// reset dirty flags
		this.treeView.dirty = false;
		this.propertyView.dirty = false;

		// unselect
		onSelected(null);
	}

	/**
	 * Update widget view
	 */
	public void updateWidget()
	{
		// System.out.println(ModelDump.toString(this.model));
		this.widget.init(this.model);
	}

	/**
	 * Update xml text view
	 */
	public void updateText()
	{
		if (this.document != null)
		{
			try
			{
				final String text = new DomTransformer(false, "Treebolic.dtd").documentToString(this.document);
				this.textView.setText(text);
				this.textView.setCaretPosition(0);
			}
			catch (final TransformerException exception)
			{
				// do nothing
			}
		}
	}

	/**
	 * Update Dom view
	 */
	public void updateDom()
	{
		if (this.document != null)
		{
			this.domTreeView.setDocument(this.document);
		}
	}

	// V I E W . F A C T O R Y

	/**
	 * Make the Html view
	 *
	 * @return Html view
	 */
	private HtmlView makeHtmlView(final Controller controller)
	{
		final HtmlView htmlView = new HtmlView();
		htmlView.addHyperlinkListener(controller);
		return htmlView;
	}

	/**
	 * Make the text view
	 *
	 * @return text view
	 */
	private TextView makeTextView()
	{
		final TextView textView = new TextView();
		textView.setMargin(new Insets(10, 20, 10, 10));
		return textView;
	}

	// U R L . F A C T O R Y

	/**
	 * Make image repository Url
	 *
	 * @return image repository Url
	 */
	public URL makeImageRepositoryURL()
	{
		final String imageRepositoryPath = this.settings.getProperty("images");
		if (imageRepositoryPath != null)
		{
			File folder = null;
			if (imageRepositoryPath.startsWith("file"))
			{
				try
				{
					folder = new File(new URI(imageRepositoryPath));
				}
				catch (final URISyntaxException exception)
				{
					// do nothing
				}
			}
			else
			{
				folder = new File(imageRepositoryPath);
			}
			if (folder.exists() && folder.isDirectory())
			{
				try
				{
					return folder.toURI().toURL();
				}
				catch (final MalformedURLException exception)
				{
					// do nothing
				}
			}
		}

		URL url;
		try
		{
			url = new URL(getBase(), "images");
			return url;
		}
		catch (final MalformedURLException exception)
		{
			// do nothing
		}
		return null;
	}

	/**
	 * Make based Url
	 *
	 * @param subPath  if not null this this the extra path to add to the base
	 * @param filename filename
	 * @return url
	 */
	private URL makeBasedURL(@SuppressWarnings("SameParameterValue") final String subPath, final String filename)
	{
		if (filename == null)
		{
			return null;
		}
		try
		{
			return new URL(getBase(), (subPath == null ? "" : subPath + "/") + filename);
		}
		catch (final MalformedURLException e)
		{
			System.err.println(Messages.getString("Controller.err_badurl") + filename);
			return null;
		}
	}

	/**
	 * Make Url
	 *
	 * @param source source
	 * @return url
	 */
	public URL makeURL(final String source)
	{
		if (source == null)
		{
			return null;
		}

		// try to consider it well-formed full-fledged url
		try
		{
			return new URL(source);
		}
		catch (final MalformedURLException e)
		{
			// do nothing
		}

		// try source relative to a base
		final URL baseUrl = makeBasedURL(null, source);
		if (baseUrl != null)
		{
			return baseUrl;
		}

		// try to consider it file
		final File file = new File(source);
		if (file.exists() && file.canRead())
		{
			try
			{
				return file.toURI().toURL();
			}
			catch (final MalformedURLException exception)
			{
				// do nothing
			}
		}

		// fail
		return null;
	}

	// P R O V I D E R . F A C T O R Y

	/**
	 * Make provider
	 *
	 * @param providerName provider name
	 * @return provider
	 */
	private IProvider makeProvider(final String providerName)
	{
		try
		{
			final Class<?> clazz = Class.forName(providerName);
			final Class<?>[] argsClass = new Class[]{};
			final Object[] args = new Object[]{};

			final Constructor<?> constructor = clazz.getConstructor(argsClass);
			final Object instance = constructor.newInstance(args);
			return (IProvider) instance;
		}
		catch (final ClassNotFoundException | InvocationTargetException | IllegalArgumentException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
		{
			System.err.println("Provider factory: " + e);
		}
		return null;
	}

	// C O N T E X T

	/*
	 * (non-Javadoc)
	 * @see treebolic.component.Context#getBase()
	 */
	@Override
	public URL getBase()
	{
		// base parameter
		final String baseSetting = this.settings.getProperty("base");
		final String baseParameter = getParameters().getProperty("base");
		String uRLString = baseSetting != null ? baseSetting : baseParameter != null ? baseParameter : System.getProperty("user.dir");

		// tail
		if (!uRLString.endsWith("/"))
		{
			uRLString += "/";
		}

		// make
		try
		{
			return new URL(uRLString);
		}
		catch (final MalformedURLException e)
		{
			// make from folder
			try
			{
				final File folder = new File(uRLString);
				return folder.toURI().toURL();
			}
			catch (final MalformedURLException e2)
			{
				// do nothing
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.Context#getImagesBase()
	 */
	@Override
	public URL getImagesBase()
	{
		return makeImageRepositoryURL();
	}

	/**
	 * Make parameters
	 *
	 * @param args command-line arguments
	 * @return parameters
	 */
	private Properties makeParameters(final String[] args)
	{
		// param1=<val> param2=<"val with spaces"> ...
		if (args == null)
		{
			return null;
		}

		final Properties parameters = new Properties();
		for (final String arg : args)
		{
			final String[] pair = arg.split("=");
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

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getParameters()
	 */
	@Override
	public Properties getParameters()
	{
		return this.parameters;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getStyle()
	 */
	@Override
	public String getStyle()
	{
		return ".content { }" + ".link {color: blue;font-size: small; }" + ".mount {color: red;}" + ".linking {color: #007D82; font-size: small; }" + ".mounting {color: #007D82; font-size: small; }" + ".searching {color: #007D82; font-size: small; }";
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.component.Context#linkTo(java.lang.String)
	 */
	@Override
	public boolean linkTo(final String linkUrl, final String urlTarget)
	{
		System.out.println(Messages.getString("Controller.linkto") + linkUrl + " , " + urlTarget);

		ExternalBrowser.browse(linkUrl);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.component.Context#showStatus(java.lang.String)
	 */
	@Override
	public void status(final String string)
	{
		System.out.println(Messages.getString("Controller.status") + string);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getInput()
	 */
	@Override
	public String getInput()
	{
		if (this.widget != null)
		{
			final Statusbar statusbar = ((Widget) this.widget).getStatusbar();
			if (statusbar != null)
			{
				return statusbar.get();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#warn(java.lang.String)
	 */
	@Override
	public void warn(final String message)
	{
		final String[] lines = message.split("\n");
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Controller.title"), JOptionPane.WARNING_MESSAGE);
	}

	// P R O V I D E R C O N T E X T

	/*
	 * (non-Javadoc)
	 * @see treebolic.model.IProviderContext#putMessage(java.lang.String)
	 */
	@Override
	public void message(final String string)
	{
		System.out.println(string);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.model.IProviderContext#putProgress(java.lang.String, boolean)
	 */
	@Override
	public void progress(final String string, final boolean fail)
	{
		System.out.println(string);
	}
}
