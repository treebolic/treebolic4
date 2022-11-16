package treebolic.glue;

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
	public Void doInBackground() throws Exception
	{
		job();
		return null;
	}
}
