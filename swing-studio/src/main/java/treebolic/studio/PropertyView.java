/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

import java.util.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;
import treebolic.model.Types.MatchMode;
import treebolic.model.Types.MatchScope;
import treebolic.propertyview.Floats;
import treebolic.propertyview.SelectListener;
import treebolic.studio.tree.*;

/**
 * Property view for treebolic documents
 *
 * @author Bernard Bou
 */
public class PropertyView extends treebolic.propertyview.PropertyView implements SelectListener
{
	/**
	 * Id to node map
	 */
	private Map<String, MutableNode> idToNodeMap;

	/**
	 * The per-class handler map
	 */
	@NonNull
	private final Map<Class<?>, Handler> classHandler;

	/**
	 * Whether data have been modified
	 */
	public boolean dirty;

	// N O D E

	static private final String LABEL_NODE_ID = Messages.getString("PropertyView.id");

	static private final String LABEL_NODE_LABEL = Messages.getString("PropertyView.label");

	static private final String LABEL_NODE_CONTENT = Messages.getString("PropertyView.content");

	static private final String LABEL_NODE_IMAGE = Messages.getString("PropertyView.image");

	static private final String LABEL_NODE_LINK = Messages.getString("PropertyView.link");

	static private final String LABEL_NODE_WEIGHT = Messages.getString("PropertyView.weight");

	static private final String LABEL_NODE_BACKCOLOR = Messages.getString("PropertyView.backcolor");

	static private final String LABEL_NODE_FORECOLOR = Messages.getString("PropertyView.forecolor");

	static private final String LABEL_NODE_EDGE_LABEL = Messages.getString("PropertyView.elabel");

	static private final String LABEL_NODE_EDGE_IMAGE = Messages.getString("PropertyView.eimage");

	static private final String LABEL_NODE_EDGE_COLOR = Messages.getString("PropertyView.ecolor");

	static private final String LABEL_NODE_EDGE_STROKE = Messages.getString("PropertyView.estroke");

	static private final String LABEL_NODE_EDGE_STROKEWIDTH = Messages.getString("PropertyView.estrokewidth");

	static private final String LABEL_NODE_EDGE_FROMTERMINATOR = Messages.getString("PropertyView.efromterminator");

	static private final String LABEL_NODE_EDGE_TOTERMINATOR = Messages.getString("PropertyView.etoterminator");

	static private final String LABEL_NODE_EDGE_LINE = Messages.getString("PropertyView.eline");

	static private final String LABEL_NODE_EDGE_HIDDEN = Messages.getString("PropertyView.ehidden");

	static private final String LABEL_NODE_MOUNT_URL = Messages.getString("PropertyView.mounturl");

	static private final String LABEL_NODE_MOUNT_NOW = Messages.getString("PropertyView.mountnow");

	/**
	 * Node getter
	 */
	private static class NodeGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final MutableNode node = (MutableNode) object;
			if (propertyName.equals(PropertyView.LABEL_NODE_ID))
			{
				return node.getId();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_LABEL))
			{
				return node.getLabel();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_CONTENT))
			{
				return node.getContent();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_IMAGE))
			{
				return node.getImageFile();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_BACKCOLOR))
			{
				return node.getBackColor();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_FORECOLOR))
			{
				return node.getForeColor();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_COLOR))
			{
				return node.getEdgeColor();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_STROKE))
			{
				return Utils.toString(node.getEdgeStyle(), Utils.StyleComponent.STROKE);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_STROKEWIDTH))
			{
				return Utils.toInteger(node.getEdgeStyle(), Utils.StyleComponent.STROKEWIDTH);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_FROMTERMINATOR))
			{
				return Utils.toString(node.getEdgeStyle(), Utils.StyleComponent.FROMTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_TOTERMINATOR))
			{
				return Utils.toString(node.getEdgeStyle(), Utils.StyleComponent.TOTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_LINE))
			{
				return Utils.toTrueBoolean(node.getEdgeStyle(), Utils.StyleComponent.LINE);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_HIDDEN))
			{
				return Utils.toTrueBoolean(node.getEdgeStyle(), Utils.StyleComponent.HIDDEN);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_LABEL))
			{
				return node.getEdgeLabel();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_IMAGE))
			{
				return node.getEdgeImageFile();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_LINK))
			{
				return node.getLink();
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_MOUNT_URL))
			{
				@Nullable final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) node.getMountPoint();
				return mountingPoint == null ? null : mountingPoint.url;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_MOUNT_NOW))
			{
				@Nullable final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) node.getMountPoint();
				return mountingPoint == null ? null : mountingPoint.now;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_WEIGHT))
			{
				final double weight = node.getWeight();
				return weight < 0. ? Math.abs(weight) : null;
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}
	}

	/**
	 * Node setter
	 */
	private class NodeSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, @Nullable final Object propertyValue)
		{
			if (!(object instanceof TreeMutableNode))
			{
				System.err.println("Tried to set property '" + propertyName + "' on non-mutable object '" + object + "' instance of " + object.getClass());
				return;
			}

			PropertyView.this.dirty = true;
			@NonNull final TreeMutableNode node = (TreeMutableNode) object;
			if (propertyName.equals(PropertyView.LABEL_NODE_ID))
			{
				@Nullable final String id2 = (String) propertyValue;
				@Nullable final String id = node.getId();
				PropertyView.this.idToNodeMap.remove(id);
				PropertyView.this.idToNodeMap.put(id2, node);
				node.setId(id2);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_LABEL))
			{
				node.setLabel((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_CONTENT))
			{
				node.setContent((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_IMAGE))
			{
				node.setImageFile((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_BACKCOLOR))
			{
				node.setBackColor((Integer) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_FORECOLOR))
			{
				node.setForeColor((Integer) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_COLOR))
			{
				node.setEdgeColor((Integer) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_STROKE))
			{
				node.setEdgeStyle(Utils.modifyStyle(node.getEdgeStyle(), propertyValue, Utils.StyleComponent.STROKE));
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_STROKEWIDTH))
			{
				node.setEdgeStyle(Utils.modifyStyle(node.getEdgeStyle(), propertyValue, Utils.StyleComponent.STROKEWIDTH));
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_FROMTERMINATOR))
			{
				node.setEdgeStyle(Utils.modifyStyle(node.getEdgeStyle(), propertyValue, Utils.StyleComponent.FROMTERMINATOR));
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_TOTERMINATOR))
			{
				node.setEdgeStyle(Utils.modifyStyle(node.getEdgeStyle(), propertyValue, Utils.StyleComponent.TOTERMINATOR));
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_LINE))
			{
				node.setEdgeStyle(Utils.modifyStyle(node.getEdgeStyle(), propertyValue, Utils.StyleComponent.LINE));
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_HIDDEN))
			{
				node.setEdgeStyle(Utils.modifyStyle(node.getEdgeStyle(), propertyValue, Utils.StyleComponent.HIDDEN));
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_LABEL))
			{
				node.setEdgeLabel((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_EDGE_IMAGE))
			{
				node.setEdgeImageFile((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_LINK))
			{
				node.setLink((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_MOUNT_URL))
			{
				if (propertyValue != null)
				{
					@Nullable MountPoint.Mounting mountingPoint = (MountPoint.Mounting) node.getMountPoint();
					if (mountingPoint == null)
					{
						mountingPoint = new MountPoint.Mounting();
						node.setMountPoint(mountingPoint);
					}
					mountingPoint.url = (String) propertyValue;
				}
				else
				{
					node.setMountPoint(null);
				}
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_MOUNT_NOW))
			{
				if (propertyValue != null)
				{
					@Nullable final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) node.getMountPoint();
					if (mountingPoint != null)
					{
						mountingPoint.now = (Boolean) propertyValue;
					}
				}
			}
			else if (propertyName.equals(PropertyView.LABEL_NODE_WEIGHT))
			{
				@Nullable final Double weight = (Double) propertyValue;
				if (weight != null)
				{
					node.setWeight(-weight);
				}
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// E D G E

	static private final String LABEL_EDGE_LABEL = Messages.getString("PropertyView.label");

	static private final String LABEL_EDGE_IMAGE = Messages.getString("PropertyView.image");

	static private final String LABEL_EDGE_FROM = Messages.getString("PropertyView.from");

	static private final String LABEL_EDGE_TO = Messages.getString("PropertyView.to");

	static private final String LABEL_EDGE_COLOR = Messages.getString("PropertyView.color");

	static private final String LABEL_EDGE_STROKE = Messages.getString("PropertyView.stroke");

	static private final String LABEL_EDGE_STROKEWIDTH = Messages.getString("PropertyView.strokewidth");

	static private final String LABEL_EDGE_FROMTERMINATOR = Messages.getString("PropertyView.fromterminator");

	static private final String LABEL_EDGE_TOTERMINATOR = Messages.getString("PropertyView.toterminator");

	static private final String LABEL_EDGE_LINE = Messages.getString("PropertyView.line");

	static private final String LABEL_EDGE_HIDDEN = Messages.getString("PropertyView.hidden");

	/**
	 * Edge getter
	 */
	private static class EdgeGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final Edge edge = (Edge) object;
			if (propertyName.equals(PropertyView.LABEL_EDGE_FROM))
			{
				final INode node = edge.getFrom();
				return node == null ? null : node.getId();
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_TO))
			{
				final INode node = edge.getTo();
				return node == null ? null : node.getId();
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_LABEL))
			{
				return edge.getLabel();
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_COLOR))
			{
				return edge.getColor();
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_IMAGE))
			{
				return edge.getImageFile();
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_STROKE))
			{
				return Utils.toString(edge.getStyle(), Utils.StyleComponent.STROKE);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_STROKEWIDTH))
			{
				return Utils.toInteger(edge.getStyle(), Utils.StyleComponent.STROKEWIDTH);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_FROMTERMINATOR))
			{
				return Utils.toString(edge.getStyle(), Utils.StyleComponent.FROMTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_TOTERMINATOR))
			{
				return Utils.toString(edge.getStyle(), Utils.StyleComponent.TOTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_LINE))
			{
				return Utils.toTrueBoolean(edge.getStyle(), Utils.StyleComponent.LINE);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_HIDDEN))
			{
				return Utils.toTrueBoolean(edge.getStyle(), Utils.StyleComponent.HIDDEN);
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}
	}

	/**
	 * Edge setter
	 */
	private class EdgeSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, final Object propertyValue)
		{
			if (!(object instanceof TreeMutableEdge))
			{
				System.err.println("Tried to set property '" + propertyName + "' on non-mutable object '" + object + "' instance of " + object.getClass());
				return;
			}

			PropertyView.this.dirty = true;
			@NonNull final TreeMutableEdge edge = (TreeMutableEdge) object;
			if (propertyName.equals(PropertyView.LABEL_EDGE_FROM))
			{
				final String id = (String) propertyValue;
				final MutableNode node = id == null ? null : PropertyView.this.idToNodeMap.get(id);
				edge.setFrom(node);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_TO))
			{
				final String id = (String) propertyValue;
				final MutableNode node = id == null ? null : PropertyView.this.idToNodeMap.get(id);
				edge.setTo(node);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_LABEL))
			{
				edge.setLabel((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_COLOR))
			{
				edge.setColor((Integer) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_IMAGE))
			{
				edge.setImageFile((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_STROKE))
			{
				edge.setStyle(Utils.modifyStyle(edge.getStyle(), propertyValue, Utils.StyleComponent.STROKE));
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_STROKEWIDTH))
			{
				edge.setStyle(Utils.modifyStyle(edge.getStyle(), propertyValue, Utils.StyleComponent.STROKEWIDTH));
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_FROMTERMINATOR))
			{
				edge.setStyle(Utils.modifyStyle(edge.getStyle(), propertyValue, Utils.StyleComponent.FROMTERMINATOR));
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_TOTERMINATOR))
			{
				edge.setStyle(Utils.modifyStyle(edge.getStyle(), propertyValue, Utils.StyleComponent.TOTERMINATOR));
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_LINE))
			{
				edge.setStyle(Utils.modifyStyle(edge.getStyle(), propertyValue, Utils.StyleComponent.LINE));
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGE_HIDDEN))
			{
				edge.setStyle(Utils.modifyStyle(edge.getStyle(), propertyValue, Utils.StyleComponent.HIDDEN));
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// T O P
	/**
	 * Top element setter
	 */

	static private final String LABEL_TOP_TOOLBAR = Messages.getString("PropertyView.toolbar");

	static private final String LABEL_TOP_STATUSBAR = Messages.getString("PropertyView.statusbar");

	static private final String LABEL_TOP_POPUP = Messages.getString("PropertyView.popup");

	static private final String LABEL_TOP_CONTENTFORMAT = Messages.getString("PropertyView.contentformat");

	static private final String LABEL_TOP_TOOLTIP = Messages.getString("PropertyView.tooltip");

	static private final String LABEL_TOP_TOOLTIP_DISPLAYS_CONTENT = Messages.getString("PropertyView.tooltipcontent");

	static private final String LABEL_TOP_FOCUS_ON_HOVER = Messages.getString("PropertyView.focushover");

	static private final String LABEL_TOP_FOCUS = Messages.getString("PropertyView.focus");

	static private final String LABEL_TOP_X_MOVETO = Messages.getString("PropertyView.xmoveto");

	static private final String LABEL_TOP_Y_MOVETO = Messages.getString("PropertyView.ymoveto");

	static private final String LABEL_TOP_X_SHIFT = Messages.getString("PropertyView.xshift");

	static private final String LABEL_TOP_Y_SHIFT = Messages.getString("PropertyView.yshift");

	private static class TopGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final TopWrapper topSettings = (TopWrapper) object;
			final Settings settings = topSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_TOP_TOOLBAR))
			{
				return settings.hasToolbarFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_STATUSBAR))
			{
				return settings.hasStatusbarFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_POPUP))
			{
				return settings.hasPopUpMenuFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_CONTENTFORMAT))
			{
				return settings.contentFormat;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_TOOLTIP))
			{
				return settings.hasToolTipFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_TOOLTIP_DISPLAYS_CONTENT))
			{
				return settings.toolTipDisplaysContentFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_FOCUS_ON_HOVER))
			{
				return settings.focusOnHoverFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_FOCUS))
			{
				return settings.focus;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_X_MOVETO))
			{
				return settings.xMoveTo;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_Y_MOVETO))
			{
				return settings.yMoveTo;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_X_SHIFT))
			{
				return settings.xShift;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_Y_SHIFT))
			{
				return settings.yShift;
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}

	}

	/**
	 * Top element setter
	 */
	private class TopSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, final Object propertyValue)
		{
			PropertyView.this.dirty = true;
			final TopWrapper topSettings = (TopWrapper) object;
			final Settings settings = topSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_TOP_TOOLBAR))
			{
				settings.hasToolbarFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_STATUSBAR))
			{
				settings.hasStatusbarFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_POPUP))
			{
				settings.hasPopUpMenuFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_CONTENTFORMAT))
			{
				settings.contentFormat = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_TOOLTIP))
			{
				settings.hasToolTipFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_TOOLTIP_DISPLAYS_CONTENT))
			{
				settings.toolTipDisplaysContentFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_FOCUS_ON_HOVER))
			{
				settings.focusOnHoverFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_FOCUS))
			{
				settings.focus = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_X_MOVETO))
			{
				settings.xMoveTo = (Float) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_Y_MOVETO))
			{
				settings.yMoveTo = (Float) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_X_SHIFT))
			{
				settings.xShift = (Float) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TOP_Y_SHIFT))
			{
				settings.yShift = (Float) propertyValue;
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// T R E E

	static private final String LABEL_TREE_BACKGROUND_IMAGE = Messages.getString("PropertyView.backimage");

	static private final String LABEL_TREE_ORIENTATION = Messages.getString("PropertyView.orientation");

	static private final String LABEL_TREE_EXPANSION = Messages.getString("PropertyView.expansion");

	static private final String LABEL_TREE_SWEEP = Messages.getString("PropertyView.sweep");

	static private final String LABEL_TREE_FORECOLOR = Messages.getString("PropertyView.forecolor");

	static private final String LABEL_TREE_BACKCOLOR = Messages.getString("PropertyView.backcolor");

	static private final String LABEL_TREE_FONT_FACE = Messages.getString("PropertyView.fontface");

	static private final String LABEL_TREE_FONT_SIZE = Messages.getString("PropertyView.fontsize");

	static private final String LABEL_TREE_SCALE_FONTS = Messages.getString("PropertyView.scalefonts");

	static private final String LABEL_TREE_FONT_SCALER = Messages.getString("PropertyView.fontscaler");

	static private final String LABEL_TREE_SCALE_IMAGES = Messages.getString("PropertyView.scaleimages");

	static private final String LABEL_TREE_IMAGE_SCALER = Messages.getString("PropertyView.imagescaler");

	static private final String LABEL_TREE_PRESERVE_ORIENTATION = Messages.getString("PropertyView.preserveorientation");

	/**
	 * Tree element setter
	 */
	private static class TreeGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final TreeWrapper treeSettings = (TreeWrapper) object;
			final Settings settings = treeSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_TREE_BACKGROUND_IMAGE))
			{
				return settings.backgroundImageFile;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_ORIENTATION))
			{
				return settings.orientation;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_EXPANSION))
			{
				return settings.expansion;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_SWEEP))
			{
				return settings.sweep;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FORECOLOR))
			{
				return settings.foreColor;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_BACKCOLOR))
			{
				return settings.backColor;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FONT_FACE))
			{
				return settings.fontFace;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FONT_SIZE))
			{
				return settings.fontSize;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_SCALE_FONTS))
			{
				return settings.downscaleFontsFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FONT_SCALER))
			{
				return settings.fontDownscaler == null ? null : new Floats(settings.fontDownscaler);
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_SCALE_IMAGES))
			{
				return settings.downscaleImagesFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_IMAGE_SCALER))
			{
				return settings.imageDownscaler == null ? null : new Floats(settings.imageDownscaler);
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_PRESERVE_ORIENTATION))
			{
				return settings.preserveOrientationFlag;
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}
	}

	/**
	 * Tree element setter
	 */
	private class TreeSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, final Object propertyValue)
		{
			PropertyView.this.dirty = true;
			final TreeWrapper treeSettings = (TreeWrapper) object;
			final Settings settings = treeSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_TREE_BACKGROUND_IMAGE))
			{
				settings.backgroundImageFile = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_ORIENTATION))
			{
				settings.orientation = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_EXPANSION))
			{
				settings.expansion = (Float) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_SWEEP))
			{
				settings.sweep = (Float) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FORECOLOR))
			{
				settings.foreColor = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_BACKCOLOR))
			{
				settings.backColor = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FONT_FACE))
			{
				settings.fontFace = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FONT_SIZE))
			{
				settings.fontSize = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_SCALE_FONTS))
			{
				settings.downscaleFontsFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_FONT_SCALER))
			{
				final Floats value = (Floats) propertyValue;
				settings.fontDownscaler = value == null ? null : value.floats;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_SCALE_IMAGES))
			{
				settings.downscaleImagesFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_IMAGE_SCALER))
			{
				final Floats value = (Floats) propertyValue;
				settings.imageDownscaler = value == null ? null : value.floats;
			}
			else if (propertyName.equals(PropertyView.LABEL_TREE_PRESERVE_ORIENTATION))
			{
				settings.preserveOrientationFlag = (Boolean) propertyValue;
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// N O D E S

	static private final String LABEL_NODES_BACKCOLOR = Messages.getString("PropertyView.backcolor");

	static private final String LABEL_NODES_FORECOLOR = Messages.getString("PropertyView.forecolor");

	static private final String LABEL_NODES_BORDER = Messages.getString("PropertyView.border");

	static private final String LABEL_NODES_ELLIPSIZE = Messages.getString("PropertyView.ellipsize");

	static private final String LABEL_NODES_MAX_LINES = Messages.getString("PropertyView.labelmaxlines");

	static private final String LABEL_NODES_EXTRA_LINE_FACTOR = Messages.getString("PropertyView.labelextralinefactor");

	static private final String LABEL_NODES_IMAGE = Messages.getString("PropertyView.image");

	static private final String LABEL_NODES_EDGE_IMAGE = Messages.getString("PropertyView.eimage");

	static private final String LABEL_NODES_EDGE_COLOR = Messages.getString("PropertyView.ecolor");

	static private final String LABEL_NODES_EDGE_STROKE = Messages.getString("PropertyView.estroke");

	static private final String LABEL_NODES_EDGE_STROKEWIDTH = Messages.getString("PropertyView.estrokewidth");

	static private final String LABEL_NODES_EDGE_FROMTERMINATOR = Messages.getString("PropertyView.efromterminator");

	static private final String LABEL_NODES_EDGE_TOTERMINATOR = Messages.getString("PropertyView.etoterminator");

	static private final String LABEL_NODES_EDGE_LINE = Messages.getString("PropertyView.eline");

	static private final String LABEL_NODES_EDGE_HIDDEN = Messages.getString("PropertyView.ehidden");

	/**
	 * Nodes getter
	 */
	private static class NodesGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final NodesWrapper nodesSettings = (NodesWrapper) object;
			final Settings settings = nodesSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_NODES_BACKCOLOR))
			{
				return settings.nodeBackColor;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_FORECOLOR))
			{
				return settings.nodeForeColor;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_IMAGE))
			{
				return settings.defaultNodeImage;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_COLOR))
			{
				return settings.treeEdgeColor;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_FROMTERMINATOR))
			{
				return Utils.toString(settings.treeEdgeStyle, Utils.StyleComponent.FROMTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_TOTERMINATOR))
			{
				return Utils.toString(settings.treeEdgeStyle, Utils.StyleComponent.TOTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_STROKE))
			{
				return Utils.toString(settings.treeEdgeStyle, Utils.StyleComponent.STROKE);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_STROKEWIDTH))
			{
				return Utils.toInteger(settings.treeEdgeStyle, Utils.StyleComponent.STROKEWIDTH);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_LINE))
			{
				return Utils.toTrueBoolean(settings.treeEdgeStyle, Utils.StyleComponent.LINE);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_HIDDEN))
			{
				return Utils.toTrueBoolean(settings.treeEdgeStyle, Utils.StyleComponent.HIDDEN);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_IMAGE))
			{
				return settings.defaultTreeEdgeImage;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_BORDER))
			{
				return settings.borderFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_ELLIPSIZE))
			{
				return settings.ellipsizeFlag;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_MAX_LINES))
			{
				return settings.labelMaxLines;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EXTRA_LINE_FACTOR))
			{
				return settings.labelExtraLineFactor;
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}
	}

	/**
	 * Nodes setter
	 */
	private class NodesSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, final Object propertyValue)
		{
			PropertyView.this.dirty = true;
			final NodesWrapper nodesSettings = (NodesWrapper) object;
			final Settings settings = nodesSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_NODES_BACKCOLOR))
			{
				settings.nodeBackColor = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_FORECOLOR))
			{
				settings.nodeForeColor = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_IMAGE))
			{
				settings.defaultNodeImage = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_COLOR))
			{
				settings.treeEdgeColor = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_FROMTERMINATOR))
			{
				settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, propertyValue, Utils.StyleComponent.FROMTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_TOTERMINATOR))
			{
				settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, propertyValue, Utils.StyleComponent.TOTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_STROKE))
			{
				settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, propertyValue, Utils.StyleComponent.STROKE);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_STROKEWIDTH))
			{
				settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, propertyValue, Utils.StyleComponent.STROKEWIDTH);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_LINE))
			{
				settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, propertyValue, Utils.StyleComponent.LINE);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_HIDDEN))
			{
				settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, propertyValue, Utils.StyleComponent.HIDDEN);
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EDGE_IMAGE))
			{
				settings.defaultTreeEdgeImage = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_BORDER))
			{
				settings.borderFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_ELLIPSIZE))
			{
				settings.ellipsizeFlag = (Boolean) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_MAX_LINES))
			{
				settings.labelMaxLines = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_NODES_EXTRA_LINE_FACTOR))
			{
				settings.labelExtraLineFactor = (Float) propertyValue;
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// E D G E S

	static private final String LABEL_EDGES_IMAGE = Messages.getString("PropertyView.image");

	static private final String LABEL_EDGES_COLOR = Messages.getString("PropertyView.color");

	static private final String LABEL_EDGES_STROKE = Messages.getString("PropertyView.stroke");

	static private final String LABEL_EDGES_STROKEWIDTH = Messages.getString("PropertyView.strokewidth");

	static private final String LABEL_EDGES_FROMTERMINATOR = Messages.getString("PropertyView.fromterminator");

	static private final String LABEL_EDGES_TOTERMINATOR = Messages.getString("PropertyView.toterminator");

	static private final String LABEL_EDGES_LINE = Messages.getString("PropertyView.line");

	static private final String LABEL_EDGES_HIDDEN = Messages.getString("PropertyView.hidden");

	static private final String LABEL_EDGES_AS_ARCS = Messages.getString("PropertyView.asarcs");

	/**
	 * Edges getter
	 */
	private static class EdgesGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final EdgesWrapper edgesSettings = (EdgesWrapper) object;
			final Settings settings = edgesSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_EDGES_IMAGE))
			{
				return settings.defaultEdgeImage;
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_COLOR))
			{
				return settings.edgeColor;
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_STROKE))
			{
				return Utils.toString(settings.edgeStyle, Utils.StyleComponent.STROKE);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_STROKEWIDTH))
			{
				return Utils.toInteger(settings.edgeStyle, Utils.StyleComponent.STROKEWIDTH);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_FROMTERMINATOR))
			{
				return Utils.toString(settings.edgeStyle, Utils.StyleComponent.FROMTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_TOTERMINATOR))
			{
				return Utils.toString(settings.edgeStyle, Utils.StyleComponent.TOTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_LINE))
			{
				return Utils.toTrueBoolean(settings.edgeStyle, Utils.StyleComponent.LINE);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_HIDDEN))
			{
				return Utils.toTrueBoolean(settings.edgeStyle, Utils.StyleComponent.HIDDEN);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_AS_ARCS))
			{
				return settings.edgesAsArcsFlag;
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}
	}

	/**
	 * Edges setter
	 */

	private class EdgesSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, final Object propertyValue)
		{
			PropertyView.this.dirty = true;
			final EdgesWrapper edgesSettings = (EdgesWrapper) object;
			final Settings settings = edgesSettings.settings;
			if (propertyName.equals(PropertyView.LABEL_EDGES_IMAGE))
			{
				settings.defaultEdgeImage = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_COLOR))
			{
				settings.edgeColor = (Integer) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_STROKE))
			{
				settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, propertyValue, Utils.StyleComponent.STROKE);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_STROKEWIDTH))
			{
				settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, propertyValue, Utils.StyleComponent.STROKEWIDTH);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_FROMTERMINATOR))
			{
				settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, propertyValue, Utils.StyleComponent.FROMTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_TOTERMINATOR))
			{
				settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, propertyValue, Utils.StyleComponent.TOTERMINATOR);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_LINE))
			{
				settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, propertyValue, Utils.StyleComponent.LINE);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_HIDDEN))
			{
				settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, propertyValue, Utils.StyleComponent.HIDDEN);
			}
			else if (propertyName.equals(PropertyView.LABEL_EDGES_AS_ARCS))
			{
				settings.edgesAsArcsFlag = (Boolean) propertyValue;
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// M E N U I T E M

	static private final String LABEL_MENUITEM_LABEL = Messages.getString("PropertyView.label");

	static private final String LABEL_MENUITEM_LINK = Messages.getString("PropertyView.link");

	static private final String LABEL_MENUITEM_ACTION = Messages.getString("PropertyView.action");

	static private final String LABEL_MENUITEM_MATCH_SCOPE = Messages.getString("PropertyView.scope");

	static private final String LABEL_MENUITEM_MATCH_MODE = Messages.getString("PropertyView.mode");

	static private final String LABEL_MENUITEM_MATCH_TARGET = Messages.getString("PropertyView.target");

	/**
	 * Menu element getter
	 */
	private static class MenuItemGetter implements Getter
	{
		@Nullable
		@SuppressWarnings("synthetic-access")
		@Override
		public Object get(final Object object, @NonNull final String propertyName)
		{
			final MenuItemWrapper menuItemWrapper = (MenuItemWrapper) object;
			final MenuItem menuItem = menuItemWrapper.menuItem;
			if (propertyName.equals(PropertyView.LABEL_MENUITEM_LABEL))
			{
				return menuItem.label;
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_LINK))
			{
				return menuItem.link;
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_ACTION))
			{
				return Utils.toString(menuItem.action);
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_MATCH_SCOPE))
			{
				return Utils.toString(menuItem.matchScope);
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_MATCH_MODE))
			{
				return Utils.toString(menuItem.matchMode);
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_MATCH_TARGET))
			{
				return menuItem.matchTarget;
			}
			System.err.println("Unhandled property: " + propertyName);
			return null;
		}
	}

	/**
	 * Menu element setter
	 */
	private class MenuItemSetter implements Setter
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void set(final Object object, @NonNull final String propertyName, final Object propertyValue)
		{
			PropertyView.this.dirty = true;
			final MenuItemWrapper menuItemWrapper = (MenuItemWrapper) object;
			final MenuItem menuItem = menuItemWrapper.menuItem;
			if (propertyName.equals(PropertyView.LABEL_MENUITEM_LABEL))
			{
				menuItem.label = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_LINK))
			{
				menuItem.link = (String) propertyValue;
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_ACTION))
			{
				menuItem.action = Utils.stringToAction((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_MATCH_SCOPE))
			{
				menuItem.matchScope = Utils.stringToScope((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_MATCH_MODE))
			{
				menuItem.matchMode = Utils.stringToMode((String) propertyValue);
			}
			else if (propertyName.equals(PropertyView.LABEL_MENUITEM_MATCH_TARGET))
			{
				menuItem.matchTarget = (String) propertyValue;
			}
			else
			{
				System.err.println("Unhandled property: " + propertyName);
			}
		}
	}

	// I D

	/**
	 * Node id getter
	 */
	private static class NodeIdGetter implements IdGetter
	{
		/**
		 * Id to node map
		 */
		private final Map<String, MutableNode> idToNodeMap;

		/**
		 * Constructor
		 *
		 * @param idToNodeMap id to node map
		 */
		public NodeIdGetter(final Map<String, MutableNode> idToNodeMap)
		{
			this.idToNodeMap = idToNodeMap;
		}

		@Override
		public Object get(final String id)
		{
			return this.idToNodeMap.get(id);
		}

		@NonNull
		@Override
		public Set<String> ids()
		{
			return this.idToNodeMap.keySet();
		}
	}

	/**
	 * Node features.
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] nodeFeatures = { //
			{PropertyView.LABEL_NODE_ID, PropertyView.AttributeType.ID, true, null}, //
			{PropertyView.LABEL_NODE_LABEL, PropertyView.AttributeType.LABEL, false, null}, //
			{PropertyView.LABEL_NODE_CONTENT, PropertyView.AttributeType.LONGTEXT, false, null}, //
			{PropertyView.LABEL_NODE_BACKCOLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_NODE_FORECOLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_NODE_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_COLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_STROKE, PropertyView.AttributeType.STROKE, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_STROKEWIDTH, PropertyView.AttributeType.INTEGER, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_FROMTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_TOTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_LINE, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_HIDDEN, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_LABEL, PropertyView.AttributeType.TEXT, false, null}, //
			{PropertyView.LABEL_NODE_EDGE_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
			{PropertyView.LABEL_NODE_LINK, PropertyView.AttributeType.LINK, false, null}, //
			{PropertyView.LABEL_NODE_MOUNT_URL, PropertyView.AttributeType.LINK, false, null}, //
			{PropertyView.LABEL_NODE_MOUNT_NOW, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODE_WEIGHT, PropertyView.AttributeType.FLOAT, false, null} //
	};

	/**
	 * Edge features
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] edgeFeatures = { //
			{PropertyView.LABEL_EDGE_FROM, PropertyView.AttributeType.REFID, true, null}, //
			{PropertyView.LABEL_EDGE_TO, PropertyView.AttributeType.REFID, true, null}, //
			{PropertyView.LABEL_EDGE_COLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_EDGE_STROKE, PropertyView.AttributeType.STROKE, false, null}, //
			{PropertyView.LABEL_EDGE_STROKEWIDTH, PropertyView.AttributeType.INTEGER, false, null}, //
			{PropertyView.LABEL_EDGE_FROMTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_EDGE_TOTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_EDGE_LINE, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_EDGE_HIDDEN, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_EDGE_LABEL, PropertyView.AttributeType.TEXT, false, null}, //
			{PropertyView.LABEL_EDGE_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
	};

	/**
	 * Top element features.
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] topFeatures = { //
			{PropertyView.LABEL_TOP_TOOLBAR, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TOP_STATUSBAR, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TOP_POPUP, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TOP_CONTENTFORMAT, PropertyView.AttributeType.TEXT, false, new String[]{ //
					"<table><tr><td valign='top'><img src='%s' style='width:32px;height:32px;'/></td><td>%s</td></tr></table>", //
					"<p><img src='%s' style='float:left;margin-right:10px;width:32px;height:32px;'/></p><p>%s</p>"}}, //
			{PropertyView.LABEL_TOP_TOOLTIP, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TOP_TOOLTIP_DISPLAYS_CONTENT, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TOP_FOCUS_ON_HOVER, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TOP_FOCUS, PropertyView.AttributeType.REFID, false, null}, //
			{PropertyView.LABEL_TOP_X_MOVETO, PropertyView.AttributeType.FLOAT, false, null}, //
			{PropertyView.LABEL_TOP_Y_MOVETO, PropertyView.AttributeType.FLOAT, false, null}, //
			{PropertyView.LABEL_TOP_X_SHIFT, PropertyView.AttributeType.FLOAT, false, null}, //
			{PropertyView.LABEL_TOP_Y_SHIFT, PropertyView.AttributeType.FLOAT, false, null}, //
	};

	/**
	 * Tree features
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] treeFeatures = { //
			{PropertyView.LABEL_TREE_BACKGROUND_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
			{PropertyView.LABEL_TREE_ORIENTATION, PropertyView.AttributeType.TEXT, false, new String[]{"radial", "north", "south", "east", "west"}}, //     
			{PropertyView.LABEL_TREE_EXPANSION, PropertyView.AttributeType.FLOAT, false, null}, //
			{PropertyView.LABEL_TREE_SWEEP, PropertyView.AttributeType.FLOAT, false, null}, //
			{PropertyView.LABEL_TREE_BACKCOLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_TREE_FORECOLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_TREE_FONT_FACE, PropertyView.AttributeType.FONTFACE, false, null}, //
			{PropertyView.LABEL_TREE_FONT_SIZE, PropertyView.AttributeType.FONTSIZE, false, null}, //
			{PropertyView.LABEL_TREE_SCALE_FONTS, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TREE_FONT_SCALER, PropertyView.AttributeType.FLOATS, false, null}, //
			{PropertyView.LABEL_TREE_SCALE_IMAGES, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_TREE_IMAGE_SCALER, PropertyView.AttributeType.FLOATS, false, null}, //
			{PropertyView.LABEL_TREE_PRESERVE_ORIENTATION, PropertyView.AttributeType.BOOLEAN, false, null}, //
	};

	/**
	 * Nodes features.
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] nodesFeatures = { //
			{PropertyView.LABEL_NODES_BACKCOLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_NODES_FORECOLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_NODES_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
			{PropertyView.LABEL_NODES_BORDER, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODES_ELLIPSIZE, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODES_MAX_LINES, PropertyView.AttributeType.INTEGER, false, null}, //
			{PropertyView.LABEL_NODES_EXTRA_LINE_FACTOR, PropertyView.AttributeType.FLOAT, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_COLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_FROMTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_TOTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_LINE, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_HIDDEN, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_STROKE, PropertyView.AttributeType.STROKE, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_STROKEWIDTH, PropertyView.AttributeType.INTEGER, false, null}, //
			{PropertyView.LABEL_NODES_EDGE_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
	};

	/**
	 * Edges features
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] edgesFeatures = { //
			{PropertyView.LABEL_EDGES_AS_ARCS, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_EDGES_COLOR, PropertyView.AttributeType.COLOR, false, null}, //
			{PropertyView.LABEL_EDGES_FROMTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_EDGES_TOTERMINATOR, PropertyView.AttributeType.TERMINATOR, false, null}, //
			{PropertyView.LABEL_EDGES_STROKE, PropertyView.AttributeType.STROKE, false, null}, //
			{PropertyView.LABEL_EDGES_STROKEWIDTH, PropertyView.AttributeType.INTEGER, false, null}, //
			{PropertyView.LABEL_EDGES_LINE, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_EDGES_HIDDEN, PropertyView.AttributeType.BOOLEAN, false, null}, //
			{PropertyView.LABEL_EDGES_IMAGE, PropertyView.AttributeType.IMAGE, false, null}, //
	};

	/**
	 * Menu item features
	 * feature[0] id:String
	 * feature[1] type:AttributeType
	 * feature[2] isMandatory:Boolean
	 * feature[3] possibleValues:String[]
	 */
	static final Object[][] menuItemFeatures = { //
			{PropertyView.LABEL_MENUITEM_LABEL, PropertyView.AttributeType.TEXT, true, null}, //
			{PropertyView.LABEL_MENUITEM_ACTION, PropertyView.AttributeType.TEXT, true, new String[]{MenuItem.Action.GOTO.toString(), MenuItem.Action.SEARCH.toString(), MenuItem.Action.LINK.toString(), MenuItem.Action.FOCUS.toString(), MenuItem.Action.MOUNT.toString()}}, //
			{PropertyView.LABEL_MENUITEM_MATCH_TARGET, PropertyView.AttributeType.TEXT, false, null}, //
			{PropertyView.LABEL_MENUITEM_MATCH_SCOPE, PropertyView.AttributeType.TEXT, false, new String[]{MatchScope.LABEL.toString(), MatchScope.CONTENT.toString(), MatchScope.ID.toString(), MatchScope.LINK.toString()}}, //
			{PropertyView.LABEL_MENUITEM_MATCH_MODE, PropertyView.AttributeType.TEXT, false, new String[]{MatchMode.STARTSWITH.toString(), MatchMode.INCLUDES.toString(), MatchMode.EQUALS.toString()}}, //
			{PropertyView.LABEL_MENUITEM_LINK, PropertyView.AttributeType.LINK, false, null}, //
	};

	/**
	 * Attribute comparator (rank)
	 */
	private final Comparator<AttributeDescriptor> comparator = Comparator.comparing(d -> d.rank);

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public PropertyView()
	{
		super();

		this.classHandler = makeHandlers();
		super.setHandlerFactory(makeHandlerFactory(this.classHandler));
	}

	// id map

	/**
	 * Set id to node map
	 *
	 * @param idToNodeMap id to node map
	 */
	public void setIdToNodeMap(final Map<String, MutableNode> idToNodeMap)
	{
		this.idToNodeMap = idToNodeMap;
		@NonNull final IdGetter idGetter = new NodeIdGetter(this.idToNodeMap);
		this.classHandler.get(TreeMutableNode.class).idGetter = idGetter;
		this.classHandler.get(TreeMutableEdge.class).idGetter = idGetter;
		this.classHandler.get(TopWrapper.class).idGetter = idGetter;
		this.classHandler.get(MenuItemWrapper.class).idGetter = idGetter;
	}

	// select

	/**
	 * Make handler
	 *
	 * @param features feature data
	 * @param getter   getter
	 * @param setter   setter
	 * @param idGetter id getter
	 * @return handler
	 */
	@NonNull
	private Handler makeHandler(@NonNull final Object[][] features, final Getter getter, final Setter setter, @SuppressWarnings("SameParameterValue") final IdGetter idGetter)
	{
		Handler handler;
		handler = new PropertyView.Handler();
		handler.attributeDescriptors = new TreeSet<>(this.comparator);
		int rank = 0;
		for (final Object[] feature : features)
		{
			final String id = (String) feature[0];
			@NonNull final AttributeDescriptor descriptor = new AttributeDescriptor();
			descriptor.name = id;
			descriptor.type = (AttributeType) feature[1];
			descriptor.isMandatory = (Boolean) feature[2];
			descriptor.possibleValues = (String[]) feature[3];
			descriptor.isReadOnly = false;
			descriptor.rank = rank++;

			handler.attributeDescriptors.add(descriptor);
		}
		handler.getter = getter;
		handler.setter = setter;
		handler.idGetter = idGetter;
		return handler;
	}

	/**
	 * Make handlers from feature tables
	 *
	 * @return class to handler map
	 */
	@NonNull
	@SuppressWarnings("synthetic-access")
	private Map<Class<?>, Handler> makeHandlers()
	{
		@NonNull final Map<Class<?>, Handler> handlers = new Hashtable<>();
		handlers.put(MutableNode.class, makeHandler(PropertyView.nodeFeatures, new NodeGetter(), new NodeSetter(), null));
		handlers.put(MutableEdge.class, makeHandler(PropertyView.edgeFeatures, new EdgeGetter(), new EdgeSetter(), null));

		handlers.put(TreeMutableNode.class, makeHandler(PropertyView.nodeFeatures, new NodeGetter(), new NodeSetter(), null));
		handlers.put(TreeMutableEdge.class, makeHandler(PropertyView.edgeFeatures, new EdgeGetter(), new EdgeSetter(), null));

		handlers.put(TopWrapper.class, makeHandler(PropertyView.topFeatures, new TopGetter(), new TopSetter(), null));
		handlers.put(TreeWrapper.class, makeHandler(PropertyView.treeFeatures, new TreeGetter(), new TreeSetter(), null));
		handlers.put(NodesWrapper.class, makeHandler(PropertyView.nodesFeatures, new NodesGetter(), new NodesSetter(), null));
		handlers.put(EdgesWrapper.class, makeHandler(PropertyView.edgesFeatures, new EdgesGetter(), new EdgesSetter(), null));
		handlers.put(MenuItemWrapper.class, makeHandler(PropertyView.menuItemFeatures, new MenuItemGetter(), new MenuItemSetter(), null));
		return handlers;
	}

	/**
	 * Make handler factory
	 *
	 * @param handlerMap handler map
	 * @return handler factory
	 */
	@NonNull
	public HandlerFactory makeHandlerFactory(@NonNull final Map<Class<?>, Handler> handlerMap)
	{
		return object -> {
			if (object == null)
			{
				return null;
			}
			return handlerMap.get(object.getClass());
		};
	}
}
