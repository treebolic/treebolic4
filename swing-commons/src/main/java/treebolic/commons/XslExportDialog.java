/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Export dialog
 *
 * @author Bernard Bou
 */
public class XslExportDialog extends JDialog
{
	static private final String[] viewOutputs = {"view:xml", "view:html", "view:text"};

	// V A L U E S

	/**
	 * Properties (input/output)
	 */
	protected final Properties properties;

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
	 * @param properties settings
	 */
	public XslExportDialog(final Properties properties)
	{
		super();
		this.properties = properties;


		@NonNull final List<String> presetXsls = new ArrayList<>(Searcher.findFileUrls(".*treebolic2.*\\.xsl$"));

		final String xSLsProperty = this.properties.getProperty("exportxsls");
		if (xSLsProperty != null)
		{
			@NonNull final String[] providers = xSLsProperty.split(":");
			Collections.addAll(presetXsls, providers);
		}

		initialize(presetXsls, XslExportDialog.viewOutputs);
	}

	/**
	 * Initialize
	 *
	 * @param presetXsls  preset xslt
	 * @param viewOutputs view outputs
	 */
	protected void initialize(@NonNull final List<String> presetXsls, @NonNull @SuppressWarnings("SameParameterValue") final String[] viewOutputs)
	{
		setTitle(Messages.getString("XslExportDialog.title"));
		setResizable(true);

		// images
		@SuppressWarnings("DataFlowIssue") @NonNull final Icon icon = new ImageIcon(XslExportDialog.class.getResource("images/xsl.png"));
		@NonNull final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("XslExportDialog.header"));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// labels
		@NonNull final JLabel exportLabel = new JLabel(Messages.getString("XslExportDialog.output"));
		@NonNull final JLabel xslLabel = new JLabel(Messages.getString("XslExportDialog.xsl"));

		// text
		@NonNull final ListCellRenderer<Object> renderer = new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
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
		this.exportComboBox.setToolTipText(Messages.getString("XslExportDialog.tooltip_xml"));
		this.xslComboBox.setToolTipText(Messages.getString("XslExportDialog.tooltip_xsl"));

		// buttons
		@NonNull final JButton xmlBrowseButton = new JButton(Messages.getString("XslExportDialog.browse"));
		@NonNull final JButton xslBrowseButton = new JButton(Messages.getString("XslExportDialog.browse"));
		@NonNull final JButton oKButton = new JButton(Messages.getString("XslExportDialog.ok"));
		@NonNull final JButton cancelButton = new JButton(Messages.getString("XslExportDialog.cancel"));

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(xslLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(exportLabel, new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(this.xslComboBox, new GridBagConstraints(0, 1, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(this.exportComboBox, new GridBagConstraints(0, 3, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(xslBrowseButton, new GridBagConstraints(1, 1, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		this.dataPanel.add(xmlBrowseButton, new GridBagConstraints(1, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));

		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// action
		xmlBrowseButton.addActionListener(event -> {
			@Nullable final String url = FileDialogs.getXmlUrl(XslExportDialog.this.properties.getProperty("base", "."));
			if (url != null && !url.isEmpty())
			{
				XslExportDialog.this.exportComboBox.getEditor().setItem(url);
			}
		});
		xslBrowseButton.addActionListener(event -> {
			@Nullable final String url = FileDialogs.getXslUrl(XslExportDialog.this.properties.getProperty("base", "."));
			if (url != null && !url.isEmpty())
			{
				XslExportDialog.this.xslComboBox.getEditor().setItem(url);
			}
		});
		oKButton.addActionListener(event -> {
			XslExportDialog.this.ok = true;
			setVisible(false);
		});
		cancelButton.addActionListener(event -> setVisible(false));

		// assemble
		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(headerLabel);
		panel.add(this.dataPanel);
		panel.add(buttonPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			// read properties into components
			final String exportValue = this.properties.getProperty("exporturl");
			this.exportComboBox.addItem(exportValue);
			this.exportComboBox.getEditor().setItem(exportValue);

			final String xslValue = this.properties.getProperty("exportxsl");
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
				this.properties.setProperty("exporturl", (String) this.exportComboBox.getEditor().getItem());
				this.properties.setProperty("exportxsl", (String) this.xslComboBox.getEditor().getItem());
			}
		}
		super.setVisible(flag);
	}
}
