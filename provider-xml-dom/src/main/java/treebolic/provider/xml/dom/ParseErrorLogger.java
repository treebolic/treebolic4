/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.provider.xml.dom;

import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import treebolic.annotations.NonNull;

/**
 * Parse error logger
 *
 * @author Bernard Bou
 */
public class ParseErrorLogger extends ParseErrorHandler
{
	/**
	 * Printer
	 */
	private PrintWriter writer;

	/**
	 * Output stream
	 */
	private OutputStream outputStream;

	/**
	 * Constructor
	 */
	public ParseErrorLogger()
	{
		// do nothing
	}

	/**
	 * Log
	 *
	 * @param level level
	 * @param e     exception
	 */
	private void log(final String level, @NonNull final SAXParseException e)
	{
		if (this.outputStream == null)
		{
			this.outputStream = System.err;
		}
		if (this.writer == null)
		{
			this.writer = new PrintWriter(this.outputStream, true);
		}

		this.writer.println(level);
		this.writer.println(" uri: " + e.getSystemId() + "(" + e.getLineNumber() + "," + e.getColumnNumber() + ")");
		this.writer.println(" message: " + e.getMessage());
	}

	/**
	 * Terminate
	 */
	public void terminate()
	{
		try
		{
			if (this.writer != null && (this.fatalErrors != 0 || this.errors != 0 || this.warnings != 0))
			{
				this.writer.println("Fatal Errors:" + this.fatalErrors);
				this.writer.println("Errors:" + this.errors);
				this.writer.println("Warnings:" + this.warnings);
			}
			if (this.outputStream != null)
			{
				this.outputStream.close();
			}
		}
		catch (@NonNull final IOException ignored)
		{
			// do nothing
		}
	}

	@Override
	public void error(@NonNull final SAXParseException e) throws SAXParseException
	{
		super.error(e);
		log("Recoverable Error", e);
	}

	@Override
	public void warning(@NonNull final SAXParseException e) throws SAXParseException
	{
		super.warning(e);
		log("Warning", e);
	}

	@Override
	public void fatalError(@NonNull final SAXParseException e) throws SAXParseException
	{
		super.fatalError(e);
		log("FATAL ERROR", e);
	}
}
