/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.site;

import java.io.IOException;

/**
 * This exeception is thrown when unexpected results are returned during an FTP session.
 *
 * @version %I%, %G%
 * @author Jonathan Payne
 */
public class FtpProtocolException extends IOException
{
	/**
	 *
	 */
	private static final long serialVersionUID = 464995721359144956L;

	FtpProtocolException(final String s)
	{
		super(s);
	}
}
