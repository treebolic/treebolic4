/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import treebolic.glue.ActionListener;
import treebolic.glue.Color;
import treebolic.glue.iface.component.Converter;

/**
 * Status bar, derived from JToolbar
 *
 * @author Bernard Bou
 */
public class Statusbar extends JToolBar implements Component, treebolic.glue.iface.component.Statusbar<Color, ActionListener>
{
	private static final long serialVersionUID = 1L;

	static public final boolean HAS_SEARCH = true;

	public enum ImageIndices
	{
		INFO, LINK, FAIL
	}

	/*
	 * Icon array
	 */
	@SuppressWarnings("ConstantConditions")
	static final ImageIcon[] icons = new ImageIcon[] { new ImageIcon(Toolbar.class.getResource("images/status_info.png")),
			new ImageIcon(Toolbar.class.getResource("images/status_linking.png")), 
			new ImageIcon(Toolbar.class.getResource("images/status_mounting.png")), 
			new ImageIcon(Toolbar.class.getResource("images/status_searching.png")), 
	};

	/**
	 * Content panel increment
	 */
	static private final int CONTENTINCREMENT = 20;

	/**
	 * Content panel minimum
	 */
	static private final int CONTENTMINIMUM = 24;

	/**
	 * Content panel maximum
	 */
	static private final int CONTENTMAXIMUM = 200;

	// D A T A

	// components

	/**
	 * Operation icon label
	 */
	private JLabel operationIconLabel;

	/**
	 * Label
	 */
	private JTextField labelTextField;

	/**
	 * Content
	 */
	private JTextPane contentTextPane;

	/**
	 * Content pane
	 */
	private JComponent contentPane;

	/**
	 * Label and search box
	 */
	private Box labelAndInputBox;

	/**
	 * Toggle input button
	 */
	@SuppressWarnings("FieldCanBeLocal")
	private JToggleButton searchToggleButton;

	/**
	 * SearchTool
	 */
	private SearchTool searchTool;

	/**
	 * Content CSS style
	 */
	private StyleSheet styleSheet;

	static private final SimpleAttributeSet contentStyle = new SimpleAttributeSet();

	static
	{
		StyleConstants.setFontFamily(Statusbar.contentStyle, Constants.FONT_FAMILY);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param ignoredHandle Handle required for component creation (unused)
	 */
	public Statusbar(final Object ignoredHandle)
	{
		super(SwingConstants.HORIZONTAL);
		setLayout(new GridBagLayout());
		setFloatable(true);
	}

	/**
	 * Init
	 *
	 * @param operationImage image
	 */
	@Override
	public void init(final int operationImage)
	{
		// enlarge/shrink buttons
		final JButton moreButton = new JButton();
		//noinspection ConstantConditions
		moreButton.setIcon(new ImageIcon(Statusbar.class.getResource("images/status_plus.png")));
		moreButton.setContentAreaFilled(false);
		moreButton.setFocusable(false);
		moreButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		moreButton.addActionListener(e -> {
			final Dimension size = Statusbar.this.contentPane.getPreferredSize();
			final int h = size.height + Statusbar.CONTENTINCREMENT;
			if (h <= Statusbar.CONTENTMAXIMUM)
			{
				size.height = h;
				Statusbar.this.contentPane.setPreferredSize(size);
				final Container container = getParent();
				container.validate();
			}
		});
		final JButton lessButton = new JButton();
		//noinspection ConstantConditions
		lessButton.setIcon(new ImageIcon(Statusbar.class.getResource("images/status_minus.png")));
		lessButton.setContentAreaFilled(false);
		lessButton.setFocusable(false);
		lessButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		lessButton.addActionListener(e -> {
			final Dimension size = Statusbar.this.contentPane.getPreferredSize();
			final int h = size.height - Statusbar.CONTENTINCREMENT;
			if (h >= Statusbar.CONTENTMINIMUM)
			{
				size.height = h;
				Statusbar.this.contentPane.setPreferredSize(size);
				final Container container = getParent();
				container.validate();
			}
		});

		// button box
		final Box buttonBox = new Box(BoxLayout.Y_AXIS);
		buttonBox.add(moreButton);
		buttonBox.add(lessButton);

		// operation
		this.operationIconLabel = new JLabel();
		this.operationIconLabel.setIcon(Statusbar.icons[operationImage]);

		// operation
		this.labelTextField = new JTextField();
		this.labelTextField.setEditable(false);
		this.labelTextField.setBackground(Color.WHITE.color);
		this.labelTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY.color));
		this.labelTextField.setToolTipText(Messages.getString("Statusbar.tooltip_label")); 
		this.labelTextField.setPreferredSize(Constants.DIM_STATUS_LABEL);

		// content
		this.contentTextPane = new JTextPane();
		this.contentTextPane.setContentType("text/html; charset=UTF-8"); 
		this.contentTextPane.setEditable(false);
		this.contentTextPane.setToolTipText(Messages.getString("Statusbar.tooltip_content")); 
		this.contentTextPane.setPreferredSize(Constants.DIM_STATUS_CONTENT);

		// stylesheet
		final HTMLEditorKit kit = new HTMLEditorKit();
		this.contentTextPane.setEditorKit(kit);
		this.styleSheet = kit.getStyleSheet();

		this.contentPane = new JScrollPane(this.contentTextPane);
		this.contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY.color));

		// label + input box
		this.labelAndInputBox = new Box(BoxLayout.X_AXIS);
		this.labelAndInputBox.add(this.labelTextField);

		// search tool
		this.searchTool = new SearchTool();

		// search tool toggle button
		this.searchToggleButton = this.searchTool.makeControlToggle(this.labelAndInputBox);

		// assemble
		add(this.operationIconLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 16, 0, 10), 0, 0));
		add(this.labelAndInputBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		add(this.contentPane, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
		if (HAS_SEARCH)
			add(this.searchToggleButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(buttonBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));

		validate();
	}

	/**
	 * Set colors
	 *
	 * @param backColor backcolor
	 * @param foreColor forecolor
	 */
	@Override
	public void setColors(final Color backColor, final Color foreColor)
	{
		setBackground(backColor.color);

		this.labelAndInputBox.setBackground(backColor.color);
		this.labelTextField.setBackground(backColor.color);
		this.searchTool.setBackground(backColor.color);
		this.contentTextPane.setBackground(backColor.color);

		setForeground(foreColor.color);

		this.labelAndInputBox.setForeground(foreColor.color);
		this.labelTextField.setForeground(foreColor.color);
		this.searchTool.setForeground(foreColor.color);
		this.contentTextPane.setForeground(foreColor.color);

		StyleConstants.setForeground(Statusbar.contentStyle, foreColor.color);
	}

	@Override
	public void setStyle(final String style)
	{
		final String[] rules = style.split("\n"); 
		for (final String rule : rules)
		{
			this.styleSheet.addRule(rule);
		}
	}

	/**
	 * Add listener to this component
	 *
	 * @param listener listener
	 */
	@Override
	public void addListener(final ActionListener listener)
	{
		this.searchTool.addListener(listener);
	}

	/**
	 * Listen to widget
	 *
	 * @param actionListener listener
	 */
	@Override
	public void setListener(final treebolic.glue.iface.ActionListener actionListener)
	{
		//
	}

	/**
	 * Put status
	 *
	 * @param image image
	 * @param converter converter
	 * @param label label
	 * @param contents contents
	 */
	@Override
	public void put(final int image, final Converter converter, final String label, final String... contents)
	{
		// icon and colors
		this.operationIconLabel.setIcon(Statusbar.icons[image]);

		// label
		this.labelTextField.setText(label);

		// content
		String content;
		if (contents == null || contents.length == 0)
			content = "";
		else if (converter != null)
			content = converter.convert(contents);
		else
			content = String.join("\n", contents);
		this.contentTextPane.setText(content);
		this.contentTextPane.setCaretPosition(0);

		// style
		final StyledDocument styledDocument = this.contentTextPane.getStyledDocument();
		styledDocument.setCharacterAttributes(0, styledDocument.getLength(), Statusbar.contentStyle, false);
	}

	/**
	 * Put status
	 *
	 * @param message content
	 */
	@Override
	public void put(final String message)
	{
		this.contentTextPane.setText(message);
		this.contentTextPane.setCaretPosition(0);
	}

	/**
	 * Get input
	 *
	 * @return input
	 */
	public String get()
	{
		return this.searchTool.get();
	}
}
