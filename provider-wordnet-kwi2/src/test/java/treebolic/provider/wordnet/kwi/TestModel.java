/*
 * Copyright (c) 2023-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi;

import org.junit.jupiter.api.Test;
import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.ModelDump;
import treebolic.provider.IProviderContext;
import treebolic.provider.wordnet.kwi.compact.Provider2;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Test treebolic model
 */
public class TestModel
{
	/**
	 * Test treebolic model
	 *
	 * @throws MalformedURLException malformed URL exception
	 */
	@Test
	public void testModel() throws IOException
	{
		final String source = System.getProperty("SOURCE");
		final URL base = new File(System.getProperty("BASE")).toURI().toURL();
		Provider2 p = new Provider2();
		p.setContext(new IProviderContext()
		{
			@Override
			public void message(final String message)
			{
				System.err.println(message);
			}

			@Override
			public void warn(final String message)
			{
				System.err.println(message);
			}

			@Override
			public void progress(final String message, final boolean fail)
			{
				System.err.println(message);
			}
		});
		p.setLocator(new ILocator()
		{
			@NonNull
			@Override
			public URL getBase()
			{
				return base;
			}

			@Nullable
			@Override
			public URL getImagesBase()
			{
				try
				{
					return new URL(base, "data");
				}
				catch (MalformedURLException e)
				{
					return null;
				}
			}
		});
		final Properties parameters = new Properties();
		Model model = p.makeModel(source, base, parameters);
		System.out.println(ModelDump.toString(model));
	}
}
