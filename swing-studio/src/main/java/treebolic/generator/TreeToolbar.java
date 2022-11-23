/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * Tool bar for tree view
 *
 * @author Bernard Bou
 */
public class TreeToolbar extends JToolBar
{
	private static final long serialVersionUID = 1L;

	// D A T A

	/**
	 * Controller (command sink)
	 */
	private Controller controller;

	/**
	 * Vertical layout
	 */
	static public final boolean isVertical = true;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public TreeToolbar()
	{
		super(TreeToolbar.isVertical ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL);
		setFloatable(true);
		setPreferredSize(new Dimension(36, 20 + 4 * 36));
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		final JButton newElementButton = makeButton(Messages.getString("TreeToolbar.new"), Messages.getString("TreeToolbar.tooltip_new"), "new.png");   
		newElementButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.NEWELEMENT, 0));

		final JButton deleteElementButton = makeButton(Messages.getString("TreeToolbar.remove"), Messages.getString("TreeToolbar.tooltip_remove"), "delete.png");   
		deleteElementButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.REMOVEELEMENT, 0));
		this.add(newElementButton);
		this.add(deleteElementButton);

		final JButton expandButton = makeButton(Messages.getString("TreeToolbar.expand"), Messages.getString("TreeToolbar.tooltip_expand"), "expand.png");   
		expandButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.EXPANDTREE, 0));
		final JButton collapseButton = makeButton(Messages.getString("TreeToolbar.collapse"), Messages.getString("TreeToolbar.tooltip_collapse"), "collapse.png");   
		collapseButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.COLLAPSETREE, 0));
		this.add(newElementButton);
		this.add(deleteElementButton);
		this.add(expandButton);
		this.add(collapseButton);

	}

	/**
	 * Make button
	 *
	 * @param ignoredText
	 *        text
	 * @param tooltip
	 *        tooltip
	 * @param image
	 *        image
	 * @return button
	 */
	private JButton makeButton(final String ignoredText, final String tooltip, final String image)
	{
		final JButton button = new JButton();
		button.setToolTipText(tooltip);
		//noinspection ConstantConditions
		button.setIcon(new ImageIcon(TreeToolbar.class.getResource("images/" + image)));
		return button;
	}

	/**
	 * Set controller
	 *
	 * @param controller
	 *        controller
	 */
	public void setController(final Controller controller)
	{
		this.controller = controller;
	}
}
