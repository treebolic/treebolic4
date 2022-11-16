/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

/**
 * Worker, embeds swing's SwingWorker
 *
 * @author Bernard Bou
 */
public abstract class Worker extends javax.swing.SwingWorker<Void, Void> implements treebolic.glue.iface.Worker
{
	@Override
	public void job()
	{
		try
		{
			doInBackground();
		}
		catch (Exception exception)
		{
			//
		}
	}

	@Override
	protected void done()
	{
		onDone();
	}

	// public void execute();

	/*
	 * (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	public Void doInBackground()
	{
		job();
		return null;
	}
}
