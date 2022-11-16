/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Search dialog, derived from JDialog
 *
 * @author Bernard Bou
 */
class SearchDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// SCOPE

	static private final String[] scopeLabels = new String[] { Messages.getString("SearchDialog.label"), Messages.getString("SearchDialog.content"), Messages.getString("SearchDialog.link"), Messages.getString("SearchDialog.id"), };

	// to avoid dependency on SearchScope
	static private final Object[] scopeValues = new String[] { "LABEL", "CONTENT", "LINK", "ID" };

	/**
	 * Icon array
	 */
	@SuppressWarnings("ConstantConditions")
	static final ImageIcon[] scopeIcons = new ImageIcon[] { new ImageIcon(SearchDialog.class.getResource("images/search_label.png")),
			new ImageIcon(SearchDialog.class.getResource("images/search_content.png")), 
			new ImageIcon(SearchDialog.class.getResource("images/search_link.png")), 
			new ImageIcon(SearchDialog.class.getResource("images/search_id.png")), 
	};

	// MODE

	static private final String[] modeLabels = new String[] { Messages.getString("SearchDialog.startswith"), Messages.getString("SearchDialog.equals"), Messages.getString("SearchDialog.includes") };

	// to avoid dependency on SearchMode
	static private final Object[] modeValues = new String[] { "STARTSWITH", "EQUALS", "INCLUDES" };

	/**
	 * Icon array
	 */
	@SuppressWarnings("ConstantConditions")
	static final ImageIcon[] modeIcons = new ImageIcon[] { new ImageIcon(SearchDialog.class.getResource("images/search_startswith.png")),
			new ImageIcon(SearchDialog.class.getResource("images/search_equals.png")), 
			new ImageIcon(SearchDialog.class.getResource("images/search_includes.png")), 
	};

	// V A L U E S

	/**
	 * Value
	 */
	public String scopeValue;

	/**
	 * Value
	 */
	public String modeValue;

	/**
	 * Ok result
	 */
	public boolean ok;

	// C O M P O N E N T S

	/**
	 * Buttons
	 */
	protected final JRadioButton[] scopeButtons;

	/**
	 * Buttons
	 */
	protected final JRadioButton[] modeButtons;

	/**
	 * Data panel
	 */
	protected final JPanel dataPanel;

	/**
	 * Scope listener
	 */
	protected final ActionListener scopeListener = event -> SearchDialog.this.scopeValue = event.getActionCommand();

	/**
	 * Mode listener
	 */
	protected final ActionListener modeListener = event -> SearchDialog.this.modeValue = event.getActionCommand();

	/**
	 * Constructor
	 *
	 * @param scopeValue0
	 *        initial scope value
	 * @param modeValue0
	 *        initial mode value
	 * @param title
	 *        title
	 * @param label
	 *        label
	 */
	public SearchDialog(final String scopeValue0, final String modeValue0, final String title, final String label)
	{
		super();
		this.scopeValue = scopeValue0;
		this.modeValue = modeValue0;

		setTitle(title);
		setResizable(true);

		// label
		final JLabel headerLabel = new JLabel();
		headerLabel.setText(label);

		// scope buttons
		final ButtonGroup scopeGroup = new ButtonGroup();
		this.scopeButtons = new JRadioButton[scopeValues.length];
		for (int i = 0; i < scopeValues.length; i++)
		{
			this.scopeButtons[i] = new JRadioButton(scopeLabels[i]);
			this.scopeButtons[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			this.scopeButtons[i].setActionCommand(scopeValues[i].toString());
			this.scopeButtons[i].addActionListener(this.scopeListener);
			this.scopeButtons[i].setSelected(scopeValues[i].equals(scopeValue0));
			scopeGroup.add(this.scopeButtons[i]);
		}

		// mode buttons
		final ButtonGroup modeGroup = new ButtonGroup();
		this.modeButtons = new JRadioButton[modeValues.length];
		for (int j = 0; j < modeValues.length; j++)
		{
			this.modeButtons[j] = new JRadioButton(modeLabels[j]);
			this.modeButtons[j].setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.modeButtons[j].setActionCommand(modeValues[j].toString());
			this.modeButtons[j].addActionListener(this.modeListener);
			this.modeButtons[j].setSelected(modeValues[j].equals(modeValue0));
			modeGroup.add(this.modeButtons[j]);
		}

		// buttons
		final JButton oKButton = new JButton(Messages.getString("SearchDialog.ok")); 
		final JButton cancelButton = new JButton(Messages.getString("SearchDialog.cancel")); 

		// buttons panel
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(headerLabel, new GridBagConstraints(0, 1, 4, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 20), 0, 20));
		for (int i = 0; i < this.scopeButtons.length; i++)
		{
			this.dataPanel.add(this.scopeButtons[i], new GridBagConstraints(0, 2 + i, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
			this.dataPanel.add(new JLabel(scopeIcons[i]), new GridBagConstraints(1, 2 + i, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
		}
		for (int j = 0; j < this.modeButtons.length; j++)
		{
			this.dataPanel.add(new JLabel(modeIcons[j]), new GridBagConstraints(2, 2 + j, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
			this.dataPanel.add(this.modeButtons[j], new GridBagConstraints(3, 2 + j, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
		}

		// command panel
		final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(cancelButton);
		commandPanel.add(oKButton);

		oKButton.addActionListener(event -> {
			SearchDialog.this.ok = true;
			setVisible(false);
		});
		cancelButton.addActionListener(event -> setVisible(false));

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 20)));
		panel.add(this.dataPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(commandPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 20)));

		setContentPane(panel);
	}

	/**
	 * Center on screen
	 */
	public void center()
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension componentSize = getSize();
		if (componentSize.height > screenSize.height)
		{
			componentSize.height = screenSize.height;
		}
		if (componentSize.width > screenSize.width)
		{
			componentSize.width = screenSize.width;
		}
		setLocation((screenSize.width - componentSize.width) / 2, (screenSize.height - componentSize.height) / 2);
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

			pack();
			center();
		}
		super.setVisible(flag);
	}
}
