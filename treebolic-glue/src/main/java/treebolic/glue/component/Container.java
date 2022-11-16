package treebolic.glue.component;

import treebolic.glue.NotImplementedException;

/**
 * Container
 */
public class Container implements Component, treebolic.glue.iface.component.Container<Component>
{
	/**
	 * Constructor
	 *
	 * @param handle  Opaque handle required for component creation
	 */
	public Container(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addComponent(final Component component, final int position)
	{
		throw new NotImplementedException();
	}

	@Override
	public void removeAll()
	{
		throw new NotImplementedException();
	}

	@Override
	public void validate()
	{
		throw new NotImplementedException();
	}
}
