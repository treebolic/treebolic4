/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 * Update : Mon Mar 10 00:00:00 CEST 2008
 */
package treebolic.glue.component;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import treebolic.glue.ActionListener;

/**
 * Tool bar
 *
 * @author Bernard Bou
 */
public class Toolbar extends JToolBar implements Component, treebolic.glue.iface.component.Toolbar<ActionListener>
{
	// D A T A

	private static final long serialVersionUID = -4830414811106373265L;

	/**
	 * (Ordered) toolbar
	 *
	 * @return list of buttons
	 */
	static public Button[] toolbar()
	{
		return new Button[] { Button.HOME, //
				Button.ZOOMIN, Button.ZOOMOUT, Button.ZOOMONE, Button.SEPARATOR, //
				Button.SCALEUP, Button.SCALEDOWN, Button.SCALEONE, Button.SEPARATOR, //
				Button.RADIAL, Button.SOUTH, Button.NORTH, Button.EAST, Button.WEST, Button.SEPARATOR, //
				Button.EXPAND, Button.SHRINK, Button.EXPANSIONRESET, Button.SEPARATOR, //
				Button.WIDEN, Button.NARROW, Button.SWEEPRESET, Button.SEPARATOR, //
				Button.EXPANSIONSWEEPRESET, Button.SEPARATOR, //
				Button.ARCEDGE, //
				Button.TOOLTIP, Button.TOOLTIPCONTENT, //
				Button.FOCUSHOVER, //
		};
	}

	/**
	 * Buttons
	 */
	private enum ButtonImplementation
	{
		HOME, //
		RADIAL, NORTH, SOUTH, EAST, WEST, //
		EXPAND, SHRINK, EXPANSIONRESET, //
		WIDEN, NARROW, SWEEPRESET, //
		EXPANSIONSWEEPRESET, //
		ZOOMIN, ZOOMOUT, ZOOMONE, //
		SCALEUP, SCALEDOWN, SCALEONE, //
		ARCEDGE, //
		TOOLTIP, TOOLTIPCONTENT, //
		FOCUSHOVER, //
		;

		public int getIconIndex()
		{
			return ordinal();
		}

		public int getSelectedIconIndex()
		{
			if (isToggle())
				return ordinal() + 4;
			return -1;
		}

		public boolean isToggle()
		{
			switch (this)
			{
			case ARCEDGE:
			case TOOLTIP:
			case TOOLTIPCONTENT:
			case FOCUSHOVER:
				return true;
			default:
			}
			return false;
		}

		public String getTooltip()
		{
			return tooltips[ordinal()];
		}

		public boolean getState()
		{
			return false;
		}
	}

	/**
	 * Icon array
	 */
	static ImageIcon[] icons = new ImageIcon[] { 
			new ImageIcon(PopupMenu.class.getResource("images/toolbar_home.png")), //$NON-NLS-1$

			new ImageIcon(Toolbar.class.getResource("images/toolbar_radial.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_north.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_south.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_east.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_west.png")), //$NON-NLS-1$

			new ImageIcon(Toolbar.class.getResource("images/toolbar_expand.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_shrink.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_expand_reset.png")), //$NON-NLS-1$
			
			new ImageIcon(Toolbar.class.getResource("images/toolbar_widen.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_narrow.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_widen_reset.png")), //$NON-NLS-1$
			
			new ImageIcon(Toolbar.class.getResource("images/toolbar_expand_widen_reset.png")), //$NON-NLS-1$

			new ImageIcon(Toolbar.class.getResource("images/toolbar_zoomin.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_zoomout.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_zoomone.png")), //$NON-NLS-1$

			new ImageIcon(Toolbar.class.getResource("images/toolbar_scaleup.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_scaledown.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_scaleone.png")), //$NON-NLS-1$

			new ImageIcon(Toolbar.class.getResource("images/toolbar_arc.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_nodetooltip.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_nodetooltipcontent.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_hoverfocus.png")), //$NON-NLS-1$

			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_arc.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_nodetooltip.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_nodetooltipcontent.png")), //$NON-NLS-1$
			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_hoverfocus.png")), //$NON-NLS-1$
	};

	/**
	 * Toolbar tooltips
	 */
	static public String[] tooltips = Messages.getStrings( //
			"Toolbar_tooltip_reset", //$NON-NLS-1$
			"Toolbar_tooltip_radial", "Toolbar_tooltip_north", "Toolbar_tooltip_south", "Toolbar_tooltip_east", "Toolbar_tooltip_west", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

			"Toolbar_tooltip_expand", "Toolbar_tooltip_shrink", "Toolbar_tooltip_expand_reset", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"Toolbar_tooltip_widen", "Toolbar_tooltip_narrow", "Toolbar_tooltip_widen_reset", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"Toolbar_tooltip_expand_widen_reset", //$NON-NLS-1$
			"Toolbar_tooltip_zoomin", "Toolbar_tooltip_zoomout", "Toolbar_tooltip_zoomreset", // $NON-NLS-1$,$NON-NLS-2$,$NON-NLS-3$ //$NON-NLS-3$
			"Toolbar_tooltip_scaleup", "Toolbar_tooltip_scaledown", "Toolbar_tooltip_scalereset", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			"Toolbar_tooltip_arcline", //$NON-NLS-1$
			"Toolbar_tooltip_tooltip", "Toolbar_tooltip_tooltipcontent", //$NON-NLS-1$ //$NON-NLS-2$
			"Toolbar_tooltip_hoverfocus" //$NON-NLS-1$
	);

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param handle Handle required for component creation (unused)
	 */
	public Toolbar(final Object handle)
	{
		super(SwingConstants.VERTICAL);
		setFloatable(true);
	}

	/**
	 * Make button
	 *
	 * @param iconIndex icon
	 * @param toolTip tooltip
	 * @param listener action listener
	 */
	public void addButton(final int iconIndex, final String toolTip, final ActionListener listener)
	{
		final JButton button = new JButton();
		button.setBorder(null);
		button.setToolTipText(toolTip);
		button.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e)
			{
				listener.actionPerformed(null);
			}
		});
		button.setIcon(Toolbar.icons[iconIndex]);
		add(button);
	}

	/**
	 * Make toggle button
	 *
	 * @param iconIndex icon
	 * @param selectedIconIndex pressed icon
	 * @param toolTip tooltip
	 * @param state tooltip
	 * @param listener action listener
	 */
	public void addToggle(final int iconIndex, final int selectedIconIndex, final String toolTip, final boolean state, final ActionListener listener)
	{
		final JToggleButton button = new JToggleButton();
		button.setBorder(null);
		button.setToolTipText(toolTip);
		button.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e)
			{
				listener.actionPerformed(null);
			}
		});
		button.setIcon(Toolbar.icons[iconIndex]);
		button.setSelectedIcon(Toolbar.icons[selectedIconIndex]);
		button.setSelected(!state);
		add(button);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.glue.iface.component.Toolbar#addButton(treebolic.glue.iface.component.Toolbar.Button, java.lang.Object)
	 */
	@Override
	public void addButton(treebolic.glue.iface.component.Toolbar.Button button, ActionListener listener)
	{
		if (button.equals(Button.SEPARATOR))
		{
			addSeparator();
			return;
		}

		// interface button to implementation
		final String name = button.name();
		final ButtonImplementation impl = ButtonImplementation.valueOf(name);

		final int iconIndex = impl.getIconIndex();
		final String tooltip = impl.getTooltip();
		if (impl.isToggle())
		{
			final int selectedIconIndex = impl.getSelectedIconIndex();
			final boolean state = impl.getState();
			addToggle(iconIndex, selectedIconIndex, tooltip, state, listener);
			return;
		}
		addButton(iconIndex, tooltip, listener);
	}
}
