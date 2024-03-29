/**
 * Title : Treebolic Text provider
 * Description : Treebolic Text provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.provider.text.pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.model.*;
import treebolic.model.graph.Converter;
import treebolic.model.graph.GraphEdge;
import treebolic.model.graph.MutableGraph;
import treebolic.model.graph.MutableGraphNode;
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

	@Override
	public void setContext(final IProviderContext context)
	{
		this.context = context;
	}

	@Override
	public void setLocator(final ILocator locator)
	{
		// do not need
	}

	@Override
	public void setHandle(final Object handle)
	{
		// do not need
	}

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
				System.err.println("SETTING " + e);
			}
		}
		return new Model(tree, settings);
	}

	@Override
	@Nullable
	public Tree makeTree(final String source0, final URL base, @Nullable final Properties parameters, final boolean checkRecursion)
	{
		// get text file
		String source = source0;
		if (source == null && parameters != null)
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
		@NonNull final MutableGraph graph = new MutableGraph();
		@NonNull final Map<String, MutableGraphNode> map = new HashMap<>();
		try (@NonNull InputStream is = url.openStream(); @NonNull BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
		{
			// parse lines
			String line;
			for (int l = 1; (line = reader.readLine()) != null; l++)
			{
				if (line.matches("^\\s*$") || line.startsWith("#"))
				{
					continue;
				}
				processLine(line, l, graph, map);
			}
			return new Converter<MutableGraphNode>().graphToTree(graph);
		}
		catch (final Exception ex)
		{
			System.err.println("Text graph parser: " + ex);
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
		@NonNull final MutableGraph graph = new MutableGraph();
		@NonNull final Map<String, MutableGraphNode> map = new HashMap<>();
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
				processLine(line, l, graph, map);
			}
			return new Converter<MutableGraphNode>().graphToTree(graph);
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
	 * @param line line
	 * @param lineNumber line number
	 * @param map node stack
	 */
	private void processLine(@NonNull final String line, final int lineNumber, @NonNull final MutableGraph graph, @NonNull final Map<String, MutableGraphNode> map)
	{
		// parse
		// parentid\tid\tlabel\tbackground\tforeground\timg\tlink\tcontent
		// parent\tchild
		@NonNull final String[] fields = line.split("\t", 8);
		if (fields.length < 2)
		{
			return;
		}

		// parent child ids
		@NonNull final String parentId = fields[0].trim();
		@NonNull final String childId = fields[1].trim();
		if (childId.isEmpty())
		{
			System.err.println("No Id " + lineNumber);
		}

		// nodes
		@Nullable MutableGraphNode parent = null;
		if (!parentId.isEmpty())
		{
			parent = map.get(parentId);
			if (parent == null)
			{
				System.err.println("Dummy parent line " + lineNumber);
				parent = new MutableGraphNode(parentId);
				parent.setLabel(parentId);
				map.put(parentId, parent);
				graph.add(parent);
			}
		}
		MutableGraphNode child = map.get(childId);
		if (child == null)
		{
			child = new MutableGraphNode(childId);
			child.setLabel(childId);
			map.put(childId, child);
			graph.add(child);

			// data
			if (fields.length >= 3 && !fields[2].isEmpty())
			{
				child.setLabel(fields[2]);
			}
			if (fields.length >= 4 && !fields[3].isEmpty())
			{
				child.setBackColor(Utils.parseColor(fields[3]));
			}
			if (fields.length >= 5 && !fields[4].isEmpty())
			{
				child.setForeColor(Utils.parseColor(fields[4]));
			}
			if (fields.length >= 6 && !fields[5].isEmpty())
			{
				child.setImageFile(fields[5]);
			}
			if (fields.length >= 7 && !fields[6].isEmpty())
			{
				child.setLink(fields[6]);
			}
			if (fields.length >= 8 && !fields[7].isEmpty())
			{
				child.setContent(fields[7]);
			}
		}

		// edge
		if (parent != null)
		{
			@NonNull final GraphEdge graphEdge = new GraphEdge(parent, child, true);
			graphEdge.setUserData(new MutableEdge(parent, child));
			graph.add(graphEdge);
		}
	}
}
