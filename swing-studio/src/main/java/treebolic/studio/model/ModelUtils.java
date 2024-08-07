/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.model.*;
import treebolic.studio.Pair;

/**
 * Model utilities
 *
 * @author Bernard Bou
 */
public class ModelUtils
{
	// I D . F A C T O R Y

	/**
	 * Randomize used to generate ids
	 */
	static private final Random RANDOMIZER = new Random();

	/**
	 * Make node id
	 *
	 * @return node id
	 */
	@NonNull
	static public String makeNodeId()
	{
		return "X" + Long.toHexString(ModelUtils.RANDOMIZER.nextLong());
	}

	// M A K E . D E F A U L T

	/**
	 * Make default root node
	 *
	 * @return root node
	 */
	@NonNull
	public static INode makeDefaultTree()
	{
		@NonNull final String[][] data = { //
				{"id1", "one\n1", "id11", "eleven\n11", "id12", "twelve\n12", "id13", "thirteen\n13", "id14", "fourteen\n14"}, //
				{"id2", "two\n2", "id21", "twenty-one\n21", "id22", "twenty-two\n22", "id23", "twenty-three\n23"}, //
				{"id3", "three\n3", "id31", "thirty-one\n31", "id32", "thirty-two\n32"}, //
				{"id4", "four\n4", "id41", "forty-one\n41"}, //
				{"id5", "five\n5"}};
		@NonNull final TreeMutableNode root = new TreeMutableNode(null, "root");
		root.setLabel("root");
		root.setBackColor(Colors.ORANGE);
		root.setForeColor(Colors.BLACK);
		for (@NonNull final String[] nodeData : data)
		{
			@NonNull final TreeMutableNode node = new TreeMutableNode(root, nodeData[0]);
			node.setLabel(nodeData[1]);
			for (int i = 2; i < nodeData.length; i += 2)
			{
				@NonNull final TreeMutableNode childNode = new TreeMutableNode(node, nodeData[i]);
				childNode.setLabel(nodeData[i + 1]);
			}
		}
		return root;
	}

	// T O . M U T A B L E

	/**
	 * Make of copy of this model with mutable nodes
	 *
	 * @param model model
	 * @return equivalent model with mutable nodes
	 */
	@NonNull
	static public Pair<Model, Map<String, MutableNode>> toMutable(@NonNull final Model model)
	{
		@NonNull final Map<String, MutableNode> idToNodeMap = new TreeMap<>();
		@NonNull final Map<INode, MutableNode> oldToNewNodeMap = new HashMap<>();

		// nodes
		@NonNull final INode root = ModelUtils.toMutable(model.tree.getRoot(), idToNodeMap, oldToNewNodeMap);

		// edges
		@Nullable List<IEdge> edges = null;
		final List<IEdge> edges0 = model.tree.getEdges();
		if (edges0 != null)
		{
			edges = new ArrayList<>();
			for (@NonNull final IEdge edge0 : edges0)
			{
				@NonNull final TreeMutableEdge edge = new TreeMutableEdge(edge0);
				edge.setFrom(oldToNewNodeMap.get(edge0.getFrom()));
				edge.setTo(oldToNewNodeMap.get(edge0.getTo()));
				edges.add(edge);
			}
		}
		return new Pair<>(new Model(new Tree(root, edges), model.settings), idToNodeMap);
	}

	/**
	 * Make of copy of this model with mutable nodes
	 *
	 * @param node0           start node
	 * @param idToNodeMap     id to node map
	 * @param oldToNewNodeMap old node to new node map
	 * @return equivalent model with mutable nodes
	 */
	@NonNull
	static public TreeMutableNode toMutable(@NonNull final INode node0, @NonNull final Map<String, MutableNode> idToNodeMap, @NonNull final Map<INode, MutableNode> oldToNewNodeMap)
	{
		// this node
		@NonNull final TreeMutableNode node = new TreeMutableNode(node0);
		idToNodeMap.put(node.getId(), node);
		oldToNewNodeMap.put(node0, node);

		// children
		@Nullable final List<INode> children0 = node0.getChildren();
		if (children0 != null)
		{
			@NonNull final List<INode> children = new ArrayList<>();
			for (@NonNull final INode childNode0 : children0)
			{
				@NonNull final TreeMutableNode child = ModelUtils.toMutable(childNode0, idToNodeMap, oldToNewNodeMap);

				// tree link
				child.setParent(node);
				children.add(child);
			}
			node.setChildren(children);
		}

		return node;
	}

	// M A K E . I D . M A P

	/**
	 * Make id to node map for this model
	 *
	 * @param model model
	 * @return id to node map
	 */
	@NonNull
	static public Map<String, MutableNode> makeIdToNodeMap(@NonNull final Model model)
	{
		@NonNull final Map<String, MutableNode> idToNodeMap = new TreeMap<>();
		ModelUtils.makeIdToNodeMap((MutableNode) model.tree.getRoot(), idToNodeMap);
		return idToNodeMap;
	}

	/**
	 * Make id to node map for this model
	 *
	 * @param node        start node
	 * @param idToNodeMap id to node result map
	 */
	static private void makeIdToNodeMap(@NonNull final MutableNode node, @NonNull final Map<String, MutableNode> idToNodeMap)
	{
		// this node
		idToNodeMap.put(node.getId(), node);

		// recurse
		@NonNull final List<INode> children = node.getChildren();
		for (final INode childNode : children)
		{
			ModelUtils.makeIdToNodeMap((MutableNode) childNode, idToNodeMap);
		}
	}

	// N O R M A L I Z E . I D S

	/**
	 * Normalize ids in this model
	 *
	 * @param model  model
	 * @param prefix id prefix
	 * @return new id to node map
	 */
	@NonNull
	static public Map<String, MutableNode> normalizeIds(@NonNull final Model model, final String prefix)
	{
		@NonNull final Map<String, MutableNode> idToNodeMap = new TreeMap<>();
		@NonNull final Map<String, String> oldIdToNewIdMap = new HashMap<>();
		ModelUtils.normalizeId((TreeMutableNode) model.tree.getRoot(), prefix, idToNodeMap, oldIdToNewIdMap);
		ModelUtils.normalizeLinks((TreeMutableNode) model.tree.getRoot(), oldIdToNewIdMap);

		return idToNodeMap;
	}

	/**
	 * Normalize ids in model
	 *
	 * @param node            node
	 * @param prefix          id prefix
	 * @param idToNodeMap     id to node result map
	 * @param oldIdToNewIdMap old id to new id result map
	 */
	static private void normalizeId(@NonNull final TreeMutableNode node, final String prefix, @NonNull final Map<String, MutableNode> idToNodeMap, @NonNull final Map<String, String> oldIdToNewIdMap)
	{
		// this node
		@Nullable final String oldId = node.getId();
		final String id = node.getParent() == null ? "root" : prefix;
		node.setId(id);

		// record in maps
		idToNodeMap.put(id, node);
		oldIdToNewIdMap.put(oldId, id);

		// recurse
		@NonNull final List<INode> children = node.getChildren();
		int i = 0;
		for (final INode childNode : children)
		{
			ModelUtils.normalizeId((TreeMutableNode) childNode, prefix + "-" + i++, idToNodeMap, oldIdToNewIdMap);
		}
	}

	/**
	 * Normalize node links
	 *
	 * @param node            start node
	 * @param oldIdToNewIdMap old id to new id map
	 */
	static private void normalizeLinks(@NonNull final TreeMutableNode node, @NonNull final Map<String, String> oldIdToNewIdMap)
	{
		// this node
		@Nullable String link = node.getLink();
		if (link != null && !link.isEmpty())
		{
			link = ModelUtils.decode(link);
			final int pos = link.indexOf('#');
			if (pos != -1 && link.length() > pos + 1)
			{
				@NonNull final String oldId = link.substring(pos + 1);
				final String newId = oldIdToNewIdMap.get(oldId);
				if (newId != null)
				{
					link = link.substring(0, pos) + newId;
					node.setLink('#' + ModelUtils.encode(link));
				}
			}
		}

		// recurse
		@NonNull final List<INode> children = node.getChildren();
		for (final INode childNode : children)
		{
			ModelUtils.normalizeLinks((TreeMutableNode) childNode, oldIdToNewIdMap);
		}
	}

	// I M A G E . M A P

	/**
	 * Map of images in this model to their locations
	 *
	 * @param model model
	 * @return map of images to locations
	 */
	@NonNull
	static public Map<String, SortedSet<String>> getImageMap(@NonNull final Model model)
	{
		@NonNull final Map<String, SortedSet<String>> map = new TreeMap<>();

		@Nullable String imageFile;

		// defaults
		if (model.settings != null)
		{
			imageFile = model.settings.backgroundImageFile;
			if (imageFile != null && !imageFile.isEmpty())
			{
				ModelUtils.putReferenceMapKeyValue(map, imageFile, "background");
			}

			imageFile = model.settings.defaultNodeImage;
			if (imageFile != null && !imageFile.isEmpty())
			{
				ModelUtils.putReferenceMapKeyValue(map, imageFile, "default node");
			}

			imageFile = model.settings.defaultTreeEdgeImage;
			if (imageFile != null && !imageFile.isEmpty())
			{
				ModelUtils.putReferenceMapKeyValue(map, imageFile, "default tree-edge");
			}

			imageFile = model.settings.defaultEdgeImage;
			if (imageFile != null && !imageFile.isEmpty())
			{
				ModelUtils.putReferenceMapKeyValue(map, imageFile, "default edge");
			}
		}

		// nodes
		ModelUtils.getImageMap(model.tree.getRoot(), map);

		// edges
		final List<IEdge> edges = model.tree.getEdges();
		if (edges != null)
		{
			for (@NonNull final IEdge edge : edges)
			{
				imageFile = edge.getImageFile();
				if (imageFile != null && !imageFile.isEmpty())
				{
					ModelUtils.putReferenceMapKeyValue(map, imageFile, "edge:" + edge.getFrom().getId() + "->" + edge.getTo().getId());
				}
			}
		}
		return map;
	}

	/**
	 * Recursive traversal of nodes for images
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getImageMap(@NonNull final INode node, @NonNull final Map<String, SortedSet<String>> map)
	{
		// this node
		@Nullable String imageFile = node.getImageFile();
		if (imageFile != null && !imageFile.isEmpty())
		{
			ModelUtils.putReferenceMapKeyValue(map, imageFile, "node:" + node.getId());
		}
		imageFile = node.getEdgeImageFile();
		if (imageFile != null && !imageFile.isEmpty())
		{
			ModelUtils.putReferenceMapKeyValue(map, imageFile, "treeedge:" + node.getId());
		}

		// recurse
		@Nullable final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (@NonNull final INode child : children)
			{
				ModelUtils.getImageMap(child, map);
			}
		}
	}

	// M O U N T . M A P

	/**
	 * Map of mounts in this model to their locations
	 *
	 * @param model model
	 * @return map of mounts to locations
	 */
	@NonNull
	static public Map<String, SortedSet<String>> getMountMap(@NonNull final Model model)
	{
		@NonNull final Map<String, SortedSet<String>> map = new TreeMap<>();
		ModelUtils.getMountMap(model.tree.getRoot(), map);
		return map;
	}

	/**
	 * Recursive traversal of nodes for mounts
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getMountMap(@NonNull final INode node, @NonNull final Map<String, SortedSet<String>> map)
	{
		// this node
		@Nullable final MountPoint mountPoint = node.getMountPoint();
		if (mountPoint instanceof MountPoint.Mounting)
		{
			@NonNull final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) mountPoint;
			ModelUtils.putReferenceMapKeyValue(map, mountingPoint.url, "node:" + node.getId());
		}

		// recurse
		@Nullable final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (@NonNull final INode child : children)
			{
				ModelUtils.getMountMap(child, map);
			}
		}
	}

	// M O U N T . M A P

	/**
	 * Map of mounts in this model to their locations
	 *
	 * @param model model
	 * @return map of mounts to locations
	 */
	@NonNull
	static public Map<String, SortedSet<String>> getLinkMap(@NonNull final Model model)
	{
		@NonNull final Map<String, SortedSet<String>> map = new TreeMap<>();
		ModelUtils.getLinkMap(model.tree.getRoot(), map);
		return map;
	}

	/**
	 * Recursive traversal of nodes for mounts
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getLinkMap(@NonNull final INode node, @NonNull final Map<String, SortedSet<String>> map)
	{
		// this node
		@Nullable final String link = node.getLink();
		if (link != null && !link.isEmpty())
		{
			ModelUtils.putReferenceMapKeyValue(map, link, "node:" + node.getId());
		}

		// recurse
		@Nullable final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (@NonNull final INode child : children)
			{
				ModelUtils.getLinkMap(child, map);
			}
		}
	}

	// I D . M A P

	/**
	 * Map of ids in this model to their locations
	 *
	 * @param model model
	 * @return map of ids to locations
	 */
	@NonNull
	static public Map<String, SortedSet<String>> getIdMap(@NonNull final Model model)
	{
		@NonNull final Map<String, SortedSet<String>> map = new TreeMap<>();
		ModelUtils.getIdMap(model.tree.getRoot(), map);
		return map;
	}

	/**
	 * Recursive traversal of nodes for ids
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getIdMap(@NonNull final INode node, @NonNull final Map<String, SortedSet<String>> map)
	{
		// this node
		@Nullable String id = node.getId();
		if (id == null || id.isEmpty())
		{
			id = "null";
		}
		@Nullable final INode parent = node.getParent();
		ModelUtils.putReferenceMapKeyValue(map, id, "label:" + node.getLabel() + " parent:" + (parent == null ? "null" : parent.getId()));

		// recurse
		@Nullable final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (@NonNull final INode child : children)
			{
				ModelUtils.getIdMap(child, map);
			}
		}
	}

	// R E F E R E N C E . H E L P E R

	/**
	 * Put value in multi-valued map
	 *
	 * @param map   map
	 * @param key   key
	 * @param value value
	 */
	static private void putReferenceMapKeyValue(@NonNull final Map<String, SortedSet<String>> map, final String key, final String value)
	{
		@NonNull SortedSet<String> locations = map.computeIfAbsent(key, k -> new TreeSet<>());
		locations.add(value);
	}

	// D E C O D E / E N C O D E

	/**
	 * Decode encoded URL (for display)
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static private String decode(@NonNull final String string)
	{
		try
		{
			return URLDecoder.decode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException e)
		{
			System.err.println("Can't decode " + string + " - " + e);
		}
		return string;
	}

	/**
	 * Encode encoded URL
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static private String encode(@NonNull final String string)
	{
		try
		{
			return URLEncoder.encode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException e)
		{
			System.err.println("Can't decode " + string + " - " + e);
		}
		return string;
	}

	/**
	 * Save settings to file
	 *
	 * @param settings     settings to save
	 * @param propertyFile property file to save to
	 */
	static public void saveSettings(@NonNull final Settings settings, @NonNull final String propertyFile)
	{
		@NonNull final Properties properties = ModelUtils.settingsToProperty(settings);
		try
		{
			properties.store(Files.newOutputStream(Paths.get(propertyFile)), "TREEBOLIC-SETTINGS");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Convert settings to properties
	 *
	 * @param settings settings to convert to properties
	 * @return properties
	 */
	@NonNull
	static public Properties settingsToProperty(@NonNull final Settings settings)
	{
		@NonNull final Properties properties = new Properties();

		// top
		if (settings.hasToolbarFlag != null)
		{
			properties.setProperty(Settings.PROP_TOOLBAR, settings.hasToolbarFlag.toString());
		}
		if (settings.hasStatusbarFlag != null)
		{
			properties.setProperty(Settings.PROP_STATUSBAR, settings.hasStatusbarFlag.toString());
		}
		if (settings.hasPopUpMenuFlag != null)
		{
			properties.setProperty(Settings.PROP_POPUPMENU, settings.hasPopUpMenuFlag.toString());
		}
		if (settings.contentFormat != null)
		{
			properties.setProperty(Settings.PROP_CONTENT_FORMAT, settings.contentFormat);
		}
		if (settings.hasToolTipFlag != null)
		{
			properties.setProperty(Settings.PROP_TOOLTIP, settings.hasToolTipFlag.toString());
		}
		if (settings.toolTipDisplaysContentFlag != null)
		{
			properties.setProperty(Settings.PROP_TOOLTIP_DISPLAYS_CONTENT, settings.toolTipDisplaysContentFlag.toString());
		}
		if (settings.focus != null)
		{
			properties.setProperty(Settings.PROP_FOCUS, settings.focus);
		}
		if (settings.focusOnHoverFlag != null)
		{
			properties.setProperty(Settings.PROP_FOCUS_ON_HOVER, settings.focusOnHoverFlag.toString());
		}
		if (settings.xMoveTo != null)
		{
			properties.setProperty(Settings.PROP_XMOVETO, settings.xMoveTo.toString());
		}
		if (settings.yMoveTo != null)
		{
			properties.setProperty(Settings.PROP_YMOVETO, settings.yMoveTo.toString());
		}
		if (settings.xShift != null)
		{
			properties.setProperty(Settings.PROP_XSHIFT, settings.xShift.toString());
		}
		if (settings.yShift != null)
		{
			properties.setProperty(Settings.PROP_YSHIFT, settings.yShift.toString());
		}

		// tree
		if (settings.orientation != null)
		{
			properties.setProperty(Settings.PROP_ORIENTATION, settings.orientation);
		}
		if (settings.expansion != null)
		{
			properties.setProperty(Settings.PROP_EXPANSION, settings.expansion.toString());
		}
		if (settings.sweep != null)
		{
			properties.setProperty(Settings.PROP_SWEEP, settings.sweep.toString());
		}
		if (settings.preserveOrientationFlag != null)
		{
			properties.setProperty(Settings.PROP_PRESERVE_ORIENTATION, settings.preserveOrientationFlag.toString());
		}
		if (settings.fontFace != null)
		{
			properties.setProperty(Settings.PROP_FONTFACE, settings.fontFace);
		}
		if (settings.fontSize != null)
		{
			properties.setProperty(Settings.PROP_FONTSIZE, settings.fontSize.toString());
		}
		if (settings.downscaleFontsFlag != null)
		{
			properties.setProperty(Settings.PROP_SCALE_FONTS, settings.downscaleFontsFlag.toString());
		}
		if (settings.fontDownscaler != null)
		{
			properties.setProperty(Settings.PROP_FONT_SCALER, Utils.floatsToString(settings.fontDownscaler));
		}
		if (settings.downscaleImagesFlag != null)
		{
			properties.setProperty(Settings.PROP_SCALE_IMAGES, settings.downscaleImagesFlag.toString());
		}
		if (settings.imageDownscaler != null)
		{
			properties.setProperty(Settings.PROP_IMAGE_SCALER, Utils.floatsToString(settings.imageDownscaler));
		}
		if (settings.backColor != null)
		{
			properties.setProperty(Settings.PROP_BACKCOLOR, Utils.colorToString(settings.backColor));
		}
		if (settings.foreColor != null)
		{
			properties.setProperty(Settings.PROP_FORECOLOR, Utils.colorToString(settings.foreColor));
		}
		if (settings.backgroundImageFile != null)
		{
			properties.setProperty(Settings.PROP_BACKGROUND_IMAGE, settings.backgroundImageFile);
		}
		if (settings.edgesAsArcsFlag != null)
		{
			properties.setProperty(Settings.PROP_EDGE_AS_ARC, settings.edgesAsArcsFlag.toString());
		}

		// nodes
		if (settings.nodeBackColor != null)
		{
			properties.setProperty(Settings.PROP_NODE_BACKCOLOR, Utils.colorToString(settings.nodeBackColor));
		}
		if (settings.nodeForeColor != null)
		{
			properties.setProperty(Settings.PROP_NODE_FORECOLOR, Utils.colorToString(settings.nodeForeColor));
		}
		if (settings.defaultNodeImage != null)
		{
			properties.setProperty(Settings.PROP_NODE_IMAGE, settings.defaultNodeImage);
		}
		if (settings.borderFlag != null)
		{
			properties.setProperty(Settings.PROP_NODE_BORDER, settings.borderFlag.toString());
		}
		if (settings.ellipsizeFlag != null)
		{
			properties.setProperty(Settings.PROP_NODE_ELLIPSIZE, settings.ellipsizeFlag.toString());
		}
		if (settings.labelMaxLines != null)
		{
			properties.setProperty(Settings.PROP_NODE_LABEL_MAX_LINES, settings.labelMaxLines.toString());
		}
		if (settings.labelExtraLineFactor != null)
		{
			properties.setProperty(Settings.PROP_NODE_LABEL_EXTRA_LINE_FACTOR, settings.labelExtraLineFactor.toString());
		}

		// edges
		if (settings.defaultEdgeImage != null)
		{
			properties.setProperty(Settings.PROP_EDGE_IMAGE, settings.defaultEdgeImage);
		}
		if (settings.defaultTreeEdgeImage != null)
		{
			properties.setProperty(Settings.PROP_TREE_EDGE_IMAGE, settings.defaultTreeEdgeImage);
		}
		if (settings.edgeColor != null)
		{
			properties.setProperty(Settings.PROP_EDGE_COLOR, Utils.colorToString(settings.edgeColor));
		}
		if (settings.treeEdgeColor != null)
		{
			properties.setProperty(Settings.PROP_TREE_EDGE_COLOR, Utils.colorToString(settings.treeEdgeColor));
		}
		if (settings.edgeStyle != null)
		{
			@Nullable String param;
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.STROKE);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_EDGE_STROKE, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.STROKEWIDTH);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_EDGE_STROKEWIDTH, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.FROMTERMINATOR);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_EDGE_FROMTERMINATOR, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.TOTERMINATOR);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_EDGE_TOTERMINATOR, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.LINE);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_EDGE_LINE, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.HIDDEN);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_EDGE_HIDDEN, param);
			}
		}
		if (settings.treeEdgeStyle != null)
		{
			@Nullable String param;
			param = Utils.toString(settings.treeEdgeStyle, Utils.StyleComponent.STROKE);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_TREE_EDGE_STROKE, param);
			}
			param = Utils.toString(settings.treeEdgeStyle, Utils.StyleComponent.STROKEWIDTH);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_TREE_EDGE_STROKEWIDTH, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.FROMTERMINATOR);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_TREE_EDGE_FROMTERMINATOR, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.TOTERMINATOR);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_TREE_EDGE_TOTERMINATOR, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.LINE);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_TREE_EDGE_LINE, param);
			}
			param = Utils.toString(settings.edgeStyle, Utils.StyleComponent.HIDDEN);
			if (param != null)
			{
				properties.setProperty(Settings.PROP_TREE_EDGE_HIDDEN, param);
			}
		}

		// menu
		int i = 0;
		final char sep = ';';
		if (settings.menu != null)
		{
			for (@NonNull final MenuItem menuItem : settings.menu)
			{
				@NonNull final StringBuilder param = new StringBuilder();
				param.append(menuItem.label);
				param.append(sep);
				if (menuItem.action != null)
				{
					param.append(menuItem.action.toString().toLowerCase());
				}
				param.append(sep);
				if (menuItem.link != null)
				{
					param.append(menuItem.link.toLowerCase());
				}
				param.append(sep);
				if (menuItem.target != null)
				{
					param.append(menuItem.target.toLowerCase());
				}
				param.append(sep);
				if (menuItem.matchTarget != null)
				{
					param.append(menuItem.matchTarget.toLowerCase());
				}
				param.append(sep);
				if (menuItem.matchScope != null)
				{
					param.append(menuItem.matchScope.toString().toLowerCase());
				}
				param.append(sep);
				if (menuItem.matchMode != null)
				{
					param.append(menuItem.matchMode.toString().toLowerCase());
				}
				properties.setProperty(Settings.PROP_MENUITEM + i++, param.toString());
			}
		}
		return properties;
	}
}
