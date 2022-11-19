/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.site;

/**
 * This exception is thrown when an error is encountered during an FTP login operation.
 *
 * @version %I%, %G%
 * @author Jonathan Payne
 */
@SuppressWarnings({})
public class FtpLoginException extends FtpProtocolException
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4359673153154433219L;

	FtpLoginException(final String s)
	{
		super(s);
	}
}
