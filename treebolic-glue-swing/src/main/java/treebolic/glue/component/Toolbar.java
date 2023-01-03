/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.glue.ActionListener;

/**
 * Toolbar, derived from JToolbar
 *
 * @author Bernard Bou
 */
public class Toolbar extends JToolBar implements Component, treebolic.glue.iface.component.Toolbar
{
	// D A T A

	/**
	 * (Ordered) toolbar
	 *
	 * @return array of buttons
	 */
	@Override
	@NonNull
	public Button[] getButtons()
	{
		return new Button[]{Button.HOME, //
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
			{
				return ordinal() + 4;
			}
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

		@SuppressWarnings("SameReturnValue")
		public boolean getState()
		{
			return false;
		}
	}

	/**
	 * Icon array
	 */
	@SuppressWarnings("DataFlowIssue")
	static final ImageIcon[] icons = new ImageIcon[]{new ImageIcon(PopupMenu.class.getResource("images/toolbar_home.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_radial.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_north.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_south.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_east.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_west.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_expand.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_shrink.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_expand_reset.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_widen.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_narrow.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_widen_reset.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_expand_widen_reset.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_zoomin.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_zoomout.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_zoomone.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_scaleup.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_scaledown.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_scaleone.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_arc.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_nodetooltip.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_nodetooltipcontent.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_hoverfocus.png")), //

			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_arc.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_nodetooltip.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_nodetooltipcontent.png")), //
			new ImageIcon(Toolbar.class.getResource("images/toolbar_no_hoverfocus.png")), //
	};

	/**
	 * Toolbar tooltips
	 */
	static public final String[] tooltips = Messages.getStrings( //
			"Toolbar_tooltip_reset", //
			"Toolbar_tooltip_radial", "Toolbar_tooltip_north", "Toolbar_tooltip_south", "Toolbar_tooltip_east", "Toolbar_tooltip_west", //

			"Toolbar_tooltip_expand", "Toolbar_tooltip_shrink", "Toolbar_tooltip_expand_reset", //
			"Toolbar_tooltip_widen", "Toolbar_tooltip_narrow", "Toolbar_tooltip_widen_reset", //
			"Toolbar_tooltip_expand_widen_reset", //
			"Toolbar_tooltip_zoomin", "Toolbar_tooltip_zoomout", "Toolbar_tooltip_zoomreset", "Toolbar_tooltip_scaleup", "Toolbar_tooltip_scaledown", "Toolbar_tooltip_scalereset", //

			"Toolbar_tooltip_arcline", //
			"Toolbar_tooltip_tooltip", "Toolbar_tooltip_tooltipcontent", //
			"Toolbar_tooltip_hoverfocus" //
	);

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param ignoredHandle Handle required for component creation (unused)
	 */
	public Toolbar(final Object ignoredHandle)
	{
		super(SwingConstants.VERTICAL);
		setFloatable(true);
	}

	/**
	 * Make button
	 *
	 * @param iconIndex icon
	 * @param toolTip   tooltip
	 * @param listener  action listener
	 */
	public void addButton(final int iconIndex, final String toolTip, @NonNull final ActionListener listener)
	{
		@NonNull final JButton button = new JButton();
		button.setBorder(null);
		button.setToolTipText(toolTip);
		button.addActionListener(e -> listener.actionPerformed(null));
		button.setIcon(Toolbar.icons[iconIndex]);
		add(button);
	}

	/**
	 * Make toggle button
	 *
	 * @param iconIndex         icon
	 * @param selectedIconIndex pressed icon
	 * @param toolTip           tooltip
	 * @param state             tooltip
	 * @param listener          action listener
	 */
	public void addToggle(final int iconIndex, final int selectedIconIndex, final String toolTip, final boolean state, @NonNull final ActionListener listener)
	{
		@NonNull final JToggleButton button = new JToggleButton();
		button.setBorder(null);
		button.setToolTipText(toolTip);
		button.addActionListener(e -> listener.actionPerformed(null));
		button.setIcon(Toolbar.icons[iconIndex]);
		button.setSelectedIcon(Toolbar.icons[selectedIconIndex]);
		button.setSelected(!state);
		add(button);
	}

	@Override
	public void addButton(@NonNull treebolic.glue.iface.component.Toolbar.Button button, @NonNull treebolic.glue.iface.ActionListener listener)
	{
		if (button.equals(Button.SEPARATOR))
		{
			addSeparator();
			return;
		}

		// interface button to implementation
		@NonNull final String name = button.name();
		@NonNull final ButtonImplementation impl = ButtonImplementation.valueOf(name);

		final int iconIndex = impl.getIconIndex();
		final String tooltip = impl.getTooltip();
		if (impl.isToggle())
		{
			final int selectedIconIndex = impl.getSelectedIconIndex();
			final boolean state = impl.getState();
			addToggle(iconIndex, selectedIconIndex, tooltip, state, (ActionListener) listener);
			return;
		}
		addButton(iconIndex, tooltip, (ActionListener) listener);
	}
}
