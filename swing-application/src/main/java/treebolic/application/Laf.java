/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Look and feel
 *
 * @author Bernard Bou
 */
public class Laf
{
	/**
	 * Initialize look and feel
	 *
	 * @param args command line arguments
	 */
	static public void lookAndFeel(@NonNull final String[] args)
	{
		// "com.incors.plaf.kunststoff.KunststoffLookAndFeel"
		// "com.jgoodies.looks.plastic.PlasticLookAndFeel"
		// "com.jgoodies.looks.windows.WindowsLookAndFeel"
		// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"

		// look and feel
		@Nullable String laf = null;
		boolean customTheme = false;
		for (@NonNull final String arg : args)
		{
			if (arg.startsWith("laf="))
			{
				laf = arg.substring(4);
			}
			if (arg.startsWith("theme="))
			{
				customTheme = Boolean.parseBoolean(arg.substring(6));
			}
		}
		// theme
		if (customTheme)
		{
			Laf.setCustomTheme(laf);
		}

		// bold
		Laf.setBold(false);

		// laf
		if (laf != null && !laf.isEmpty())
		{
			try
			{
				Laf.setLookAndFeel(laf);
			}
			catch (final UnsupportedLookAndFeelException e)
			{
				System.err.println("Unsupported LookAndFeel " + e.getMessage());
				Laf.setDefault();
			}
		}
		else
		{
			if (System.getProperty("swing.defaultlaf") == null)
			{
				Laf.setDefault();
			}
		}
	}

	/**
	 * Java version
	 *
	 * @return float for java version
	 */
	private static float getJavaVersion()
	{
		// String version = Runtime.class.getPackage().getImplementationVersion();
		final String version = System.getProperty("java.version");
		int pos = 0, count = 0;
		for (; pos < version.length() && count < 2; pos++)
		{
			if (version.charAt(pos) == '.')
			{
				count++;
			}
		}
		--pos;
		try
		{
			return Float.parseFloat(version.substring(0, pos));
		}
		catch (final NumberFormatException e)
		{
			//
		}
		return -1;
	}

	/**
	 * Default
	 */
	private static void setDefault()
	{
		final boolean isJava7 = getJavaVersion() >= 1.7F;
		@NonNull final String plafName = isJava7 ? "javax.swing.plaf.nimbus.NimbusLookAndFeel" : "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"; // "javax.swing.plaf.metal.MetalLookAndFeel";
		try
		{
			UIManager.setLookAndFeel(plafName);
		}
		catch (final Exception e2)
		{
			System.err.println("Can't set LookAndFeel");
		}
	}

	/**
	 * Set bold font
	 *
	 * @param haveBold true/false
	 */
	static public void setBold(final boolean haveBold)
	{
		UIManager.put("swing.boldMetal", haveBold);
	}

	/**
	 * Set look and feel
	 *
	 * @param lafName laf name
	 * @return true if successful
	 * @throws UnsupportedLookAndFeelException unsupported laf
	 */
	@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
	static public boolean setLookAndFeel(final String lafName) throws UnsupportedLookAndFeelException
	{
		@Nullable final LookAndFeel laf = Laf.getLaf(lafName);
		UIManager.setLookAndFeel(laf);
		return true;
	}

	/**
	 * Get LAF instance
	 *
	 * @param className LAF class name
	 * @return instance of this class
	 */
	@Nullable
	static LookAndFeel getLaf(final String className)
	{
		try
		{
			@NonNull final Class<?> clazz = Class.forName(className);
			@NonNull final Class<?>[] argsClass = new Class[] {};
			@NonNull final Object[] args = new Object[] {};

			@NonNull final Constructor<?> constructor = clazz.getConstructor(argsClass);
			@NonNull final Object instance = constructor.newInstance(args);
			return (LookAndFeel) instance;
		}
		catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException nfe)
		{
			nfe.printStackTrace();
		}
		return null;
	}

	/**
	 * Set custom theme
	 *
	 * @param lafName laf name
	 * @return true if successful
	 */
	@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
	static public boolean setCustomTheme(final String lafName)
	{
		@NonNull final MetalTheme theme = Laf.makeCustomTheme();
		Laf.setCurrentTheme(lafName, theme);
		return true;
	}

	/**
	 * Set current theme
	 *
	 * @param className LAF class name (may have overridden 'setCurrentTheme()')
	 * @param theme theme
	 */
	static void setCurrentTheme(final String className, @NonNull final MetalTheme theme)
	{
		try
		{
			@NonNull final Class<?> clazz = Class.forName(className);
			@NonNull final Class<?>[] argsClass = new Class[] { MetalTheme.class };
			@NonNull final Method method = clazz.getMethod("setCurrentTheme", argsClass);
			method.invoke(null, theme);
		}
		catch (final Exception e)
		{
			System.err.println("Setting current theme: " + e.getMessage());
			MetalLookAndFeel.setCurrentTheme(theme);
		}
	}

	/**
	 * Make custom golden metal theme
	 *
	 * @return metal theme
	 */
	@NonNull
	static private MetalTheme makeCustomTheme()
	{
		return new DefaultMetalTheme()
		{
			private final ColorUIResource primary1 = new ColorUIResource(83, 74, 43);

			private final ColorUIResource primary2 = new ColorUIResource(154, 149, 131);

			private final ColorUIResource primary3 = new ColorUIResource(229, 227, 222);

			private final ColorUIResource secondary1 = new ColorUIResource(105, 97, 78);

			private final ColorUIResource secondary2 = new ColorUIResource(172, 168, 153);

			private final ColorUIResource secondary3 = new ColorUIResource(236, 233, 216);

			/*
			 * (non-Javadoc)
			 * @see javax.swing.plaf.metal.MetalTheme#getPrimary1()
			 */
			@Override
			public ColorUIResource getPrimary1()
			{
				return this.primary1;
			}

			/*
			 * (non-Javadoc)
			 * @see javax.swing.plaf.metal.MetalTheme#getPrimary2()
			 */
			@Override
			public ColorUIResource getPrimary2()
			{
				return this.primary2;
			}

			/*
			 * (non-Javadoc)
			 * @see javax.swing.plaf.metal.MetalTheme#getPrimary3()
			 */
			@Override
			public ColorUIResource getPrimary3()
			{
				return this.primary3;
			}

			/*
			 * (non-Javadoc)
			 * @see javax.swing.plaf.metal.MetalTheme#getSecondary1()
			 */
			@Override
			public ColorUIResource getSecondary1()
			{
				return this.secondary1;
			}

			/*
			 * (non-Javadoc)
			 * @see javax.swing.plaf.metal.MetalTheme#getSecondary2()
			 */
			@Override
			public ColorUIResource getSecondary2()
			{
				return this.secondary2;
			}

			/*
			 * (non-Javadoc)
			 * @see javax.swing.plaf.metal.MetalTheme#getSecondary3()
			 */
			@Override
			public ColorUIResource getSecondary3()
			{
				return this.secondary3;
			}
		};
	}
}
