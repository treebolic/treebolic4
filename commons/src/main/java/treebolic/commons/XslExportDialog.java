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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * Export dialog
 *
 * @author Bernard Bou
 */
public class XslExportDialog extends JDialog
{
	static private final long serialVersionUID = 1L;

	static private String[] viewOutputs = { "view:xml", "view:html", "view:text" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// V A L U E S

	/**
	 * Properties (input/output)
	 */
	protected Properties properties;

	/**
	 * Ok result
	 */
	public boolean ok;

	// C O M P O N E N T S

	/**
	 * Export text
	 */
	private JComboBox<String> exportComboBox;

	/**
	 * XSL text
	 */
	private JComboBox<String> xslComboBox;

	/**
	 * Data panel
	 */
	protected JPanel dataPanel;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param properties
	 *        settings
	 */
	public XslExportDialog(final Properties properties)
	{
		super();
		this.properties = properties;

		final List<String> presetXsls = new ArrayList<String>();
		for (final String url : Searcher.findFileUrls(".*treebolic2.*\\.xsl$")) //$NON-NLS-1$
		{
			presetXsls.add(url);
		}

		final String xSLsProperty = this.properties.getProperty("exportxsls"); //$NON-NLS-1$
		if (xSLsProperty != null)
		{
			final String[] providers = xSLsProperty.split(":"); //$NON-NLS-1$
			for (final String provider : providers)
			{
				presetXsls.add(provider);
			}
		}

		initialize(presetXsls, XslExportDialog.viewOutputs);
	}

	/**
	 * Initialize
	 */
	protected void initialize(final List<String> presetXsls, final String[] viewOutputs)
	{
		setTitle(Messages.getString("XslExportDialog.title")); //$NON-NLS-1$
		setResizable(true);

		// images
		final Icon icon = new ImageIcon(XslExportDialog.class.getResource("images/xsl.png")); //$NON-NLS-1$
		final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("XslExportDialog.header")); //$NON-NLS-1$
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// labels
		final JLabel exportLabel = new JLabel(Messages.getString("XslExportDialog.output")); //$NON-NLS-1$
		final JLabel xslLabel = new JLabel(Messages.getString("XslExportDialog.xsl")); //$NON-NLS-1$

		// text
		final ListCellRenderer<Object> renderer = new DefaultListCellRenderer()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = -3981888637610754743L;

			/*
			 * (non-Javadoc)
			 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
			 */
			@Override
			public Component getListCellRendererComponent(final JList<? extends Object> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				String string = (String) value;
				if (string != null)
				{
					final int position = string.lastIndexOf('/');
					if (position != -1)
					{
						string = string.substring(position + 1);
					}
				}
				return super.getListCellRendererComponent(list, string, index, isSelected, cellHasFocus);
			}
		};
		this.exportComboBox = new JComboBox<>(viewOutputs);
		this.exportComboBox.setEditable(true);
		this.exportComboBox.setRenderer(renderer);
		this.exportComboBox.setPreferredSize(new Dimension(300, 24));
		this.xslComboBox = new JComboBox<>();
		this.xslComboBox.setEditable(true);
		this.xslComboBox.setRenderer(renderer);
		this.xslComboBox.setPreferredSize(new Dimension(300, 24));
		for (final String item : presetXsls)
		{
			this.xslComboBox.addItem(item);
		}

		// tooltips
		this.exportComboBox.setToolTipText(Messages.getString("XslExportDialog.tooltip_xml")); //$NON-NLS-1$
		this.xslComboBox.setToolTipText(Messages.getString("XslExportDialog.tooltip_xsl")); //$NON-NLS-1$

		// buttons
		final JButton xmlBrowseButton = new JButton(Messages.getString("XslExportDialog.browse")); //$NON-NLS-1$
		final JButton xslBrowseButton = new JButton(Messages.getString("XslExportDialog.browse")); //$NON-NLS-1$
		final JButton oKButton = new JButton(Messages.getString("XslExportDialog.ok")); //$NON-NLS-1$
		final JButton cancelButton = new JButton(Messages.getString("XslExportDialog.cancel")); //$NON-NLS-1$

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(xslLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(exportLabel, new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(this.xslComboBox, new GridBagConstraints(0, 1, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(this.exportComboBox, new GridBagConstraints(0, 3, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(xslBrowseButton, new GridBagConstraints(1, 1, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		this.dataPanel.add(xmlBrowseButton, new GridBagConstraints(1, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// action
		xmlBrowseButton.addActionListener(new ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent event)
			{
				final String url = FileDialogs.getXmlUrl(XslExportDialog.this.properties.getProperty("base", ".")); //$NON-NLS-1$ //$NON-NLS-2$
				if (url != null && !url.isEmpty())
				{
					XslExportDialog.this.exportComboBox.getEditor().setItem(url);
				}
			}
		});
		xslBrowseButton.addActionListener(new ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent event)
			{
				final String url = FileDialogs.getXslUrl(XslExportDialog.this.properties.getProperty("base", ".")); //$NON-NLS-1$ //$NON-NLS-2$
				if (url != null && !url.isEmpty())
				{
					XslExportDialog.this.xslComboBox.getEditor().setItem(url);
				}
			}
		});
		oKButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent event)
			{
				XslExportDialog.this.ok = true;
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent event)
			{
				setVisible(false);
			}
		});

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(headerLabel);
		panel.add(this.dataPanel);
		panel.add(buttonPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			// read properties into components
			final String exportValue = this.properties.getProperty("exporturl"); //$NON-NLS-1$
			this.exportComboBox.addItem(exportValue);
			this.exportComboBox.getEditor().setItem(exportValue);

			final String xslValue = this.properties.getProperty("exportxsl"); //$NON-NLS-1$
			this.xslComboBox.addItem(xslValue);
			this.xslComboBox.getEditor().setItem(xslValue);

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				this.properties.setProperty("exporturl", (String) this.exportComboBox.getEditor().getItem()); //$NON-NLS-1$
				this.properties.setProperty("exportxsl", (String) this.xslComboBox.getEditor().getItem()); //$NON-NLS-1$
			}
		}
		super.setVisible(flag);
	}
}
