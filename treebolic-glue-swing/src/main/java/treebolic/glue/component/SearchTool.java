/**
 * Title : Treebolic
 * Description: Treebolic
 * Version: 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 * Update : Jan 7, 2016
 */

package treebolic.glue.component;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import treebolic.glue.ActionListener;
import treebolic.glue.Color;

/**
 * @author Bernard Bou
 */
public class SearchTool extends Box
{
	private static final long serialVersionUID = -6354719663163733300L;

	static private final String CMD_SEARCH = "SEARCH"; //$NON-NLS-1$

	static private final String CMD_RESET = "RESET"; //$NON-NLS-1$

	static private final String CMD_CONTINUE = "CONTINUE"; //$NON-NLS-1$

	private String scope = "LABEL"; //$NON-NLS-1$

	private String mode = "STARTSWITH"; //$NON-NLS-1$

	/**
	 * Input
	 */
	private JTextField inputTextField;

	/**
	 * Listener (sink for button)
	 */
	private ActionListener listener;

	/**
	 * Search pending flag
	 */
	private boolean searchPending = false;

	/**
	 * Constructor
	 */
	public SearchTool()
	{
		super(BoxLayout.X_AXIS);
		init();
	}

	private void search()
	{
		if (SearchTool.this.listener != null)
		{
			if (!SearchTool.this.searchPending)
			{
				final String scope = SearchTool.this.scope;
				final String mode = SearchTool.this.mode;
				final String target = get();
				// System.out.println("SEARCH " + scope + ' ' + mode + ' ' + target); //$NON-NLS-1$
				SearchTool.this.listener.onAction(CMD_SEARCH, scope, mode, target);
				SearchTool.this.searchPending = true;
			}
			else
			{
				// System.out.println("SEARCH CONTINUE"); //$NON-NLS-1$
				SearchTool.this.listener.onAction(CMD_CONTINUE);
			}
		}
	}

	public void resetSearch()
	{
		this.searchPending = false;
		if (SearchTool.this.listener != null)
		{
			this.listener.onAction(CMD_RESET);
		}
	}

	void init()
	{
		// input button
		final JButton inputButton = new JButton();
		inputButton.setIcon(new ImageIcon(SearchTool.class.getResource("images/search_run.png"))); //$NON-NLS-1$
		inputButton.setToolTipText(Messages.getString("SearchTool.tooltip_run")); //$NON-NLS-1$
		inputButton.setContentAreaFilled(false);
		inputButton.setFocusable(false);
		inputButton.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				search();
			}
		});

		// input clear
		final JButton inputClearButton = new JButton();
		inputClearButton.setIcon(new ImageIcon(SearchTool.class.getResource("images/search_reset.png"))); //$NON-NLS-1$
		inputClearButton.setToolTipText(Messages.getString("SearchTool.tooltip_reset")); //$NON-NLS-1$
		inputClearButton.setContentAreaFilled(false);
		inputClearButton.setFocusable(false);
		inputClearButton.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (SearchTool.this.listener != null)
				{
					// System.out.println("SEARCH RESET"); //$NON-NLS-1$
					if ("".equals(SearchTool.this.inputTextField.getText())) //$NON-NLS-1$
						resetSearch();
					else
						SearchTool.this.inputTextField.setText(""); //$NON-NLS-1$
				}
			}
		});

		// input
		this.inputTextField = new JTextField();
		this.inputTextField.setEditable(true);
		this.inputTextField.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.GRAY.color), new EmptyBorder(0, 5, 0, 5)));
		this.inputTextField.setToolTipText(Messages.getString("SearchTool.tooltip_target")); //$NON-NLS-1$
		this.inputTextField.setPreferredSize(Constants.DIM_SEARCH_LABEL);
		this.inputTextField.setMaximumSize(Constants.DIM_SEARCH_LABEL);
		this.inputTextField.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				search();
			}
		});
		this.inputTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				resetSearch();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				resetSearch();
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				resetSearch();
			}

			public void resetSearch()
			{
				SearchTool.this.resetSearch();
			}
		});

		// assemble
		inputButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
		add(inputButton);

		add(this.inputTextField);

		inputClearButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		add(inputClearButton);

		JButton settingButton = makeSettingsButton();
		settingButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(settingButton);
	}

	/**
	 * Add listener
	 *
	 * @param listener
	 *        listener
	 */
	public void addListener(final ActionListener listener)
	{
		this.listener = listener;
	}

	/**
	 * Make search tool toggle button
	 * 
	 * @param container
	 *        container
	 * @return button
	 */
	public JToggleButton makeControlToggle(final java.awt.Container container)
	{
		// toggle icons
		final Icon openIcon = new ImageIcon(SearchTool.class.getResource("images/search_open.png")); //$NON-NLS-1$
		final Icon closeIcon = new ImageIcon(SearchTool.class.getResource("images/search_close.png")); //$NON-NLS-1$

		// toggle button
		final JToggleButton toggleButton = new JToggleButton();
		toggleButton.setIcon(openIcon);
		toggleButton.setToolTipText(Messages.getString("SearchTool.tooltip_toggle")); //$NON-NLS-1$
		toggleButton.setRolloverIcon(openIcon);
		toggleButton.setSelectedIcon(closeIcon);
		toggleButton.setRolloverSelectedIcon(closeIcon);
		toggleButton.setOpaque(true);
		toggleButton.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(final ItemEvent e)
			{
				final boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;
				if (isSelected)
				{
					container.add(SearchTool.this);
				}
				else
				{
					container.remove(SearchTool.this);
				}
				container.validate();
			}
		});
		return toggleButton;
	}

	/**
	 * Make search tool toggle button
	 * 
	 * @return button
	 */
	public JButton makeSettingsButton()
	{
		// toggle icons
		final Icon settingsIcon = new ImageIcon(SearchDialog.class.getResource("images/search_settings.png")); //$NON-NLS-1$

		// toggle button
		final JButton settingsButton = new JButton();
		settingsButton.setIcon(settingsIcon);
		settingsButton.setContentAreaFilled(false);
		settingsButton.setOpaque(true);
		settingsButton.setToolTipText(Messages.getString("SearchTool.tooltip_scope_mode")); //$NON-NLS-1$
		settingsButton.addActionListener(new java.awt.event.ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				final SearchDialog dialog = new SearchDialog(SearchTool.this.scope, SearchTool.this.mode, Messages.getString("SearchDialog.title"), Messages.getString("SearchDialog.prompt")); //$NON-NLS-1$ //$NON-NLS-2$
				dialog.setModal(true);
				dialog.setVisible(true);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				if (dialog.ok)
				{
					SearchTool.this.scope = dialog.scopeValue;
					SearchTool.this.mode = dialog.modeValue;
					System.out.println(SearchTool.this.scope + ' ' + SearchTool.this.mode);

					resetSearch();
				}
			}
		});
		return settingsButton;
	}

	/**
	 * Get input
	 *
	 * @return input
	 */
	public String get()
	{
		return this.inputTextField.getText();
	}
}
