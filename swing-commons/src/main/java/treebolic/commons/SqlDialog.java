/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

/**
 * SQL statement dialog
 *
 * @author Bernard Bou
 */
public class SqlDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Attributes
	 */
	private final String[][] nameValues = new String[][] { //
			{ "nodes.id", null }, 
			{ "nodes.label", null }, 
			{ "nodes.content", null }, 
			{ "nodes.backcolor", null }, 
			{ "nodes.forecolor", null }, 
			{ "nodes.image", null }, 
			{ "nodes.link", null }, 
			{ "nodes.target", null }, 
			{ "nodes.weight", null }, 
			{ "nodes.mountpoint", null }, 
			{ "nodes.mountnow", null }, 

			{ "edges.istree", null }, 
			{ "edges.from", null }, 
			{ "edges.to", null }, 
			{ "edges.label", null }, 
			{ "edges.image", null }, 
			{ "edges.color", null }, 
			{ "edges.hidden", null }, 
			{ "edges.stroke", null }, 
			{ "edges.fromterminator", null }, 
			{ "edges.toterminator", null }, 

			{ "settings.backimage", null }, 
			{ "settings.backcolor", null }, 
			{ "settings.forecolor", null }, 
			{ "settings.fontface", null }, 
			{ "settings.fontsize", null }, 
			{ "settings.scalefonts", null }, 
			{ "settings.fontscaler", null }, 
			{ "settings.scaleimages", null }, 
			{ "settings.imagescaler", null }, 
			{ "settings.orientation", null }, 
			{ "settings.expansion", null }, 
			{ "settings.sweep", null }, 
			{ "settings.preserveorientation", null }, 
			{ "settings.hastoolbar", null }, 
			{ "settings.hasstatusbar", null }, 
			{ "settings.haspopupmenu", null }, 
			{ "settings.hastooltip", null }, 
			{ "settings.tooltipdisplayscontent", null }, 
			{ "settings.focusonhover", null }, 
			{ "settings.focus", null }, 
			{ "settings.xmoveto", null }, 
			{ "settings.ymoveto", null }, 
			{ "settings.xshift", null }, 
			{ "settings.yshift", null }, 
			{ "settings.nodebackcolor", null }, 
			{ "settings.nodeforecolor", null }, 
			{ "settings.nodeborder", null }, 
			{ "settings.nodeellipsize", null }, 
			{ "settings.nodelabelmaxlines", null }, 
			{ "settings.nodelabelextralinefactor", null }, 
			{ "settings.nodeimage", null }, 
			{ "settings.treeedgecolor", null }, 
			{ "settings.treeedgehidden", null }, 
			{ "settings.treeedgestroke", null }, 
			{ "settings.treeedgefromterminator", null }, 
			{ "settings.treeedgetoterminator", null }, 
			{ "settings.treeedgeimage", null }, 
			{ "settings.edgecolor", null }, 
			{ "settings.edgearc", null }, 
			{ "settings.edgehidden", null }, 
			{ "settings.edgestroke", null }, 
			{ "settings.edgefromterminator", null }, 
			{ "settings.edgetoterminator", null }, 
			{ "settings.edgeimage", null }, 

			{ "menu.action", null }, 
			{ "menu.label", null }, 
			{ "menu.target", null }, 
			{ "menu.scope", null }, 
			{ "menu.mode", null }, 
			{ "menu.link", null }, 
	};

	/**
	 * Ok button pressed
	 */
	public boolean ok;

	/**
	 * Property file
	 */
	private String propertyUrlString;

	/**
	 * URL combo box
	 */
	private final JTextField uRLField;

	/**
	 * Querier text area
	 */
	private final JTextField userTextField;

	/**
	 * Password field
	 */
	private final JPasswordField passwdField;

	/**
	 * Name table
	 */
	private final JTable nameTable;

	/**
	 * Nodes SQL statement text area
	 */
	private final JTextArea nodesSqlTextArea;

	/**
	 * Tree Edges SQL statement text area
	 */
	private final JTextArea treeEdgesSqlTextArea;

	/**
	 * Edges SQL statement text area
	 */
	private final JTextArea edgesSqlTextArea;

	/**
	 * Settings SQL statement text area
	 */
	private final JTextArea settingsSqlTextArea;

	/**
	 * Constructor
	 *
	 * @param propertyUrlString
	 *        property file string
	 */
	public SqlDialog(final String propertyUrlString)
	{
		super((JFrame) null, true);
		this.ok = false;
		this.propertyUrlString = propertyUrlString;
		this.uRLField = new JTextField();
		this.userTextField = new JTextField();
		this.passwdField = new JPasswordField();
		this.nodesSqlTextArea = new JTextArea();
		this.treeEdgesSqlTextArea = new JTextArea();
		this.edgesSqlTextArea = new JTextArea();
		this.settingsSqlTextArea = new JTextArea();
		this.nameTable = new JTable(this.nameValues, new String[] { Messages.getString("SqlDialog.name"), Messages.getString("SqlDialog.value") });  

		initialize();
		load();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		final JLabel titleLabel = new JLabel(Messages.getString("SqlDialog.title")); 
		titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		final JLabel uRLLabel = new JLabel(Messages.getString("SqlDialog.url")); 
		final JLabel userLabel = new JLabel(Messages.getString("SqlDialog.user")); 
		final JLabel passwdLabel = new JLabel(Messages.getString("SqlDialog.password")); 
		final JLabel sqlLabel = new JLabel(Messages.getString("SqlDialog.sql")); 
		final JLabel mapLabel = new JLabel(Messages.getString("SqlDialog.map")); 

		this.uRLField.setEditable(true);
		this.uRLField.setToolTipText(Messages.getString("SqlDialog.tooltip_url")); 

		this.userTextField.setToolTipText(Messages.getString("SqlDialog.tooltip_user")); 
		this.userTextField.setText(Messages.getString("SqlDialog.prompt_user")); 

		this.passwdField.setToolTipText(null);
		this.passwdField.setText(Messages.getString("SqlDialog.prompt_password")); 

		this.nodesSqlTextArea.setEditable(true);
		this.nodesSqlTextArea.setLineWrap(true);
		this.nodesSqlTextArea.setWrapStyleWord(true);
		this.nodesSqlTextArea.setRequestFocusEnabled(true);
		this.nodesSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqlnodes")); 
		this.nodesSqlTextArea.setText(null);
		this.nodesSqlTextArea.setRows(4);

		this.treeEdgesSqlTextArea.setEditable(true);
		this.treeEdgesSqlTextArea.setLineWrap(true);
		this.treeEdgesSqlTextArea.setWrapStyleWord(true);
		this.treeEdgesSqlTextArea.setRequestFocusEnabled(true);
		this.treeEdgesSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqltreeedges")); 
		this.treeEdgesSqlTextArea.setText(null);
		this.treeEdgesSqlTextArea.setRows(4);

		this.edgesSqlTextArea.setEditable(true);
		this.edgesSqlTextArea.setLineWrap(true);
		this.edgesSqlTextArea.setWrapStyleWord(true);
		this.edgesSqlTextArea.setRequestFocusEnabled(true);
		this.edgesSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqledges")); 
		this.edgesSqlTextArea.setText(null);
		this.edgesSqlTextArea.setRows(4);

		this.settingsSqlTextArea.setEditable(true);
		this.settingsSqlTextArea.setLineWrap(true);
		this.settingsSqlTextArea.setWrapStyleWord(true);
		this.settingsSqlTextArea.setRequestFocusEnabled(true);
		this.settingsSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqlsettings")); 
		this.settingsSqlTextArea.setText(null);
		this.settingsSqlTextArea.setRows(4);

		final JScrollPane nodesSqlScrollPane = new JScrollPane(this.nodesSqlTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollPane treeEdgesSqlScrollPane = new JScrollPane(this.treeEdgesSqlTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollPane edgesSqlScrollPane = new JScrollPane(this.edgesSqlTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollPane settingsSqlScrollPane = new JScrollPane(this.settingsSqlTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		nodesSqlScrollPane.setBorder(null);
		treeEdgesSqlScrollPane.setBorder(null);
		edgesSqlScrollPane.setBorder(null);
		settingsSqlScrollPane.setBorder(null);

		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(Messages.getString("SqlDialog.tab_nodes"), nodesSqlScrollPane); 
		tabbedPane.add(Messages.getString("SqlDialog.tab_treeedges"), treeEdgesSqlScrollPane); 
		tabbedPane.add(Messages.getString("SqlDialog.tab_edges"), edgesSqlScrollPane); 
		tabbedPane.add(Messages.getString("SqlDialog.tab_settings"), settingsSqlScrollPane); 

		this.nameTable.setToolTipText(Messages.getString("SqlDialog.tooltip_value")); 
		final JScrollPane mapsSqlScrollPane = new JScrollPane(this.nameTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mapsSqlScrollPane.setPreferredSize(new Dimension(500, 100));

		final JButton okButton = new JButton();
		okButton.setText(Messages.getString("SqlDialog.ok")); 
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		okButton.addActionListener(e -> {
			SqlDialog.this.ok = true;
			setVisible(false);
		});

		final JButton cancelButton = new JButton();
		cancelButton.setText(Messages.getString("SqlDialog.cancel")); 
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		cancelButton.addActionListener(e -> setVisible(false));

		final JButton loadButton = new JButton();
		loadButton.setText(Messages.getString("SqlDialog.load")); 
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		loadButton.addActionListener(e -> {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter()
			{
				/*
				 * (non-Javadoc)
				 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
				 */
				@Override
				public boolean accept(final File file)
				{
					return file.getName().toLowerCase().endsWith(".properties") || file.isDirectory(); 
				}

				/*
				 * (non-Javadoc)
				 * @see javax.swing.filechooser.FileFilter#getDescription()
				 */
				@Override
				public String getDescription()
				{
					return Messages.getString("SqlDialog.propertyfiles"); 
				}
			});
			fileChooser.setDialogTitle(Messages.getString("SqlDialog.title_choose")); 
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File(".")); 
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					SqlDialog.this.propertyUrlString = fileChooser.getSelectedFile().toURI().toURL().toString();
					load();
				}
				catch (final MalformedURLException exception)
				{
					// do nothing
				}
			}
		});
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(loadButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 20, 5), 0, 0));

		panel.add(uRLLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(userLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 1));
		panel.add(passwdLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(sqlLabel, new GridBagConstraints(0, 5, 1, 5, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(mapLabel, new GridBagConstraints(0, 20, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		panel.add(this.uRLField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(this.userTextField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(this.passwdField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 200, 0));
		panel.add(tabbedPane, new GridBagConstraints(1, 5, 1, 5, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(mapsSqlScrollPane, new GridBagConstraints(1, 20, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		panel.add(buttonPanel, new GridBagConstraints(0, 25, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		setContentPane(panel);
	}

	/**
	 * Get properties
	 *
	 * @return properties
	 */
	public Properties getProperties()
	{
		final Properties properties = new Properties();
		properties.put("url", this.uRLField.getText() == null ? "" : this.uRLField.getText());  
		properties.put("user", this.userTextField.getText()); 
		properties.put("passwd", new String(this.passwdField.getPassword())); 
		properties.put("nodesSql", this.nodesSqlTextArea.getText()); 
		properties.put("treeedgesSql", this.treeEdgesSqlTextArea.getText()); 
		properties.put("edgesSql", this.edgesSqlTextArea.getText()); 
		properties.put("settingsSql", this.settingsSqlTextArea.getText()); 
		final TableModel model = this.nameTable.getModel();
		int rowIndex = 0;
		for (final String[] row : this.nameValues)
		{
			final String key = row[0];
			final String value = (String) model.getValueAt(rowIndex, 1);
			if (value != null)
			{
				properties.setProperty(key, value);
			}
			rowIndex++;
		}
		return properties;
	}

	/**
	 * Set properties
	 *
	 * @param properties
	 *        properties
	 */
	public void setProperties(final Properties properties)
	{
		this.uRLField.setText((String) properties.get("url")); 
		this.userTextField.setText((String) properties.get("user")); 
		this.passwdField.setText((String) properties.get("passwd")); 
		this.nodesSqlTextArea.setText((String) properties.get("nodesSql")); 
		this.treeEdgesSqlTextArea.setText((String) properties.get("treeEdgesSql")); 
		this.edgesSqlTextArea.setText((String) properties.get("edgesSql")); 
		this.settingsSqlTextArea.setText((String) properties.get("settingsSql")); 
		final TableModel model = this.nameTable.getModel();
		int rowIndex = 0;
		for (final String[] row : this.nameValues)
		{
			final String key = row[0];
			final String value = properties.getProperty(key);
			model.setValueAt(value, rowIndex, 1);
			rowIndex++;
		}
	}

	/**
	 * Load properties
	 */
	public void load()
	{
		if (this.propertyUrlString == null)
			return;

		try
		{
			final URL url = new URL(this.propertyUrlString);
			final Properties properties = SqlProperties.load(url);
			if (properties != null)
			{
				setProperties(properties);
			}
		}
		catch (final MalformedURLException exception)
		{
			// do nothing
		}
	}

	/**
	 * Save properties
	 */
	public void save()
	{
		if (this.propertyUrlString == null)
			return;
		try
		{
			final File file = new File(new URL(this.propertyUrlString).toURI());
			final Properties properties = getProperties();
			SqlProperties.save(properties, file.getAbsolutePath());
		}
		catch (final MalformedURLException | URISyntaxException exception)
		{
			// do nothing
		}
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
			pack();
			SqlDialog.center(this);
		}
		super.setVisible(flag);
	}

	/**
	 * Center on screen
	 *
	 * @param component
	 *        component to center
	 */
	static public void center(final Component component)
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension componentSize = component.getSize();
		if (componentSize.height > screenSize.height)
		{
			componentSize.height = screenSize.height;
		}
		if (componentSize.width > screenSize.width)
		{
			componentSize.width = screenSize.width;
		}
		component.setLocation((screenSize.width - componentSize.width) / 2, (screenSize.height - componentSize.height) / 2);
	}

	/**
	 * Main
	 *
	 * @param args
	 *        arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false); 
		final SqlDialog dialog = new SqlDialog(args.length > 0 ? args[0] : null);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.ok)
		{
			dialog.save();
		}
		System.exit(0);
	}
}
