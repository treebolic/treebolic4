/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.site;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import treebolic.commons.FileDialogs;
import treebolic.commons.Persist;
import treebolic.commons.Utils;
import treebolic.generator.Pair;
import treebolic.generator.dialogs.XSiteDialog;
import treebolic.provider.xml.Parser;

/**
 * Make site dialog
 *
 * @author Bernard Bou
 */
public class MakeSiteDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * DOM document (source)
	 */
	private Document document;

	// C O M P O N E N T S

	/**
	 * Site information
	 */
	private JLabel siteLabel;

	/**
	 * Template repository information
	 */
	private JLabel repositoryLabel;

	/**
	 * Image repository information
	 */
	private JLabel imageRepositoryLabel;

	/**
	 * Title input
	 */
	private JTextField titleTextField;

	/**
	 * XML file input
	 */
	private JTextField xMLFileTextField;

	/**
	 * HTML file input
	 */
	private JTextField hTMLFileTextField;

	/**
	 * Progress label
	 */
	private JLabel progressLabel;

	/**
	 * Progress bar
	 */
	private JProgressBar progressBar;

	/**
	 * Wheter to chain with run site
	 */
	private JCheckBox runCheckBox;

	/**
	 * Properties (input/output)
	 */
	private final Properties properties;

	// C O N S T R U C T OR

	/**
	 * Constructor
	 */
	public MakeSiteDialog(final Properties properties)
	{
		super();
		this.properties = properties;
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setTitle(Messages.getString("MakeSiteDialog.title")); 
		setResizable(true);

		// checkbox
		this.runCheckBox = new JCheckBox(Messages.getString("MakeSiteDialog.run")); 

		// buttons
		final JButton transferButton = new JButton();
		transferButton.setText(Messages.getString("MakeSiteDialog.tranfer")); 
		final JButton destinationButton = new JButton();
		destinationButton.setText(Messages.getString("MakeSiteDialog.destination")); 
		final JButton cancelButton = new JButton();
		cancelButton.setText(Messages.getString("MakeSiteDialog.cancel")); 
		final JButton browseRepositoryButton = new JButton();
		browseRepositoryButton.setText(Messages.getString("MakeSiteDialog.browse")); 
		final JButton browseImageRepositoryButton = new JButton();
		browseImageRepositoryButton.setText(Messages.getString("MakeSiteDialog.browse")); 
		final JButton modeButton = new JButton();
		modeButton.setText(Messages.getString("MakeSiteDialog.browse")); 

		// images
		final Icon icon = new ImageIcon(MakeSiteDialog.class.getResource("images/sitemake.png")); 
		final JLabel imageLabel = new JLabel();
		imageLabel.setBackground(Color.RED);
		imageLabel.setIcon(icon);
		imageLabel.setVerticalTextPosition(SwingConstants.TOP);
		imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageLabel.setText(Messages.getString("MakeSiteDialog.header")); 

		// progress
		this.progressBar = new JProgressBar();
		this.progressLabel = new JLabel(Messages.getString("MakeSiteDialog.progress_initial")); 

		// labels
		this.repositoryLabel = new JLabel();
		this.repositoryLabel.setEnabled(false);
		this.imageRepositoryLabel = new JLabel();
		this.imageRepositoryLabel.setEnabled(false);
		this.siteLabel = new JLabel();
		this.siteLabel.setEnabled(false);

		// textfields
		this.titleTextField = new JTextField(16);
		this.xMLFileTextField = new JTextField(16);
		this.hTMLFileTextField = new JTextField(16);

		// assemble
		final JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new GridBagLayout());
		dataPanel.add(new JLabel(Messages.getString("MakeSiteDialog.data_title")), new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0)); 
		dataPanel.add(new JLabel(Messages.getString("MakeSiteDialog.data_xml")), new GridBagConstraints(0, 1, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0)); 
		dataPanel.add(new JLabel(Messages.getString("MakeSiteDialog.data_html")), new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0)); 
		dataPanel.add(new JLabel(Messages.getString("MakeSiteDialog.data_dest")), new GridBagConstraints(0, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0)); 
		dataPanel.add(new JLabel(Messages.getString("MakeSiteDialog.data_repo")), new GridBagConstraints(0, 4, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0)); 
		dataPanel.add(new JLabel(Messages.getString("MakeSiteDialog.data_images")), new GridBagConstraints(0, 5, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0)); 

		dataPanel.add(this.titleTextField, new GridBagConstraints(1, 0, 2, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		dataPanel.add(this.xMLFileTextField, new GridBagConstraints(1, 1, 2, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		dataPanel.add(this.hTMLFileTextField, new GridBagConstraints(1, 2, 2, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		dataPanel.add(this.siteLabel, new GridBagConstraints(1, 3, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		dataPanel.add(this.repositoryLabel, new GridBagConstraints(1, 4, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		dataPanel.add(this.imageRepositoryLabel, new GridBagConstraints(1, 5, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));

		dataPanel.add(modeButton, new GridBagConstraints(2, 3, 1, 1, 10., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		dataPanel.add(browseRepositoryButton, new GridBagConstraints(2, 4, 1, 1, 10., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		dataPanel.add(browseImageRepositoryButton, new GridBagConstraints(2, 5, 1, 1, 10., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));

		final JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		progressPanel.setLayout(new GridBagLayout());
		progressPanel.add(this.progressLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 20, 0, 20), 0, 0));
		progressPanel.add(this.progressBar, new GridBagConstraints(0, 1, 1, 1, 1., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 10, 20), 0, 0));

		final JPanel commandPanel = new JPanel();
		commandPanel.add(this.runCheckBox);
		commandPanel.add(cancelButton);
		commandPanel.add(transferButton);

		// events
		browseRepositoryButton.addActionListener(e -> {
			final String path = FileDialogs.getFolder(MakeSiteDialog.this.properties.getProperty("base", "."));  
			if (path != null && !path.isEmpty())
			{
				MakeSiteDialog.this.properties.setProperty("repository", path); 
				MakeSiteDialog.this.repositoryLabel.setText(path);
			}
			else
			{
				MakeSiteDialog.this.properties.remove("repository"); 
				MakeSiteDialog.this.repositoryLabel.setText("<internal>"); 
			}
		});

		browseImageRepositoryButton.addActionListener(e -> {
			final String path = FileDialogs.getFolder(MakeSiteDialog.this.properties.getProperty("base", "."));  
			if (path != null)
			{
				MakeSiteDialog.this.properties.setProperty("images", path); 
				MakeSiteDialog.this.imageRepositoryLabel.setText(path);
			}
		});

		modeButton.addActionListener(e -> {
			final XSiteDialog dialog = new XSiteDialog(MakeSiteDialog.this.properties);
			dialog.setModal(true);
			dialog.setVisible(true);
			MakeSiteDialog.this.siteLabel.setText(getSiteUrlString());
		});

		transferButton.addActionListener(e -> SwingUtilities.invokeLater(this::transfer));

		cancelButton.addActionListener(e -> setVisible(false));

		this.titleTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void removeUpdate(final DocumentEvent e)
			{
				final String title = MakeSiteDialog.this.titleTextField.getText();
				MakeSiteDialog.this.hTMLFileTextField.setText(title + ".html"); 
				MakeSiteDialog.this.xMLFileTextField.setText(title + ".xml"); 
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void insertUpdate(final DocumentEvent e)
			{
				final String title = MakeSiteDialog.this.titleTextField.getText();
				MakeSiteDialog.this.hTMLFileTextField.setText(title + ".html"); 
				MakeSiteDialog.this.xMLFileTextField.setText(title + ".xml"); 
			}

			@Override
			public void changedUpdate(final DocumentEvent e)
			{
				// do nothing
			}
		});

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(imageLabel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(dataPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(progressPanel);
		panel.add(commandPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	/**
	 * Set document
	 *
	 * @param document document
	 */
	public void setDocument(final Document document)
	{
		this.document = document;
	}

	// H E L P E R S

	/**
	 * Get destination Url
	 *
	 * @return site url string
	 */
	private String getSiteUrlString()
	{
		final String mode = this.properties.getProperty("mode"); 
		if ("NET".equals(mode)) 
		{
			// ftp-specific check
			final String server = this.properties.getProperty("server", "");  
			final String directory = this.properties.getProperty("directory", "");  
			final String login = this.properties.getProperty("login", "");  
			final String password = this.properties.getProperty("password", "");  
			if (!server.isEmpty())
			{
				final StringBuilder urlBuffer = new StringBuilder();
				urlBuffer.append("ftp://"); 
				urlBuffer.append(login);
				urlBuffer.append(":"); 
				urlBuffer.append(password);
				urlBuffer.append("@"); 
				urlBuffer.append(server);
				urlBuffer.append("/"); 
				if (directory != null && !directory.isEmpty())
				{
					urlBuffer.append(directory);
					urlBuffer.append("/"); 
				}
				urlBuffer.append(";type=i"); 
				return urlBuffer.toString();
			}
		}
		else
		{
			// file system-specific check
			final String path = this.properties.getProperty("path", "");  
			if (path != null)
			{
				final File directory = new File(path);
				try
				{
					return directory.toURI().toURL().toString();
				}
				catch (final MalformedURLException exception)
				{
					// do nothing
				}
			}
		}
		return ""; 
	}

	// T R A N S F E R

	/**
	 * Transfer
	 */
	private void transfer()
	{
		boolean hasFailed = false;
		try
		{
			// repository check
			String repository = this.properties.getProperty("repository"); 
			if (repository != null && !repository.isEmpty())
			{
				File repositoryFolder;
				if (repository.startsWith("file:")) 
				{
					repositoryFolder = new File(new URI(repository));
				}
				else
				{
					repositoryFolder = new File(repository);
				}
				if (!repositoryFolder.exists() || !repositoryFolder.isDirectory())
				{
					inform(String.format(Messages.getString("MakeSiteDialog.err_repo_notexists"), repository)); 
					repository = null;
				}
				else
				{
					repository = repositoryFolder.toURI().toURL().toString();
				}
			}
			if (repository == null || repository.isEmpty())
			{
				repository = SiteMaker.class.getResource("repository/").toURI().toURL().toString(); 
			}

			// image repository check
			String imageRepository = this.properties.getProperty("images"); 
			if (imageRepository != null && !imageRepository.isEmpty())
			{
				File imageRepositoryFolder;
				if (imageRepository.startsWith("file:")) 
				{
					imageRepositoryFolder = new File(new URI(imageRepository));
				}
				else
				{
					imageRepositoryFolder = new File(imageRepository);
				}
				if (!imageRepositoryFolder.exists() || !imageRepositoryFolder.isDirectory())
				{
					inform(String.format(Messages.getString("MakeSiteDialog.err_imagerepo_notexists"), imageRepository)); 
					imageRepository = null;
				}
				else
				{
					imageRepository = imageRepositoryFolder.toURI().toURL().toString();
				}
			}
			if (imageRepository == null || imageRepository.isEmpty())
			{
				final String imageRepository2 = new File(System.getProperty("user.home") + "/images").toURI().toURL().toString();  
				inform(String.format(Messages.getString("MakeSiteDialog.err_imagerepo_notexists_change"), imageRepository, imageRepository2)); 
				imageRepository = imageRepository2;
			}

			// data check
			final String hTMLFile = this.hTMLFileTextField.getText();
			final String xMLFile = this.xMLFileTextField.getText();
			final String title = this.titleTextField.getText();
			if (hTMLFile.isEmpty() || xMLFile.isEmpty())
			{
				return;
			}

			// create site maker
			SiteMaker siteMaker;
			final String mode = this.properties.getProperty("mode"); 
			if ("NET".equals(mode)) 
			{
				// ftp-specific check
				final String server = this.properties.getProperty("server"); 
				final String directory = this.properties.getProperty("directory"); 
				final String login = this.properties.getProperty("login"); 
				final String password = this.properties.getProperty("password"); 
				if (server.isEmpty() || login.isEmpty())
				{
					return;
				}

				// create
				siteMaker = new FtpSiteMaker(this.document, repository, imageRepository, hTMLFile, xMLFile, title, server, directory, login, password);
			}
			else
			{
				// file system-specific check
				final String path = this.properties.getProperty("path"); 
				if (path != null)
				{
					final File folder = new File(path);
					if (!folder.exists())
					{
						inform(String.format(Messages.getString("MakeSiteDialog.creating_folder"), path)); 
						if (!folder.mkdirs())
						{
							inform(String.format(Messages.getString("MakeSiteDialog.err_creating_folder"), path)); 
							return;
						}
					}
				}

				// create
				siteMaker = new FileSiteMaker(this.document, repository, imageRepository, path, hTMLFile, xMLFile, this.titleTextField.getText());
			}

			// add observers
			siteMaker.addObserver((o, arg) -> {
				final Pair<String, Integer> info = (Pair<String, Integer>) arg;
				MakeSiteDialog.this.progressLabel.setText(info.first);
				MakeSiteDialog.this.progressBar.setValue(info.second);
			});

			// make site
			final boolean hasSucceeded = siteMaker.make();

			// delete observers
			siteMaker.deleteObservers();

			// result
			if (!hasSucceeded)
			{
				hasFailed = true;
				return;
			}
		}
		catch (final IOException exception)
		{
			System.err.println("Transfer: " + exception); 
			return;
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}

		// chain to run
		if (this.runCheckBox.isSelected())
		{
			// update data from properties
			this.properties.setProperty("page", this.hTMLFileTextField.getText()); 
			final RunSiteDialog dialog = new RunSiteDialog(this.properties);
			dialog.setModal(true);
			dialog.setVisible(true);
		}

		setVisible(false);
	}

	// I N F O R M

	/**
	 * Inform dialog
	 *
	 * @param message message
	 */
	protected void inform(final String message)
	{
		final String[] lines = message.split("\n"); 
		JOptionPane.showMessageDialog(null, lines, Messages.getString("MakeSiteDialog.title"), JOptionPane.WARNING_MESSAGE); 
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			// read properties into components
			this.siteLabel.setText(getSiteUrlString());
			this.repositoryLabel.setText(this.properties.getProperty("repository", "<internal>"));  
			this.imageRepositoryLabel.setText(this.properties.getProperty("images")); 
			this.progressLabel.setText(Messages.getString("MakeSiteDialog.progress_notransfer")); 
			this.progressBar.setValue(0);

			pack();
			Utils.center(this);
		}
		else
		{
			// update properties from components
			this.properties.setProperty("page", this.hTMLFileTextField.getText()); 
		}
		super.setVisible(flag);
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false); 
		final Properties settings = Persist.getSettings("treebolic-generator"); 
		String stringUrl;
		if (args.length >= 1)
		{
			stringUrl = args[0];
		}
		else
		{
			JOptionPane.showMessageDialog(null, new String[]{Messages.getString("MakeSiteDialog.choose_xml")}, Messages.getString("MakeSiteDialog.title"), JOptionPane.WARNING_MESSAGE);  
			stringUrl = FileDialogs.getAnyUrl(settings.getProperty("base", "."));  
		}
		if (stringUrl == null)
		{
			System.exit(0);
		}
		Document document = null;
		try
		{
			document = new Parser().makeDocument(new URL(stringUrl), null);
		}
		catch (ParserConfigurationException | IOException | SAXException exception)
		{
			exception.printStackTrace();
		}
		if (document == null)
		{
			System.exit(0);
		}
		final MakeSiteDialog dialog = new MakeSiteDialog(settings);
		dialog.setDocument(document);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setVisible(true);
		Persist.saveSettings("treebolic-generator", settings); 
		System.exit(0);
	}
}
