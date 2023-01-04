/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.event.ItemEvent;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.ActionListener;
import treebolic.glue.ColorKit;
import treebolic.glue.iface.Colors;

/**
 * Search tool, derived from BOX
 *
 * @author Bernard Bou
 */
public class SearchTool extends Box
{
	static private final String CMD_SEARCH = "SEARCH";

	static private final String CMD_RESET = "RESET";

	static private final String CMD_CONTINUE = "CONTINUE";

	private String scope = "LABEL";

	private String mode = "STARTSWITH";

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
				// System.out.println("SEARCH " + scope + ' ' + mode + ' ' + target); 
				SearchTool.this.listener.onAction(CMD_SEARCH, scope, mode, target);
				SearchTool.this.searchPending = true;
			}
			else
			{
				// System.out.println("SEARCH CONTINUE"); 
				SearchTool.this.listener.onAction(CMD_CONTINUE);
			}
		}
	}

	/**
	 * Reset search
	 */
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
		@NonNull final JButton inputButton = new JButton();
		@Nullable final URL searchRunIconUrl = SearchTool.class.getResource("images/search_run.png");
		assert searchRunIconUrl != null;
		inputButton.setIcon(new ImageIcon(searchRunIconUrl));
		inputButton.setToolTipText(Messages.getString("SearchTool.tooltip_run"));
		inputButton.setContentAreaFilled(false);
		inputButton.setFocusable(false);
		inputButton.addActionListener(e -> search());

		// input clear
		@NonNull final JButton inputClearButton = new JButton();
		@Nullable final URL searchResetIconUrl = SearchTool.class.getResource("images/search_reset.png");
		assert searchResetIconUrl != null;
		inputClearButton.setIcon(new ImageIcon(searchResetIconUrl));
		inputClearButton.setToolTipText(Messages.getString("SearchTool.tooltip_reset"));
		inputClearButton.setContentAreaFilled(false);
		inputClearButton.setFocusable(false);
		inputClearButton.addActionListener(e -> {
			if (SearchTool.this.listener != null)
			{
				// System.out.println("SEARCH RESET");
				if ("".equals(SearchTool.this.inputTextField.getText()))
				{
					resetSearch();
				}
				else
				{
					SearchTool.this.inputTextField.setText("");
				}
			}
		});

		// input
		this.inputTextField = new JTextField();
		this.inputTextField.setEditable(true);
		this.inputTextField.setBorder(new CompoundBorder(BorderFactory.createLineBorder(ColorKit.toAWT(Colors.GRAY)), new EmptyBorder(0, 5, 0, 5)));
		this.inputTextField.setToolTipText(Messages.getString("SearchTool.tooltip_target"));
		this.inputTextField.setPreferredSize(Constants.DIM_SEARCH_LABEL);
		this.inputTextField.setMaximumSize(Constants.DIM_SEARCH_LABEL);
		this.inputTextField.addActionListener(e -> search());
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

		@NonNull JButton settingButton = makeSettingsButton();
		settingButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(settingButton);
	}

	/**
	 * Add listener
	 *
	 * @param listener listener
	 */
	public void addListener(final ActionListener listener)
	{
		this.listener = listener;
	}

	/**
	 * Make search tool toggle button
	 *
	 * @param container container
	 * @return button
	 */
	@NonNull
	public JToggleButton makeControlToggle(@NonNull final java.awt.Container container)
	{
		// toggle icons
		@Nullable final URL searchOpenIconUrl = SearchTool.class.getResource("images/search_open.png");
		assert searchOpenIconUrl != null;
		@NonNull final Icon openIcon = new ImageIcon(searchOpenIconUrl);
		@Nullable final URL searchCloseIconUrl = SearchTool.class.getResource("images/search_close.png");
		assert searchCloseIconUrl != null;
		@NonNull final Icon closeIcon = new ImageIcon(searchCloseIconUrl);

		// toggle button
		@NonNull final JToggleButton toggleButton = new JToggleButton();
		toggleButton.setIcon(openIcon);
		toggleButton.setToolTipText(Messages.getString("SearchTool.tooltip_toggle"));
		toggleButton.setRolloverIcon(openIcon);
		toggleButton.setSelectedIcon(closeIcon);
		toggleButton.setRolloverSelectedIcon(closeIcon);
		toggleButton.setOpaque(true);
		toggleButton.addItemListener(e -> {
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
		});
		return toggleButton;
	}

	/**
	 * Make search tool toggle button
	 *
	 * @return button
	 */
	@NonNull
	public JButton makeSettingsButton()
	{
		// toggle icons
		@Nullable final URL searchSettingsIconUrl = SearchDialog.class.getResource("images/search_settings.png");
		assert searchSettingsIconUrl != null;
		@NonNull final Icon settingsIcon = new ImageIcon(searchSettingsIconUrl);

		// toggle button
		@NonNull final JButton settingsButton = new JButton();
		settingsButton.setIcon(settingsIcon);
		settingsButton.setContentAreaFilled(false);
		settingsButton.setOpaque(true);
		settingsButton.setToolTipText(Messages.getString("SearchTool.tooltip_scope_mode"));
		settingsButton.addActionListener(e -> {
			@NonNull final SearchDialog dialog = new SearchDialog(SearchTool.this.scope, SearchTool.this.mode, Messages.getString("SearchDialog.title"), Messages.getString("SearchDialog.prompt"));
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
