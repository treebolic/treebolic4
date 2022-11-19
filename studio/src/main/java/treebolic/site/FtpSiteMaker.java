/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.site;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.w3c.dom.Document;

import sun.net.ftp.FtpLoginException;

/**
 * FTP extended client
 *
 * @author Bernard Bou
 */
class FtpClientX extends FtpClient
{
	/**
	 * MKDIR a directory on a remote FTP server
	 *
	 * @param remoteDirectory remote directory to create
	 * @throws IOException io exception
	 */
	public void mkdir(final String remoteDirectory) throws IOException
	{
		try
		{
			issueCommandCheck("MKD " + remoteDirectory); 
		}
		catch (final IOException e1)
		{
			// Well, "/" might not be the file delimitor for this particular ftp server,
			// so let's try a series of "cd" and "mkd" commands to get to the right place.

			String pathElement;

			// cd path
			final StringTokenizer t = new StringTokenizer(remoteDirectory, "/"); 
			while (t.hasMoreElements())
			{
				pathElement = t.nextToken();

				// cd this pathElement
				try
				{
					cd(pathElement);
				}
				catch (final IOException e2)
				{
					try
					{
						issueCommandCheck("MKDIR " + pathElement); 
						cd(pathElement);
					}
					catch (final IOException e3)
					{
						// giving up.
						throw e1;
					}
				}
			}
		}
	}

	/**
	 * MKDIR a directory on a remote FTP server
	 *
	 */
	public void passive()
	{
		try
		{
			issueCommandCheck("PASV "); 
		}
		catch (final IOException e1)
		{
			// do nothing
		}
	}
}

/**
 * FTP site maker
 *
 * @author Bernard Bou
 */
public class FtpSiteMaker extends SiteMaker
{
	/**
	 * Server
	 */
	private final String server;

	/**
	 * Server directory
	 */
	private final String directory;

	/**
	 * Server login
	 */
	private final String login;

	/**
	 * Server login password
	 */
	private final String password;

	/**
	 * FTP client
	 */
	private final FtpClientX client;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param document        DOM document (source)
	 * @param repository      repository (source)
	 * @param imageRepository image repository (source)
	 * @param htmlFile        HTML file (target)
	 * @param xmlFile         XML file (target)
	 * @param title           HTML page title (target)
	 * @param server          server (target)
	 * @param directory       server directory (target)
	 * @param login           server login (target)
	 * @param password        server login password (target)
	 */
	public FtpSiteMaker(final Document document, final String repository, final String imageRepository, final String htmlFile, final String xmlFile, final String title, final String server, final String directory, final String login, final String password)
	{
		super(document, repository, imageRepository, htmlFile, xmlFile, title);

		this.server = server;
		this.directory = directory;
		this.login = login;
		this.password = password;
		this.client = new FtpClientX();
	}

	// I N T E R F A C E

	/*
	 * (non-Javadoc)
	 * @see treebolic.site.SiteMaker#makeOutStream(java.lang.String)
	 */
	@Override
	public OutputStream makeOutStream(final String string) throws IOException
	{
		return this.client.put(string);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.sitemaker.SiteMaker#connect()
	 */
	@Override
	public boolean connect()
	{
		try
		{
			notifyOperation(Messages.getString("FtpSiteMaker.opening"), SiteMaker.PROGRESSCONNECT); 
			this.client.openServer(this.server);

			try
			{
				notifyOperation(Messages.getString("FtpSiteMaker.logging"), SiteMaker.PROGRESSCONNECT + 2); 
				this.client.login(this.login, this.password);
			}
			catch (final FtpLoginException exception)
			{
				this.client.closeServer();
				throw new IOException("LOGGING IN"); 
			}

			notifyOperation(Messages.getString("FtpSiteMaker.passive"), SiteMaker.PROGRESSCONNECT + 2); 
			this.client.passive();

			notifyOperation(Messages.getString("FtpSiteMaker.binary"), SiteMaker.PROGRESSCONNECT + 3); 
			this.client.binary();

			try
			{
				notifyOperation("CD " + this.directory, SiteMaker.PROGRESSCONNECT + 4); 
				this.client.cd(this.directory);
			}
			catch (final IOException exception)
			{
				this.client.mkdir(this.directory);
				this.client.cd(this.directory);
			}
		}
		catch (final IOException exception)
		{
			notifyOperation(Messages.getString("FtpSiteMaker.err_io") + exception.getMessage(), SiteMaker.PROGRESSCONNECTCOMPLETE); 
			return false;
		}

		notifyOperation(Messages.getString("FtpSiteMaker.opened"), SiteMaker.PROGRESSCONNECTCOMPLETE); 
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.sitemaker.SiteMaker#disconnect()
	 */
	@Override
	public boolean disconnect()
	{
		try
		{
			this.client.closeServer();
		}
		catch (final IOException exception)
		{
			exception.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.sitemaker.SiteMaker#changeFolder(java.lang.String)
	 */
	@Override
	public boolean changeFolder(final String relativeDirectory)
	{
		try
		{
			this.client.cd(relativeDirectory);
		}
		catch (final IOException exception)
		{
			try
			{
				notifyOperation("MKDIR " + relativeDirectory, SiteMaker.PROGRESSXFERIMAGES + 2); 
				this.client.mkdir(relativeDirectory);
				notifyOperation("CD " + relativeDirectory, SiteMaker.PROGRESSXFERIMAGES + 2); 
				this.client.cd(relativeDirectory);
			}
			catch (final IOException subException)
			{
				return false;
			}
		}
		return true;
	}
}
