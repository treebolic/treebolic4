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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
			{ "nodes.id", null }, //$NON-NLS-1$
			{ "nodes.label", null }, //$NON-NLS-1$
			{ "nodes.content", null }, //$NON-NLS-1$
			{ "nodes.backcolor", null }, //$NON-NLS-1$
			{ "nodes.forecolor", null }, //$NON-NLS-1$
			{ "nodes.image", null }, //$NON-NLS-1$
			{ "nodes.link", null }, //$NON-NLS-1$
			{ "nodes.target", null }, //$NON-NLS-1$
			{ "nodes.weight", null }, //$NON-NLS-1$
			{ "nodes.mountpoint", null }, //$NON-NLS-1$
			{ "nodes.mountnow", null }, //$NON-NLS-1$

			{ "edges.istree", null }, //$NON-NLS-1$
			{ "edges.from", null }, //$NON-NLS-1$
			{ "edges.to", null }, //$NON-NLS-1$
			{ "edges.label", null }, //$NON-NLS-1$
			{ "edges.image", null }, //$NON-NLS-1$
			{ "edges.color", null }, //$NON-NLS-1$
			{ "edges.hidden", null }, //$NON-NLS-1$
			{ "edges.stroke", null }, //$NON-NLS-1$
			{ "edges.fromterminator", null }, //$NON-NLS-1$
			{ "edges.toterminator", null }, //$NON-NLS-1$

			{ "settings.backimage", null }, //$NON-NLS-1$
			{ "settings.backcolor", null }, //$NON-NLS-1$
			{ "settings.forecolor", null }, //$NON-NLS-1$
			{ "settings.fontface", null }, //$NON-NLS-1$
			{ "settings.fontsize", null }, //$NON-NLS-1$
			{ "settings.scalefonts", null }, //$NON-NLS-1$
			{ "settings.fontscaler", null }, //$NON-NLS-1$
			{ "settings.scaleimages", null }, //$NON-NLS-1$
			{ "settings.imagescaler", null }, //$NON-NLS-1$
			{ "settings.orientation", null }, //$NON-NLS-1$
			{ "settings.expansion", null }, //$NON-NLS-1$
			{ "settings.sweep", null }, //$NON-NLS-1$
			{ "settings.preserveorientation", null }, //$NON-NLS-1$
			{ "settings.hastoolbar", null }, //$NON-NLS-1$
			{ "settings.hasstatusbar", null }, //$NON-NLS-1$
			{ "settings.haspopupmenu", null }, //$NON-NLS-1$
			{ "settings.hastooltip", null }, //$NON-NLS-1$
			{ "settings.tooltipdisplayscontent", null }, //$NON-NLS-1$
			{ "settings.focusonhover", null }, //$NON-NLS-1$
			{ "settings.focus", null }, //$NON-NLS-1$
			{ "settings.xmoveto", null }, //$NON-NLS-1$
			{ "settings.ymoveto", null }, //$NON-NLS-1$
			{ "settings.xshift", null }, //$NON-NLS-1$
			{ "settings.yshift", null }, //$NON-NLS-1$
			{ "settings.nodebackcolor", null }, //$NON-NLS-1$
			{ "settings.nodeforecolor", null }, //$NON-NLS-1$
			{ "settings.nodeborder", null }, //$NON-NLS-1$
			{ "settings.nodeellipsize", null }, //$NON-NLS-1$
			{ "settings.nodelabelmaxlines", null }, //$NON-NLS-1$
			{ "settings.nodelabelextralinefactor", null }, //$NON-NLS-1$
			{ "settings.nodeimage", null }, //$NON-NLS-1$
			{ "settings.treeedgecolor", null }, //$NON-NLS-1$
			{ "settings.treeedgehidden", null }, //$NON-NLS-1$
			{ "settings.treeedgestroke", null }, //$NON-NLS-1$
			{ "settings.treeedgefromterminator", null }, //$NON-NLS-1$
			{ "settings.treeedgetoterminator", null }, //$NON-NLS-1$
			{ "settings.treeedgeimage", null }, //$NON-NLS-1$
			{ "settings.edgecolor", null }, //$NON-NLS-1$
			{ "settings.edgearc", null }, //$NON-NLS-1$
			{ "settings.edgehidden", null }, //$NON-NLS-1$
			{ "settings.edgestroke", null }, //$NON-NLS-1$
			{ "settings.edgefromterminator", null }, //$NON-NLS-1$
			{ "settings.edgetoterminator", null }, //$NON-NLS-1$
			{ "settings.edgeimage", null }, //$NON-NLS-1$

			{ "menu.action", null }, //$NON-NLS-1$
			{ "menu.label", null }, //$NON-NLS-1$
			{ "menu.target", null }, //$NON-NLS-1$
			{ "menu.scope", null }, //$NON-NLS-1$
			{ "menu.mode", null }, //$NON-NLS-1$
			{ "menu.link", null }, //$NON-NLS-1$
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
		this.nameTable = new JTable(this.nameValues, new String[] { Messages.getString("SqlDialog.name"), Messages.getString("SqlDialog.value") }); //$NON-NLS-1$ //$NON-NLS-2$

		initialize();
		load();
	}

	/**
	 * Initialize
	 *
	 * @throws Exception
	 */
	private void initialize()
	{
		final JLabel titleLabel = new JLabel(Messages.getString("SqlDialog.title")); //$NON-NLS-1$
		titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		final JLabel uRLLabel = new JLabel(Messages.getString("SqlDialog.url")); //$NON-NLS-1$
		final JLabel userLabel = new JLabel(Messages.getString("SqlDialog.user")); //$NON-NLS-1$
		final JLabel passwdLabel = new JLabel(Messages.getString("SqlDialog.password")); //$NON-NLS-1$
		final JLabel sqlLabel = new JLabel(Messages.getString("SqlDialog.sql")); //$NON-NLS-1$
		final JLabel mapLabel = new JLabel(Messages.getString("SqlDialog.map")); //$NON-NLS-1$

		this.uRLField.setEditable(true);
		this.uRLField.setToolTipText(Messages.getString("SqlDialog.tooltip_url")); //$NON-NLS-1$

		this.userTextField.setToolTipText(Messages.getString("SqlDialog.tooltip_user")); //$NON-NLS-1$
		this.userTextField.setText(Messages.getString("SqlDialog.prompt_user")); //$NON-NLS-1$

		this.passwdField.setToolTipText(null);
		this.passwdField.setText(Messages.getString("SqlDialog.prompt_password")); //$NON-NLS-1$

		this.nodesSqlTextArea.setEditable(true);
		this.nodesSqlTextArea.setLineWrap(true);
		this.nodesSqlTextArea.setWrapStyleWord(true);
		this.nodesSqlTextArea.setRequestFocusEnabled(true);
		this.nodesSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqlnodes")); //$NON-NLS-1$
		this.nodesSqlTextArea.setText(null);
		this.nodesSqlTextArea.setRows(4);

		this.treeEdgesSqlTextArea.setEditable(true);
		this.treeEdgesSqlTextArea.setLineWrap(true);
		this.treeEdgesSqlTextArea.setWrapStyleWord(true);
		this.treeEdgesSqlTextArea.setRequestFocusEnabled(true);
		this.treeEdgesSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqltreeedges")); //$NON-NLS-1$
		this.treeEdgesSqlTextArea.setText(null);
		this.treeEdgesSqlTextArea.setRows(4);

		this.edgesSqlTextArea.setEditable(true);
		this.edgesSqlTextArea.setLineWrap(true);
		this.edgesSqlTextArea.setWrapStyleWord(true);
		this.edgesSqlTextArea.setRequestFocusEnabled(true);
		this.edgesSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqledges")); //$NON-NLS-1$
		this.edgesSqlTextArea.setText(null);
		this.edgesSqlTextArea.setRows(4);

		this.settingsSqlTextArea.setEditable(true);
		this.settingsSqlTextArea.setLineWrap(true);
		this.settingsSqlTextArea.setWrapStyleWord(true);
		this.settingsSqlTextArea.setRequestFocusEnabled(true);
		this.settingsSqlTextArea.setToolTipText(Messages.getString("SqlDialog.tooltip_sqlsettings")); //$NON-NLS-1$
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
		tabbedPane.add(Messages.getString("SqlDialog.tab_nodes"), nodesSqlScrollPane); //$NON-NLS-1$
		tabbedPane.add(Messages.getString("SqlDialog.tab_treeedges"), treeEdgesSqlScrollPane); //$NON-NLS-1$
		tabbedPane.add(Messages.getString("SqlDialog.tab_edges"), edgesSqlScrollPane); //$NON-NLS-1$
		tabbedPane.add(Messages.getString("SqlDialog.tab_settings"), settingsSqlScrollPane); //$NON-NLS-1$

		this.nameTable.setToolTipText(Messages.getString("SqlDialog.tooltip_value")); //$NON-NLS-1$
		final JScrollPane mapsSqlScrollPane = new JScrollPane(this.nameTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mapsSqlScrollPane.setPreferredSize(new Dimension(500, 100));

		final JButton okButton = new JButton();
		okButton.setText(Messages.getString("SqlDialog.ok")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				SqlDialog.this.ok = true;
				setVisible(false);
			}
		});

		final JButton cancelButton = new JButton();
		cancelButton.setText(Messages.getString("SqlDialog.cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				setVisible(false);
			}
		});

		final JButton loadButton = new JButton();
		loadButton.setText(Messages.getString("SqlDialog.load")); //$NON-NLS-1$
		loadButton.addActionListener(new ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			@SuppressWarnings("synthetic-access")
			public void actionPerformed(final ActionEvent e)
			{
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
						return file.getName().toLowerCase().endsWith(".properties") || file.isDirectory(); //$NON-NLS-1$
					}

					/*
					 * (non-Javadoc)
					 * @see javax.swing.filechooser.FileFilter#getDescription()
					 */
					@Override
					public String getDescription()
					{
						return Messages.getString("SqlDialog.propertyfiles"); //$NON-NLS-1$
					}
				});
				fileChooser.setDialogTitle(Messages.getString("SqlDialog.title_choose")); //$NON-NLS-1$
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setCurrentDirectory(new File(".")); //$NON-NLS-1$
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
		properties.put("url", this.uRLField.getText() == null ? "" : this.uRLField.getText()); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("user", this.userTextField.getText()); //$NON-NLS-1$
		properties.put("passwd", new String(this.passwdField.getPassword())); //$NON-NLS-1$
		properties.put("nodesSql", this.nodesSqlTextArea.getText()); //$NON-NLS-1$
		properties.put("treeedgesSql", this.treeEdgesSqlTextArea.getText()); //$NON-NLS-1$
		properties.put("edgesSql", this.edgesSqlTextArea.getText()); //$NON-NLS-1$
		properties.put("settingsSql", this.settingsSqlTextArea.getText()); //$NON-NLS-1$
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
		this.uRLField.setText((String) properties.get("url")); //$NON-NLS-1$
		this.userTextField.setText((String) properties.get("user")); //$NON-NLS-1$
		this.passwdField.setText((String) properties.get("passwd")); //$NON-NLS-1$
		this.nodesSqlTextArea.setText((String) properties.get("nodesSql")); //$NON-NLS-1$
		this.treeEdgesSqlTextArea.setText((String) properties.get("treeEdgesSql")); //$NON-NLS-1$
		this.edgesSqlTextArea.setText((String) properties.get("edgesSql")); //$NON-NLS-1$
		this.settingsSqlTextArea.setText((String) properties.get("settingsSql")); //$NON-NLS-1$
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
		catch (final MalformedURLException exception)
		{
			// do nothing
		}
		catch (final URISyntaxException exception)
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
		UIManager.put("swing.boldMetal", false); //$NON-NLS-1$
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
