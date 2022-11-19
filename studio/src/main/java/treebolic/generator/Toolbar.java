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
 * Tool bar
 *
 * @author Bernard Bou
 */
public class Toolbar extends JToolBar
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
	static public boolean isVertical = true;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Toolbar()
	{
		super(Toolbar.isVertical ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL);
		setFloatable(true);
		setPreferredSize(new Dimension(36, 20 + 8 * 36));
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		final JButton openButton = makeButton(Messages.getString("Toolbar.open"), Messages.getString("Toolbar.tooltip_open"), "open.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		openButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.OPEN, 0));

		final JButton openHttpButton = makeButton(Messages.getString("Toolbar.open_url"), Messages.getString("Toolbar.tooltip_open_url"), "openurl.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		openHttpButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.OPENURL, 0));

		final JButton newButton = makeButton(Messages.getString("Toolbar.new"), Messages.getString("Toolbar.tooltip_new"), "newdoc.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		newButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.NEW, 0));

		final JButton saveButton = makeButton(Messages.getString("Toolbar.save"), Messages.getString("Toolbar.tooltip_save"), "save.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		saveButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.SAVE, 0));

		final JButton makeSiteButton = makeButton(Messages.getString("Toolbar.make"), Messages.getString("Toolbar.tooltip_make"), "sitemake.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		makeSiteButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.MAKESITE, 0));

		final JButton runSiteButton = makeButton(Messages.getString("Toolbar.run"), Messages.getString("Toolbar.tooltip_run"), "siterun.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		runSiteButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.RUNSITE, 0));

		final JButton helpButton = makeButton(Messages.getString("Toolbar.help"), Messages.getString("Toolbar.tooltip_help"), "help.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		helpButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.HELP, 0));

		final JButton updateNodeButton = makeButton(Messages.getString("Toolbar.update"), Messages.getString("Toolbar.tooltip_update"), "refresh.png"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		updateNodeButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.UPDATE, 0));

		this.add(openButton);
		this.add(openHttpButton);
		this.add(newButton);
		this.add(saveButton);
		this.add(updateNodeButton);
		this.add(makeSiteButton);
		this.add(runSiteButton);
		this.add(helpButton);
	}

	/**
	 * Make button
	 *
	 * @param text
	 *        text
	 * @param tooltip
	 *        tooltip
	 * @param image
	 *        image
	 * @return button
	 */
	private JButton makeButton(final String text, final String tooltip, final String image)
	{
		final JButton button = new JButton();
		button.setToolTipText(tooltip);
		button.setIcon(new ImageIcon(Toolbar.class.getResource("images/" + image))); //$NON-NLS-1$
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
