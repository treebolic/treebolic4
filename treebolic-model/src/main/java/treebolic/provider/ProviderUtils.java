/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package treebolic.provider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Provider utils
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class ProviderUtils
{
	private static final boolean DEBUG = false;

	/**
	 * Make URL
	 *
	 * @param source  source
	 * @param base    base
	 * @param extras  extras
	 * @param context context
	 * @return url
	 */
	@Nullable
	static public URL makeURL(@Nullable final String source, final URL base, @SuppressWarnings("unused") @Nullable final Properties extras, @Nullable final IProviderContext context)
	{
		if (source == null)
		{
			if (DEBUG && context != null)
			{
				context.warn("URL= null (null source)");
			}
			return null;
		}

		// try to consider it well-formed full-fledged url
		try
		{
			@NonNull final URL url = new URL(source);
			if (DEBUG && context != null)
			{
				context.message("URL=" + url);
			}
			return url;
		}
		catch (@NonNull final MalformedURLException ignored)
		{
			// do nothing
		}

		// default to source relative to a base
		try
		{
			@NonNull final URL url = new URL(base, source);
			if (DEBUG && context != null)
			{
				context.message("URL=" + url); // + " from BASE URL=" + base.toString());
			}
			return url;
		}
		catch (@NonNull final MalformedURLException ignored)
		{
			// do nothing
		}
		if (DEBUG && context != null)
		{
			context.warn("URL= null (fail)");
		}
		return null;
	}
}
