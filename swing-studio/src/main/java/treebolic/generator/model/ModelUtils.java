/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import treebolic.generator.Pair;
import treebolic.glue.Color;
import treebolic.model.*;

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
	public static INode makeDefaultTree()
	{
		final String[][] data = { //
				{"id1", "one\n1", "id11", "eleven\n11", "id12", "twelve\n12", "id13", "thirteen\n13", "id14", "fourteen\n14"},          //$NON-NLS-10$
				{"id2", "two\n2", "id21", "twenty-one\n21", "id22", "twenty-two\n22", "id23", "twenty-three\n23"}, {"id3", "three\n3", "id31", "thirty-one\n31", "id32", "thirty-two\n32"}, {"id4", "four\n4", "id41", "forty-one\n41"}, {"id5", "five\n5"}};
		final TreeMutableNode root = new TreeMutableNode(null, "root");
		root.setLabel("root");
		root.setBackColor(Color.ORANGE);
		root.setForeColor(Color.BLACK);
		for (final String[] nodeData : data)
		{
			final TreeMutableNode node = new TreeMutableNode(root, nodeData[0]);
			node.setLabel(nodeData[1]);
			for (int i = 2; i < nodeData.length; i += 2)
			{
				final TreeMutableNode childNode = new TreeMutableNode(node, nodeData[i]);
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
	static public Pair<Model, Map<String, MutableNode>> toMutable(final Model model)
	{
		final Map<String, MutableNode> idToNodeMap = new TreeMap<>();
		final Map<INode, MutableNode> oldToNewNodeMap = new HashMap<>();

		// nodes
		final INode root = ModelUtils.toMutable(model.tree.getRoot(), idToNodeMap, oldToNewNodeMap);

		// edges
		List<IEdge> edges = null;
		final List<IEdge> edges0 = model.tree.getEdges();
		if (edges0 != null)
		{
			edges = new ArrayList<>();
			for (final IEdge edge0 : edges0)
			{
				final TreeMutableEdge edge = new TreeMutableEdge(edge0);
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
	static public TreeMutableNode toMutable(final INode node0, final Map<String, MutableNode> idToNodeMap, final Map<INode, MutableNode> oldToNewNodeMap)
	{
		// this node
		final TreeMutableNode node = new TreeMutableNode(node0);
		idToNodeMap.put(node.getId(), node);
		oldToNewNodeMap.put(node0, node);

		// children
		final List<INode> children0 = node0.getChildren();
		if (children0 != null)
		{
			final List<INode> children = new ArrayList<>();
			for (final INode childNode0 : children0)
			{
				final TreeMutableNode child = ModelUtils.toMutable(childNode0, idToNodeMap, oldToNewNodeMap);

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
	static public Map<String, MutableNode> makeIdToNodeMap(final Model model)
	{
		final Map<String, MutableNode> idToNodeMap = new TreeMap<>();
		ModelUtils.makeIdToNodeMap((MutableNode) model.tree.getRoot(), idToNodeMap);
		return idToNodeMap;
	}

	/**
	 * Make id to node map for this model
	 *
	 * @param node        start node
	 * @param idToNodeMap id to node result map
	 */
	static private void makeIdToNodeMap(final MutableNode node, final Map<String, MutableNode> idToNodeMap)
	{
		// this node
		idToNodeMap.put(node.getId(), node);

		// recurse
		final List<INode> children = node.getChildren();
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
	static public Map<String, MutableNode> normalizeIds(final Model model, final String prefix)
	{
		final Map<String, MutableNode> idToNodeMap = new TreeMap<>();
		final Map<String, String> oldIdToNewIdMap = new HashMap<>();
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
	static private void normalizeId(final TreeMutableNode node, final String prefix, final Map<String, MutableNode> idToNodeMap, final Map<String, String> oldIdToNewIdMap)
	{
		// this node
		final String oldId = node.getId();
		final String id = node.getParent() == null ? "root" : prefix;
		node.setId(id);

		// record in maps
		idToNodeMap.put(id, node);
		oldIdToNewIdMap.put(oldId, id);

		// recurse
		final List<INode> children = node.getChildren();
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
	static private void normalizeLinks(final TreeMutableNode node, final Map<String, String> oldIdToNewIdMap)
	{
		// this node
		String link = node.getLink();
		if (link != null && !link.isEmpty())
		{
			link = ModelUtils.decode(link);
			final int pos = link.indexOf('#');
			if (pos != -1 && link.length() > pos + 1)
			{
				final String oldId = link.substring(pos + 1);
				final String newId = oldIdToNewIdMap.get(oldId);
				if (newId != null)
				{
					link = link.substring(0, pos) + newId;
					node.setLink('#' + ModelUtils.encode(link));
				}
			}
		}

		// recurse
		final List<INode> children = node.getChildren();
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
	static public Map<String, SortedSet<String>> getImageMap(final Model model)
	{
		final Map<String, SortedSet<String>> map = new TreeMap<>();

		String imageFile;

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
			for (final IEdge edge : edges)
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
	static private void getImageMap(final INode node, final Map<String, SortedSet<String>> map)
	{
		// this node
		String imageFile = node.getImageFile();
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
		final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (final INode child : children)
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
	static public Map<String, SortedSet<String>> getMountMap(final Model model)
	{
		final Map<String, SortedSet<String>> map = new TreeMap<>();
		ModelUtils.getMountMap(model.tree.getRoot(), map);
		return map;
	}

	/**
	 * Recursive traversal of nodes for mounts
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getMountMap(final INode node, final Map<String, SortedSet<String>> map)
	{
		// this node
		final MountPoint mountPoint = node.getMountPoint();
		if (mountPoint instanceof MountPoint.Mounting)
		{
			final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) mountPoint;
			ModelUtils.putReferenceMapKeyValue(map, mountingPoint.url, "node:" + node.getId());
		}

		// recurse
		final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (final INode child : children)
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
	static public Map<String, SortedSet<String>> getLinkMap(final Model model)
	{
		final Map<String, SortedSet<String>> map = new TreeMap<>();
		ModelUtils.getLinkMap(model.tree.getRoot(), map);
		return map;
	}

	/**
	 * Recursive traversal of nodes for mounts
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getLinkMap(final INode node, final Map<String, SortedSet<String>> map)
	{
		// this node
		final String link = node.getLink();
		if (link != null && !link.isEmpty())
		{
			ModelUtils.putReferenceMapKeyValue(map, link, "node:" + node.getId());
		}

		// recurse
		final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (final INode child : children)
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
	static public Map<String, SortedSet<String>> getIdMap(final Model model)
	{
		final Map<String, SortedSet<String>> map = new TreeMap<>();
		ModelUtils.getIdMap(model.tree.getRoot(), map);
		return map;
	}

	/**
	 * Recursive traversal of nodes for ids
	 *
	 * @param node start node
	 * @param map  map to collect results
	 */
	static private void getIdMap(final INode node, final Map<String, SortedSet<String>> map)
	{
		// this node
		String id = node.getId();
		if (id == null || id.isEmpty())
		{
			id = "null";
		}
		final INode parent = node.getParent();
		ModelUtils.putReferenceMapKeyValue(map, id, "label:" + node.getLabel() + " parent:" + (parent == null ? "null" : parent.getId()));

		// recurse
		final List<INode> children = node.getChildren();
		if (children != null)
		{
			for (final INode child : children)
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
	static private void putReferenceMapKeyValue(final Map<String, SortedSet<String>> map, final String key, final String value)
	{
		SortedSet<String> locations = map.computeIfAbsent(key, k -> new TreeSet<>());
		locations.add(value);
	}

	// D E C O D E / E N C O D E

	/**
	 * Decode encoded URL (for display)
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static private String decode(final String string)
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
	static private String encode(final String string)
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
	static public void saveSettings(final Settings settings, final String propertyFile)
	{
		final Properties properties = ModelUtils.settingsToProperty(settings);
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
	static public Properties settingsToProperty(final Settings settings)
	{
		final Properties properties = new Properties();

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
			String param;
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
			String param;
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
			for (final MenuItem menuItem : settings.menu)
			{
				final StringBuilder param = new StringBuilder();
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
