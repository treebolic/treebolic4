/**
 * Title : Treebolic File System provider
 * Description : Treebolic File System provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.provider.files;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;

import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.model.*;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.LoadBalancer;

/**
 * Provider for file system
 *
 * @author Bernard Bou
 */
public class Provider implements IProvider, ImageDecorator
{
	// N O D E . I D

	/**
	 * Randomize used to generate ids
	 */
	static private final Random RANDOMIZER = new Random();

	/**
	 * Make node id
	 *
	 * @return node id
	 */
	static public String makeNodeId()
	{
		return "X" + Long.toHexString(Provider.RANDOMIZER.nextLong());
	}

	// S T A T I C . D A T A

	/**
	 * Image index
	 */
	public enum ImageIndex
	{
		/**
		 * FS root
		 */
		ROOT,
		/**
		 * Folder
		 */
		FOLDER,
		/**
		 * File
		 */
		FILE,
		/**
		 * Branch
		 */
		BRANCH
	}

	static final String[] images = new String[]{ //
			"root.png", // ROOT 
			"folder.png", // FOLDER 
			"file.png", // FILE 
			"branch.png", // FILE 
	};

	/**
	 * Background color
	 */
	static private final Integer BACK_COLOR = Colors.WHITE; // 0x3030300xFFF0C0;0xF5F5F0

	/**
	 * Edge color
	 */
	static private final Integer EDGE_COLOR = Colors.DARK_GRAY;


	/**
	 * File background color
	 */
	static private final Integer FILE_BACKCOLOR = 0xFFFF80; // 0x606060

	/**
	 * File foreground color
	 */
	static private final Integer FILE_FORECOLOR = Colors.BLACK;

	/**
	 * File treeedge color
	 */
	static private final Integer FILE_EDGECOLOR = Colors.BLACK;

	/**
	 * File treeedge style
	 */
	static private final int FILE_EDGESTYLE = IEdge.SOLID | IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	/**
	 * Folder background color
	 */
	static private final Integer FOLDER_BACKCOLOR = Colors.ORANGE;

	/**
	 * Folder foreground color
	 */
	static private final Integer FOLDER_FORECOLOR = Colors.BLACK; // Colors.DARK_GRAY;

	/**
	 * Folder treeedge color
	 */
	static private final Integer FOLDER_EDGECOLOR = Colors.BLACK;

	/**
	 * Folder treeedge style
	 */
	static private final int FOLDER_EDGESTYLE = IEdge.SOLID | IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;


	/**
	 * Root Folder background color
	 */
	static private final Integer ROOTFOLDER_BACKCOLOR = 0xFF3000; //

	/**
	 * Root Folder foreground color
	 */
	static private final Integer ROOTFOLDER_FORECOLOR = Colors.WHITE; // Colors.WHITE;0xCCCCGREENCC

	// L O A D B A L A N C I N G

	/**
	 * LoadBalancer : Max children nodes at level 0, 1 .. n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value i holds for level i to n.
	 */
	static private final int[] MAX_AT_LEVEL = {6, 3};

	/**
	 * LoadBalancer : Truncation threshold
	 */
	static private final int LABEL_TRUNCATE_AT = 3;

	/**
	 * LoadBalancer : back color
	 */
	static private final Integer LOADBALANCING_BACKCOLOR = null;

	/**
	 * LoadBalancer : fore color
	 */
	static private final Integer LOADBALANCING_FORECOLOR = null;

	/**
	 * LoadBalancer : edge color
	 */
	static private final Integer LOADBALANCING_EDGECOLOR = Colors.GRAY; //Colors.DARK_GRAY;

	/**
	 * LoadBalancer : image index
	 */
	static private final int LOADBALANCING_IMAGEINDEX = ImageIndex.BRANCH.ordinal(); // -1;

	/**
	 * LoadBalancer : Edge style
	 */
	static private final int LOADBALANCING_EDGE_STYLE = IEdge.DASH | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	/**
	 * Provider context
	 */
	private IProviderContext context;

	/**
	 * Load balancer
	 */
	@SuppressWarnings("WeakerAccess")
	protected final LoadBalancer loadBalancer;

	/**
	 * Constructor
	 */
	public Provider()
	{
		this.loadBalancer = new LoadBalancer(MAX_AT_LEVEL, LABEL_TRUNCATE_AT);
		this.loadBalancer.setGroupNode(null, LOADBALANCING_BACKCOLOR, LOADBALANCING_FORECOLOR, LOADBALANCING_EDGECOLOR, LOADBALANCING_EDGE_STYLE, LOADBALANCING_IMAGEINDEX, null, null);
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

	@Override
	public Model makeModel(final String source, final URL base, final Properties parameters)
	{
		final Tree tree = makeTree(source, base, parameters, false);
		if (tree == null)
		{
			return null;
		}

		// settings
		final List<INode> children = tree.getRoot().getChildren();
		final boolean asTree = children == null || children.size() < 6;
		final Settings settings = new Settings();
		settings.backColor = Provider.BACK_COLOR;
		settings.nodeBackColor = Provider.FILE_BACKCOLOR;
		settings.nodeForeColor = Provider.FILE_FORECOLOR;
		settings.orientation = asTree ? "south" : "radial";  
		settings.yMoveTo = asTree ? -0.25F : 0F;
		// settings.hasToolbarFlag = true;
		settings.fontFace = "SansSerif";
		settings.expansion = .8F;
		settings.sweep = 1.3F;
		settings.ellipsizeFlag = true;
		settings.labelMaxLines = 0;
		settings.labelExtraLineFactor = .66F;
		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;
		settings.focusOnHoverFlag = true;
		settings.treeEdgeColor = Provider.EDGE_COLOR;
		settings.treeEdgeStyle = IEdge.SOLID | IEdge.STROKEDEF;
		return new Model(tree, settings, null);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#makeTree(java.lang.String, java.net.URL, java.util.Properties, boolean)
	 */
	@Override
	public Tree makeTree(final String source0, final URL base, final Properties parameters, final boolean checkRecursion)
	{
		String source = source0;
		if (source == null)
		{
			source = parameters == null ? null : parameters.getProperty("source");
		}
		if (source == null)
		{
			return null;
		}

		// try url syntax
		try
		{
			final URL url = new URL(source);
			final String protocol = url.getProtocol();
			if ("file".equals(protocol))
			{
				source = url.getFile();
			}
		}
		catch (final MalformedURLException e)
		{
			// do nothing
		}

		// try uri syntax
		try
		{
			final URI uri = new URI(source);
			final String scheme = uri.getScheme();
			if ("directory".equals(scheme))
			{
				source = uri.getPath();
			}
		}
		catch (final URISyntaxException e)
		{
			// do nothing
		}

		// root
		final File dir = new File(source);
		if (!dir.exists())
		{
			this.context.warn("Can't get root : " + dir.getName() + " does not exist.");  
			return null;
		}
		if (!dir.isDirectory())
		{
			this.context.warn(" Can't get root : " + dir.getName() + " is not a directory.");  
			return null;
		}

		// graph
		final INode rootNode = makeNode(dir, true);
		return new Tree(rootNode, null);
	}

	// N O D E . F A C T O R Y

	/**
	 * Make node
	 *
	 * @param file   file
	 * @param isRoot whether this file is root
	 * @return node
	 */
	public INode makeNode(final File file, boolean isRoot)
	{
		final TreeMutableNode node = new TreeMutableNode(null, Provider.makeNodeId());

		final String name = file.getName();
		node.setLabel(name);
		node.setTarget(name);
		final StringBuilder sb = new StringBuilder();

		if (file.isDirectory())
		{
			node.setLink("directory:" + file.getPath());
			node.setBackColor(isRoot ? Provider.ROOTFOLDER_BACKCOLOR : Provider.FOLDER_BACKCOLOR);
			node.setForeColor(isRoot ? Provider.ROOTFOLDER_FORECOLOR : Provider.FOLDER_FORECOLOR);
			node.setEdgeStyle(Provider.FOLDER_EDGESTYLE);
			node.setEdgeColor(Provider.FOLDER_EDGECOLOR);
			setNodeImage(node, isRoot ? ImageIndex.ROOT.ordinal() : ImageIndex.FOLDER.ordinal());

			// recurse
			final String[] subFiles = file.list();
			if (subFiles != null)
			{
				final List<INode> childNodes = makeChildNodes(file, subFiles);
				sb.append(subFiles.length).append(" elements<br>"); // 

				// balance load
				final List<INode> balancedNodes = this.loadBalancer.buildHierarchy(childNodes, 0);
				node.addChildren(balancedNodes);
			}

			sb.append("last modified ").append(DateFormat.getDateTimeInstance().format(new Date(file.lastModified()))) //
					.append("<br>").append("permissions ").append(file.canRead() ? 'r' : '-') //
					.append(file.canRead() ? 'w' : '-') //
					.append(file.canExecute() ? 'x' : '-');
		}
		else
		{
			node.setLink(file.toURI().toString());
			node.setEdgeStyle(Provider.FILE_EDGESTYLE);
			node.setEdgeColor(Provider.FILE_EDGECOLOR);
			setNodeImage(node, ImageIndex.FILE.ordinal());
			sb.append(file.length()) //
					.append(" bytes<br>").append("last modified ").append(DateFormat.getDateTimeInstance().format(new Date(file.lastModified()))) //
					.append("<br>").append("permissions ").append(file.canRead() ? 'r' : '-') //
					.append(file.canRead() ? 'w' : '-') //
					.append(file.canExecute() ? 'x' : '-');
		}
		node.setContent(sb.toString());

		return node;
	}

	/**
	 * Make child nodes
	 *
	 * @param dir      dir
	 * @param subFiles subfiles
	 * @return nodes
	 */
	public List<INode> makeChildNodes(final File dir, final String[] subFiles)
	{
		final List<INode> childNodes = new ArrayList<>();
		for (final String subFile : subFiles)
		{
			final File child = new File(dir.getPath() + File.separator + subFile);
			final INode childNode = makeNode(child, false);
			childNodes.add(childNode);
		}
		return childNodes;
	}
	// D E C O R A T E

	private void setNodeImage(@NonNull final MutableNode node, @Nullable final String imageFile, @Nullable final ImageIndex index)
	{
		if (imageFile != null)
		{
			node.setImageFile(imageFile);
		}
		else if (index != null)
		{
			setNodeImage(node, index.ordinal());
		}
	}

	private void setTreeEdgeImage(@NonNull final MutableNode node, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
	{
		if (edgeImageFile != null)
		{
			node.setEdgeImageFile(edgeImageFile);
		}
		else if (index != null)
		{
			setTreeEdgeImage(node, index.ordinal());
		}
	}

	private void setEdgeImage(@NonNull final MutableEdge edge, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
	{
		if (edgeImageFile != null)
		{
			edge.setImageFile(edgeImageFile);
		}
		else if (index != null)
		{
			setEdgeImage(edge, index.ordinal());
		}
	}

	@Override
	public void setNodeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setImageFile(images[index]);
		}
	}

	@Override
	public void setTreeEdgeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setEdgeImageFile(images[index]);
		}
	}

	@Override
	public void setEdgeImage(@NonNull final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			edge.setImageFile(images[index]);
		}
	}
}
