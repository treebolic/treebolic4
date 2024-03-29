/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * File dialog utilities
 *
 * @author Bernard Bou
 */
public class FileDialogs
{
	/**
	 * Extended file filter
	 *
	 * @author Bernard Bou
	 */
	static abstract class XFileFilter extends javax.swing.filechooser.FileFilter
	{
		/**
		 * Filters
		 */
		protected final String[] filters;

		/**
		 * Description
		 */
		protected final String description;

		/**
		 * Constructor
		 *
		 * @param filters     filters
		 * @param description description
		 */
		public XFileFilter(final String[] filters, final String description)
		{
			this.filters = filters;
			this.description = description;
		}

		@Override
		public String getDescription()
		{
			return this.description;
		}
	}

	static class RegExprFileFilter extends XFileFilter
	{
		/**
		 * Constructor
		 *
		 * @param filters     filters
		 * @param description description
		 */
		public RegExprFileFilter(String[] filters, String description)
		{
			super(filters, description);
		}

		@Override
		public boolean accept(@NonNull final File file)
		{
			@NonNull final String name = file.getName().toLowerCase();
			for (@NonNull final String filter : this.filters)
			{
				if (name.matches(filter))
				{
					return true;
				}
			}
			return file.isDirectory();
		}
	}

	static class NegRegExprFileFilter extends XFileFilter
	{
		/**
		 * Constructor
		 *
		 * @param extensions  extension
		 * @param description description
		 */
		public NegRegExprFileFilter(String[] extensions, String description)
		{
			super(extensions, description);
		}

		@Override
		public boolean accept(@NonNull final File file)
		{
			@NonNull final String name = file.getName().toLowerCase();
			for (@NonNull final String filter : this.filters)
			{
				if (name.matches(filter))
				{
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Executable file filter
	 */
	static private final FileFilter executableFileFilter = new RegExprFileFilter(new String[]{".*\\.exe", ".*\\.sh"}, Messages.getString("FileDialogs.exe"));

	/**
	 * XML file filter
	 */
	static private final FileFilter xmlFileFilter = new RegExprFileFilter(new String[]{".*\\.xml"}, Messages.getString("FileDialogs.xml"));

	/**
	 * XSL file filter
	 */
	static private final FileFilter xslFileFilter = new RegExprFileFilter(new String[]{".*\\.xsl", ".*\\.xslt"}, Messages.getString("FileDialogs.xslt"));

	/**
	 * Zip file filter
	 */
	static private final FileFilter zipFileFilter = new RegExprFileFilter(new String[]{".*\\.zip", ".*\\.jar"}, Messages.getString("FileDialogs.zip"));

	/**
	 * Ser file filter
	 */
	static private final FileFilter serFileFilter = new RegExprFileFilter(new String[]{".*\\.ser", ".*\\.ser\\.zip"}, Messages.getString("FileDialogs.ser"));

	/**
	 * XSL file filter
	 */
	static private final FileFilter propertyFileFilter = new RegExprFileFilter(new String[]{".*\\.properties"}, Messages.getString("FileDialogs.properties"));

	/**
	 * Document file filter
	 */
	static private final FileFilter docFileFilter = new NegRegExprFileFilter(new String[]{".*\\.png", ".*\\.jpg", ".*\\.gif", ".*\\.html", ".*\\.properties", ".*\\.applet"}, Messages.getString("FileDialogs.doc"));

	/**
	 * Get executable
	 *
	 * @param currentDirectory current directory
	 * @return string for executable
	 */
	@Nullable
	static public String getExec(final String currentDirectory)
	{
		return getFile(currentDirectory, FileDialogs.executableFileFilter);
	}

	/**
	 * Get property file
	 *
	 * @param currentDirectory current directory
	 * @return string for property file
	 */
	@Nullable
	static public String getPropertyFile(final String currentDirectory)
	{
		return getFile(currentDirectory, FileDialogs.propertyFileFilter);
	}

	/**
	 * Get Zip file path
	 *
	 * @param currentDirectory current directory
	 * @return string for zip file path
	 */
	@Nullable
	static public String getZip(final String currentDirectory)
	{
		return getFile(currentDirectory, FileDialogs.zipFileFilter);
	}

	/**
	 * Get Zip file path
	 *
	 * @param currentDirectory current directory
	 * @return string for zip file path
	 */
	@Nullable
	static public String getSer(final String currentDirectory)
	{
		return getFile(currentDirectory, FileDialogs.serFileFilter);
	}

	/**
	 * Get XML file path
	 *
	 * @param currentDirectory current directory
	 * @return string for XML file path
	 */
	@Nullable
	static public String getXml(final String currentDirectory)
	{
		return getFile(currentDirectory, FileDialogs.xmlFileFilter);
	}

	/**
	 * Get file path
	 *
	 * @param currentDirectory current directory
	 * @return string for any file path
	 */
	@Nullable
	static public String getAny(final String currentDirectory)
	{
		return getFile(currentDirectory, null); // FileDialogs.anyFileFilter);
	}

	/**
	 * Get file path
	 *
	 * @param currentDirectory current directory
	 * @param fileFilter       file filter
	 * @return string for XML file path
	 */
	@Nullable
	static private String getFile(final String currentDirectory, @Nullable final FileFilter fileFilter)
	{
		@NonNull final JFileChooser chooser = FileDialogs.makeFileChooser();
		FileDialogs.setCurrentDirectory(chooser, currentDirectory);
		if (fileFilter != null)
		{
			chooser.setFileFilter(fileFilter);
		}
		else
		{
			chooser.setFileFilter(docFileFilter);
		}

		if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null))
		{
			try
			{
				return chooser.getSelectedFile().getCanonicalPath();
			}
			catch (final IOException exception)
			{
				// do nothing
			}
		}
		return null;
	}

	/**
	 * Get XML url
	 *
	 * @param currentDirectory current directory
	 * @return string for XML file url
	 */
	@Nullable
	static public String getXmlUrl(final String currentDirectory)
	{
		return getUrl(currentDirectory, FileDialogs.xmlFileFilter);
	}

	/**
	 * Get XSL url
	 *
	 * @param currentDirectory current directory
	 * @return string for XSL file url
	 */
	@Nullable
	static public String getXslUrl(final String currentDirectory)
	{
		return getUrl(currentDirectory, FileDialogs.xslFileFilter);
	}

	/**
	 * Get any url
	 *
	 * @param currentDirectory current directory
	 * @return string for XML file url
	 */
	@Nullable
	static public String getAnyUrl(final String currentDirectory)
	{
		return getUrl(currentDirectory, null);
	}

	/**
	 * Get XML url
	 *
	 * @param currentDirectory current directory
	 * @param fileFilter       file filter
	 * @return string for XML file url
	 */
	@Nullable
	static private String getUrl(final String currentDirectory, @Nullable final FileFilter fileFilter)
	{
		@NonNull final JFileChooser chooser = FileDialogs.makeFileChooser();
		FileDialogs.setCurrentDirectory(chooser, currentDirectory);
		if (fileFilter != null)
		{
			chooser.setFileFilter(fileFilter);
		}
		else
		{
			chooser.setFileFilter(docFileFilter);
		}
		if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null))
		{
			try
			{
				return chooser.getSelectedFile().toURI().toURL().toString();
			}
			catch (final MalformedURLException exception)
			{
				// do nothing
			}
		}
		return null;
	}

	/**
	 * Get directory
	 *
	 * @param currentDirectory current directory
	 * @return string for directory path
	 */
	@Nullable
	static public String getFolder(final String currentDirectory)
	{
		@NonNull final JFileChooser chooser = FileDialogs.makeFolderChooser();
		FileDialogs.setCurrentDirectory(chooser, currentDirectory);
		if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null))
		{
			return chooser.getSelectedFile().getPath();
		}
		return null;
	}

	/**
	 * Make file chooser
	 *
	 * @return file chooser
	 */
	@NonNull
	static private JFileChooser makeFileChooser()
	{
		@NonNull final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(Messages.getString("FileDialogs.title"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		return chooser;
	}

	/**
	 * Make folder chooser
	 *
	 * @return folder chooser
	 */
	@NonNull
	static private JFileChooser makeFolderChooser()
	{
		@NonNull final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(Messages.getString("FileDialogs.title"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
		{
			@Override
			public boolean accept(@NonNull final File file)
			{
				return file.isDirectory();
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return Messages.getString("FileDialogs.folder");
			}
		});
		return chooser;
	}

	/**
	 * Set current directory for file chooser
	 *
	 * @param chooser          file chooser
	 * @param currentDirectory directory to set as current
	 */
	static private void setCurrentDirectory(@NonNull final JFileChooser chooser, @Nullable final String currentDirectory)
	{
		if (currentDirectory == null || currentDirectory.isEmpty())
		{
			return;
		}
		@Nullable File directory = null;
		if (currentDirectory.startsWith("file:"))
		{
			try
			{
				directory = new File(new URI(currentDirectory));
			}
			catch (final URISyntaxException exception)
			{
				// do nothing
			}
		}
		else
		{
			directory = new File(currentDirectory);
		}
		chooser.setCurrentDirectory(directory);
	}
}
