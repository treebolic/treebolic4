/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.sax;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import treebolic.ILocator;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.ModelDump;
import treebolic.provider.IProviderContext;

public class TestModel
{
	@Test
	public void testModel() throws MalformedURLException
	{
		final String source = System.getProperty("SOURCE");
		final URL base = new File(System.getProperty("BASE")).toURI().toURL();
		Provider p = new Provider();
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
			@Nullable
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
