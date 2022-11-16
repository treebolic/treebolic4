/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 */
package treebolic.glue.component;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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
 * Import dialog
 *
 * @author Bernard Bou
 */
public class SearchDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// SCOPE

	static private String[] scopeLabels = new String[] { Messages.getString("SearchDialog.label"), Messages.getString("SearchDialog.content"), Messages.getString("SearchDialog.link"), Messages.getString("SearchDialog.id"), }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	// to avoid dependency on SearchScope
	static private Object[] scopeValues = new String[] { "LABEL", "CONTENT", "LINK", "ID" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/**
	 * Icon array
	 */
	static ImageIcon[] scopeIcons = new ImageIcon[] { new ImageIcon(SearchDialog.class.getResource("images/search_label.png")), //$NON-NLS-1$
			new ImageIcon(SearchDialog.class.getResource("images/search_content.png")), //$NON-NLS-1$
			new ImageIcon(SearchDialog.class.getResource("images/search_link.png")), //$NON-NLS-1$
			new ImageIcon(SearchDialog.class.getResource("images/search_id.png")), //$NON-NLS-1$
	};

	// MODE

	static private String[] modeLabels = new String[] { Messages.getString("SearchDialog.startswith"), Messages.getString("SearchDialog.equals"), Messages.getString("SearchDialog.includes") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// to avoid dependency on SearchMode
	static private Object[] modeValues = new String[] { "STARTSWITH", "EQUALS", "INCLUDES" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * Icon array
	 */
	static ImageIcon[] modeIcons = new ImageIcon[] { new ImageIcon(SearchDialog.class.getResource("images/search_startswith.png")), //$NON-NLS-1$
			new ImageIcon(SearchDialog.class.getResource("images/search_equals.png")), //$NON-NLS-1$
			new ImageIcon(SearchDialog.class.getResource("images/search_includes.png")), //$NON-NLS-1$
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
	protected JRadioButton[] scopeButtons;

	/**
	 * Buttons
	 */
	protected JRadioButton[] modeButtons;

	/**
	 * Data panel
	 */
	protected JPanel dataPanel;

	/**
	 * Scope listener
	 */
	protected ActionListener scopeListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			SearchDialog.this.scopeValue = event.getActionCommand();

		}
	};

	/**
	 * Mode listener
	 */
	protected ActionListener modeListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			SearchDialog.this.modeValue = event.getActionCommand();
		}
	};

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
		final JButton oKButton = new JButton(Messages.getString("SearchDialog.ok")); //$NON-NLS-1$
		final JButton cancelButton = new JButton(Messages.getString("SearchDialog.cancel")); //$NON-NLS-1$

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

		oKButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				SearchDialog.this.ok = true;
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				setVisible(false);
			}
		});

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

	// static public void main(final String[] args)
	// {
	// UIManager.put("swing.boldMetal", false); //$NON-NLS-1$
	// final SearchDialog dialog = new SearchDialog("LABEL", "STARTSWITH", Messages.getString("SearchDialog.title"), //$NON-NLS-1$ //$NON-NLS-2$
	// //$NON-NLS-3$
	// Messages.getString("SearchDialog.prompt")); //$NON-NLS-1$
	// dialog.setModal(true);
	// dialog.setVisible(true);
	// dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	// if (dialog.ok)
	// {
	// System.out.println(dialog.scopeValue);
	// System.out.println(dialog.modeValue);
	// }
	// System.exit(0);
	// }
}
