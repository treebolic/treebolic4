/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Change notifier
 *
 * @author Bernard Bou
 */
public class ChangeNotifier
{
	/**
	 * Listener list
	 */
	protected final EventListenerList listeners = new EventListenerList();

	/**
	 * Constructor
	 */
	public ChangeNotifier()
	{
		// do nothing
	}

	/**
	 * Add a listener
	 *
	 * @param listener listener
	 */
	public void addChangeListener(final ChangeListener listener)
	{
		this.listeners.add(ChangeListener.class, listener);
	}

	/**
	 * Remove listener
	 *
	 * @param listener listener
	 */
	public void removeChangeListener(final ChangeListener listener)
	{
		this.listeners.remove(ChangeListener.class, listener);
	}

	/**
	 * Get change listeners
	 *
	 * @return list of listeners
	 */
	public ChangeListener[] getChangeListeners()
	{
		return this.listeners.getListeners(ChangeListener.class);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type.
	 *
	 * @param changeEvent change event
	 * @see EventListenerList
	 */
	public void fireStateChanged(final ChangeEvent changeEvent)
	{
		// guaranteed to return a non-null array
		final Object[] listeners = this.listeners.getListenerList();

		// process the listeners last to first, notifying those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				// fire
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}
}
