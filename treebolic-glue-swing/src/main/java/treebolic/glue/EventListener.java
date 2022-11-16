package treebolic.glue;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class EventListener implements MouseListener, MouseMotionListener, treebolic.glue.iface.EventListener
{
	// MouseListener;

	// @Override
	// abstract public boolean onClicked(int x, int y, boolean isShiftDown, boolean isControlDown, boolean isRightButton);

	@Override
	abstract public boolean onDown(int x, int y, boolean rotate);

	@Override
	abstract public boolean onUp(int x, int y);

	// MouseMotionListener;

	@Override
	abstract public boolean onHover(int x, int y);

	@Override
	abstract public boolean onDragged(int x, int y);

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(final MouseEvent e)
	{
		if (onDragged(e.getX(), e.getY()))
		{
			e.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(final MouseEvent e)
	{
		// check if we are hovering
		if (onHover(e.getX(), e.getY()))
		{
			e.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(final MouseEvent e)
	{
		boolean consume = false;
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
		{
			consume = onMenu(e.getX(), e.getY());
		}
		else if (e.isControlDown())
		{
			consume = onMount(e.getX(), e.getY());
		}
		else if (e.isShiftDown())
		{
			consume = onLink(e.getX(), e.getY());
		}
		else if (!e.isControlDown() && !e.isShiftDown())
		{
			consume = onFocus(e.getX(), e.getY());
		}
		if (consume)
		{
			e.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(final MouseEvent e)
	{
		if (onDown(e.getX(), e.getY(), e.isShiftDown()))
		{
			e.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(final MouseEvent e)
	{
		if (onUp(e.getX(), e.getY()))
		{
			e.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(final MouseEvent e)
	{
		//
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(final MouseEvent e)
	{
		//
	}
}
