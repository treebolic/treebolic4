/**
 * Title : Treebolic Text provider
 * Description : Treebolic Text provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.provider.text.indent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Properties;

import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.model.*;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.ProviderUtils;

/**
 * Provider for file system
 *
 * @author Bernard Bou
 */
public class Provider implements IProvider
{
	static private final Integer backgroundColor = Colors.WHITE;

	/**
	 * Stack entry
	 */
	private static class StackEntry
	{
		/**
		 * Node
		 */
		public final MutableNode node;

		/**
		 * Node's level
		 */
		public final int level;

		/**
		 * Constructor
		 *
		 * @param node  node
		 * @param level itslevel
		 */
		public StackEntry(final MutableNode node, final int level)
		{
			this.node = node;
			this.level = level;
		}
	}

	/**
	 * Provider context
	 */
	private IProviderContext context;

	/**
	 * Constructor
	 */
	public Provider()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#setContext(treebolic.provider.IProviderContext)
	 */
	@Override
	public void setContext(final IProviderContext context)
	{
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#setLocator(treebolic.ILocator)
	 */
	@Override
	public void setLocator(final ILocator locator)
	{
		// do not need
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#setHandle(java.lang.Object)
	 */
	@Override
	public void setHandle(final Object handle)
	{
		// do not need
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#makeModel(java.lang.String, java.net.URL, java.util.Properties)
	 */
	@Nullable
	@Override
	public Model makeModel(final String source, final URL base, @Nullable final Properties parameters)
	{
		@Nullable final Tree tree = makeTree(source, base, parameters, false);
		if (tree == null)
		{
			return null;
		}
		@Nullable final List<INode> nonRootTree = tree.getRoot().getChildren();
		if (nonRootTree == null)
		{
			return null;
		}

		// settings
		final boolean asTree = nonRootTree.size() < 3;
		@NonNull final Settings settings = new Settings();

		settings.backColor = Provider.backgroundColor;
		settings.nodeBackColor = Colors.WHITE;
		settings.nodeForeColor = Colors.BLACK;
		settings.treeEdgeColor = Colors.GRAY;
		settings.treeEdgeStyle = IEdge.SOLID | IEdge.TOTRIANGLE | IEdge.TOFILL;

		settings.orientation = asTree ? "south" : "radial";
		settings.fontFace = "SansSerif";
		settings.fontSize = 20;
		settings.expansion = .9F;
		settings.sweep = 1.2F;

		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;
		settings.focusOnHoverFlag = true;

		// override settings properties
		Properties properties;
		final String location = parameters == null ? null : parameters.getProperty("settings");
		if (location != null && !location.isEmpty())
		{
			@Nullable final URL url = ProviderUtils.makeURL(location, base, parameters, this.context);
			this.context.progress("Loading ..." + (url != null ? url : location), false);

			try
			{
				properties = url != null ? Utils.load(url) : Utils.load(location);
				this.context.progress("Loaded " + (url != null ? url : location), false);
				settings.load(properties);
			}
			catch (final IOException e)
			{
				this.context.message("Cannot load Settings file <" + (url != null ? url : location) + ">");
			}
			catch (final Exception e)
			{
				System.err.println("SETTINGS: " + e);
			}
		}
		return new Model(tree, settings);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#makeTree(java.lang.String, java.net.URL, java.util.Properties, boolean)
	 */
	@Nullable
	@Override
	public Tree makeTree(final String source0, final URL base, @Nullable final Properties parameters, final boolean checkRecursion)
	{
		// get text file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source");
		}

		if (source != null)
		{
			// URL
			@Nullable final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);
			this.context.progress("Loading ..." + (url != null ? url : source), false);

			// parse
			@Nullable final Tree tree = url != null ? parseTree(url) : parseTree(source);
			if (tree != null)
			{
				this.context.progress("Loaded " + (url != null ? url : source), false);
				return tree;
			}
			else
			{
				this.context.message("Cannot load text file <" + (url != null ? url : source) + ">");
			}
		}
		return null;
	}

	/**
	 * Parse tree
	 *
	 * @param url text file URL
	 * @return tree
	 */
	@Nullable
	private Tree parseTree(@NonNull final URL url)
	{
		// root
		MutableNode rootNode = new MutableNode(null, "root");
		rootNode.setLabel("root");
		rootNode.setBackColor(Colors.RED);
		rootNode.setForeColor(Colors.WHITE);

		@NonNull final Deque<StackEntry> stack = new ArrayDeque<>();
		stack.push(new StackEntry(rootNode, -1));

		try (InputStream is = url.openStream(); @NonNull BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
		{
			// parse lines
			String line;
			for (int l = 1; (line = reader.readLine()) != null; l++)
			{
				if (line.matches("^\\s*$") || line.startsWith("#"))
				{
					continue;
				}
				processLine(line, l, stack);
			}

			// graph
			@NonNull final List<INode> children = rootNode.getChildren();
			if (children.size() == 1)
			{
				rootNode = (MutableNode) children.get(0);
				rootNode.setParent(null);
			}
			return new Tree(rootNode, null);
		}
		catch (final Exception e)
		{
			System.err.println("Text graph parser: " + e);
			return null;
		}
	}

	/**
	 * Parse tree
	 *
	 * @param location text file location
	 * @return tree
	 */
	@Nullable
	private Tree parseTree(@NonNull final String location)
	{
		// root
		MutableNode rootNode = new MutableNode(null, "root");
		rootNode.setLabel("root");
		rootNode.setBackColor(Colors.RED);
		rootNode.setForeColor(Colors.WHITE);

		@NonNull final Deque<StackEntry> stack = new ArrayDeque<>();
		stack.push(new StackEntry(rootNode, -1));

		try (@NonNull InputStream is = Files.newInputStream(Paths.get(location)); @NonNull BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
		{
			// parse lines
			String line;
			for (int l = 1; (line = reader.readLine()) != null; l++)
			{
				if (line.matches("^\\s*$") || line.startsWith("#"))
				{
					continue;
				}
				processLine(line, l, stack);
			}

			// graph
			@NonNull final List<INode> children = rootNode.getChildren();
			if (children.size() == 1)
			{
				rootNode = (MutableNode) children.get(0);
				rootNode.setParent(null);
			}
			return new Tree(rootNode, null);
		}
		catch (final Exception e)
		{
			System.err.println("Text graph parser: " + e);
			return null;
		}
	}

	/**
	 * Process line
	 *
	 * @param line       line
	 * @param lineNumber line number
	 * @param stack      node stack
	 */
	private void processLine(@NonNull final String line, final int lineNumber, @NonNull final Deque<StackEntry> stack)
	{
		final int level = getLevel(line);
		if (level == -1)
		{
			return;
		}

		// stack
		StackEntry entry = stack.peek();
		if (entry == null)
		{
			return;
		}

		int entryLevel = entry.level;

		// parent
		@Nullable MutableNode parent;
		if (level == entryLevel)
		{
			stack.pop();
			entry = stack.peek();
			if (entry == null)
			{
				return;
			}
			parent = entry.node;
		}
		else if (level > entryLevel)
		{
			parent = stack.peek().node;
		}
		else
		{
			while (level <= entryLevel)
			{
				stack.pop();
				entry = stack.peek();
				if (entry == null)
				{
					return;
				}
				entryLevel = entry.level;
			}
			parent = stack.peek().node;
		}

		// parse
		// label:id:backcolor:forecolor:img:link:content
		@NonNull final String[] fields = line.substring(level).split(":", 7);
		final String id = fields.length > 1 ? fields[1] : "N" + lineNumber;

		// new node
		@NonNull final MutableNode node = new MutableNode(parent, id);
		stack.push(new StackEntry(node, level));

		// node data
		node.setLabel(fields[0]);
		if (fields.length > 2)
		{
			node.setBackColor(Utils.stringToColor(fields[2]));
		}
		if (fields.length > 3)
		{
			node.setForeColor(Utils.stringToColor(fields[3]));
		}
		if (fields.length > 4)
		{
			node.setImageFile(fields[4]);
		}
		if (fields.length > 5)
		{
			node.setLink(fields[5]);
		}
		if (fields.length > 6)
		{
			node.setContent(fields[6]);
		}
	}

	/**
	 * Get level (as per indentation)
	 *
	 * @param line line
	 * @return level
	 */
	private int getLevel(@NonNull final String line)
	{
		for (int i = 0; i < line.length(); i++)
		{
			final char c = line.charAt(i);
			if (c != ' ' && c != '\t')
			{
				return i;
			}
		}
		return -1;
	}
}
