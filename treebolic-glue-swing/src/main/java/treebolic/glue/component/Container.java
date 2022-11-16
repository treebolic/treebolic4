package treebolic.glue.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

public class Container extends javax.swing.JPanel implements Component, treebolic.glue.iface.component.Container<Component>
{
	private static final long serialVersionUID = 5343097039212596542L;

	/**
	 * Constructor
	 *
	 * @param handle
	 */
	public Container(final Object handle)
	{
		super();
		setLayout(new BorderLayout());
		setPreferredSize(Constants.DIM_CONTAINER);
	}

	@Override
	public void addComponent(final Component component, final int position)
	{
		Object constraints = null;
		switch (position)
		{
		case PANE:
		case VIEW:
			constraints = BorderLayout.CENTER;
			break;
		case TOOLBAR:
			constraints = BorderLayout.EAST;
			break;
		case STATUSBAR:
			constraints = BorderLayout.SOUTH;
			break;
		}
		add((java.awt.Component) component, constraints);
	}

	@Override
	public void setPreferredSize(final Dimension preferredSize)
	{
		super.setPreferredSize(preferredSize);
	}
}
