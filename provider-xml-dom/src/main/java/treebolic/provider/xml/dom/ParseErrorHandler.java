/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.dom;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Parse error handler
 *
 * @author Bernard Bou
 */
class ParseErrorHandler implements ErrorHandler
{
	/**
	 * Warning count
	 */
	@SuppressWarnings("WeakerAccess")
	protected int warnings;

	/**
	 * Error count
	 */
	@SuppressWarnings("WeakerAccess")
	protected final int errors;

	/**
	 * Fatal error count
	 */
	@SuppressWarnings("WeakerAccess")
	protected int fatalErrors;

	/**
	 * Constructor
	 */
	@SuppressWarnings("WeakerAccess")
	protected ParseErrorHandler()
	{
		this.warnings = 0;
		this.errors = 0;
		this.fatalErrors = 0;
	}

	@Override
	public void error(final SAXParseException e) throws SAXParseException
	{
		this.fatalErrors++;
	}

	@Override
	public void warning(final SAXParseException e) throws SAXParseException
	{
		this.warnings++;
	}

	@Override
	public void fatalError(final SAXParseException e) throws SAXParseException
	{
		this.fatalErrors++;
	}
}
