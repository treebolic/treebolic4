/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class XslImportDialog extends JDialog
{
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
	 * Import text
	 */
	private JTextField importTextField;

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
	public XslImportDialog(final Properties properties)
	{
		super();
		this.properties = properties;

		@NonNull final List<String> presetXsls = new ArrayList<>(Searcher.findFileUrls(".*2treebolic.*\\.xsl$"));
		initialize(presetXsls);
	}

	/**
	 * Initialize
	 *
	 * @param presetXslts preset xslt
	 */
	protected void initialize(@NonNull final List<String> presetXslts)
	{
		setTitle(Messages.getString("XslImportDialog.title"));
		setResizable(true);

		// images
		@SuppressWarnings("DataFlowIssue") @NonNull final Icon icon = new ImageIcon(XslImportDialog.class.getResource("images/xsl.png"));
		@NonNull final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("XslImportDialog.header"));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// labels
		@NonNull final JLabel importLabel = new JLabel(Messages.getString("XslImportDialog.input"));
		@NonNull final JLabel xslLabel = new JLabel(Messages.getString("XslImportDialog.xsl"));

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
		this.importTextField = new JTextField(32);
		this.importTextField.setPreferredSize(new Dimension(300, 24));
		this.xslComboBox = new JComboBox<>();
		this.xslComboBox.setEditable(true);
		this.xslComboBox.setRenderer(renderer);
		this.xslComboBox.setPreferredSize(new Dimension(300, 24));
		for (final String item : presetXslts)
		{
			this.xslComboBox.addItem(item);
		}

		// tooltips
		this.importTextField.setToolTipText(Messages.getString("XslImportDialog.tooltip_xml"));
		this.xslComboBox.setToolTipText(Messages.getString("XslImportDialog.tooltip_xsl"));

		// buttons
		@NonNull final JButton importBrowseButton = new JButton(Messages.getString("XslImportDialog.browse"));
		@NonNull final JButton xslBrowseButton = new JButton(Messages.getString("XslImportDialog.browse"));
		@NonNull final JButton oKButton = new JButton(Messages.getString("XslImportDialog.ok"));
		@NonNull final JButton cancelButton = new JButton(Messages.getString("XslImportDialog.cancel"));

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(importLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(xslLabel, new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(this.importTextField, new GridBagConstraints(0, 1, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(this.xslComboBox, new GridBagConstraints(0, 3, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(importBrowseButton, new GridBagConstraints(1, 1, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		this.dataPanel.add(xslBrowseButton, new GridBagConstraints(1, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));

		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// action
		importBrowseButton.addActionListener(event -> {
			@Nullable final String url = FileDialogs.getXmlUrl(XslImportDialog.this.properties.getProperty("base", "."));
			if (url != null && !url.isEmpty())
			{
				XslImportDialog.this.importTextField.setText(url);
			}
		});
		xslBrowseButton.addActionListener(event -> {
			@Nullable final String url = FileDialogs.getXslUrl(XslImportDialog.this.properties.getProperty("base", "."));
			if (url != null && !url.isEmpty())
			{
				XslImportDialog.this.xslComboBox.getEditor().setItem(url);
			}
		});
		oKButton.addActionListener(event -> {
			XslImportDialog.this.ok = true;
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
			this.importTextField.setText(this.properties.getProperty("importurl"));
			final String value = this.properties.getProperty("importxsl");
			this.xslComboBox.addItem(value);
			this.xslComboBox.getEditor().setItem(value);

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				this.properties.setProperty("importurl", this.importTextField.getText());
				this.properties.setProperty("importxsl", (String) this.xslComboBox.getEditor().getItem());
			}
		}
		super.setVisible(flag);
	}
}
