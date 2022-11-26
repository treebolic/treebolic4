/*
 * Copyright (c) 2019-2022. Bernard Bou
 */

package treebolic.model;

import java.util.List;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.Image;

/**
 * Model dump
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class ModelDump
{
	/**
	 * Stringify model
	 *
	 * @param model model
	 * @return string for model
	 */
	@NonNull
	static public String toString(@Nullable final Model model)
	{
		if (model == null)
		{
			return "null";
		}
		return ModelDump.toString(model.tree) + ModelDump.toString(model.settings) + ModelDump.toString(model.images);
	}

	/**
	 * Stringify tree
	 *
	 * @param tree tree
	 * @return string for tree
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public String toString(@Nullable final Tree tree)
	{
		if (tree == null)
		{
			return "null";
		}
		return "NODES\n" + ModelDump.toString(tree.getRoot(), 0) + "EDGES\n" + ModelDump.toString(tree.getEdges());
	}

	/**
	 * Stringify node and children
	 *
	 * @param node  node
	 * @param level level
	 * @return string for node
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public String toString(@Nullable final INode node, final int level)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++)
		{
			sb.append('\t');
		}

		if (node == null)
		{
			sb.append("[null node]");
		}
		else
		{
			// id
			sb.append('#');
			sb.append(node.getId());

			// label
			sb.append(" '");
			sb.append(node);

			// parent
			sb.append("' ^");
			@Nullable final INode parent = node.getParent();
			if (parent != null)
			{
				sb.append('#');
				sb.append(parent.getId());
			}

			// image
			@Nullable final String imageFile = node.getImageFile();
			final int imageIndex = node.getImageIndex();
			if (imageFile != null)
			{
				sb.append(" !");
				sb.append(imageFile);
			}
			if (imageIndex != -1)
			{
				sb.append(" !");
				sb.append(imageIndex);
			}

			// children
			@Nullable final List<INode> childNodes = node.getChildren();
			sb.append(" ~");
			sb.append(childNodes == null ? -1 : childNodes.size());
			sb.append('\n');
			if (childNodes != null)
			{
				for (final INode childNode : childNodes)
				{
					sb.append(ModelDump.toString(childNode, level + 1));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Stringify edge list
	 *
	 * @param edgeList edge list
	 * @return string for edge list
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public String toString(@Nullable final Iterable<IEdge> edgeList)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		if (edgeList != null)
		{
			for (@NonNull final IEdge edge : edgeList)
			{
				sb.append(edge);
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	/**
	 * Stringify image list
	 *
	 * @param images images
	 * @return string for images
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public String toString(@Nullable final Image[] images)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		sb.append("IMAGES\n");
		int i = 0;
		if (images != null)
		{
			for (@NonNull Image image : images)
			{
				sb.append(i);
				sb.append('-');
				sb.append(image.getHeight() + 'x' + image.getHeight());
				sb.append('\n');
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * Stringify settings
	 *
	 * @param settings settings
	 * @return string for settings
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public String toString(@NonNull final Settings settings)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		sb.append("SETTINGS\n") //
				.append("BackColor=").append(Utils.colorToString(settings.backColor)).append('\n') //
				.append("ForeColor=").append(Utils.colorToString(settings.foreColor)).append('\n') //
				.append("BackgroundImage=").append(settings.backgroundImageFile).append('\n') //
				.append("FontFace=").append(settings.fontFace).append('\n') //
				.append("FontSize=").append(settings.fontSize).append('\n') //
				.append("FontSizeFactor=").append(settings.fontSizeFactor).append('\n') //
				.append("DownScaleFonts=").append(settings.downscaleFontsFlag).append('\n') //
				.append("FontScaler=").append(toString(settings.fontDownscaler)).append('\n') //
				.append("DownScaleImages=").append(settings.downscaleImagesFlag).append('\n') //
				.append("ImageScaler=").append(toString(settings.imageDownscaler)).append('\n') //
				.append("Orientation=").append(settings.orientation).append('\n') //
				.append("Expansion=").append(settings.expansion).append('\n') //
				.append("Sweep=").append(settings.sweep).append('\n') //
				.append("PreserveOrientationFlag=").append(settings.preserveOrientationFlag).append('\n') //
				.append("HasToolbarFlag=").append(settings.hasToolbarFlag).append('\n') //
				.append("HasStatusbarFlag=").append(settings.hasStatusbarFlag).append('\n') //
				.append("HasPopUpMenuFlag=").append(settings.hasPopUpMenuFlag).append('\n') //
				.append("HasToolTipFlag=").append(settings.hasToolTipFlag).append('\n') //
				.append("ToolTipDisplaysContentFlag=").append(settings.toolTipDisplaysContentFlag).append('\n') //
				.append("FocusOnHoverFlag=").append(settings.focusOnHoverFlag).append('\n') //
				.append("Focus=").append(settings.focus).append('\n') //
				.append("XMoveTo=").append(settings.xMoveTo).append('\n') //
				.append("YMoveTo=").append(settings.yMoveTo).append('\n') //
				.append("XShift=").append(settings.xMoveTo).append('\n') //
				.append("YShift=").append(settings.yMoveTo).append('\n') //
				.append("NodeBackColor=").append(Utils.colorToString(settings.nodeBackColor)).append('\n') //
				.append("NodeForeColor=").append(Utils.colorToString(settings.nodeForeColor)).append('\n') //
				.append("DefaultNodeImage=").append(settings.defaultNodeImage).append('\n') //
				.append("BorderFlag=").append(settings.borderFlag).append('\n') //
				.append("EllipsizeFlag=").append(settings.ellipsizeFlag).append('\n') //
				.append("LabelMaxLines=").append(settings.labelMaxLines).append('\n') //
				.append("LabelExtraLineFactor=").append(settings.labelExtraLineFactor).append('\n') //
				.append("TreeEdgeColor=").append(Utils.colorToString(settings.treeEdgeColor)).append('\n') //
				.append("TreeEdgeStyle=").append(ModelDump.toString(settings.treeEdgeStyle)).append('\n') //
				.append("DefaultTreeEdgeImage=").append(settings.defaultTreeEdgeImage).append('\n') //
				.append("EdgesAsArcsFlag=").append(settings.edgesAsArcsFlag).append('\n') //
				.append("EdgeColor=").append(Utils.colorToString(settings.edgeColor)).append('\n') //
				.append("EdgeStyle=").append(ModelDump.toString(settings.edgeStyle)).append('\n') //
				.append("DefaultEdgeImage=").append(settings.defaultEdgeImage).append('\n');
		if (settings.menu == null)
		{
			sb.append("Menu=null");
		}
		else
		{
			for (@NonNull final MenuItem menuItem : settings.menu)
			{
				sb.append("MenuItem") //
						.append(" action=").append(menuItem.action == null ? "" : menuItem.action) //
						.append(" link=").append(menuItem.link == null ? "" : menuItem.link) //
						.append(" target=").append(menuItem.matchTarget == null ? "" : menuItem.matchTarget) //
						.append(" scope=").append(menuItem.matchScope == null ? "" : menuItem.matchScope) //
						.append(" mode=").append(menuItem.matchMode == null ? "" : menuItem.matchMode) //
						.append(" label=").append(menuItem.label == null ? "" : menuItem.label) //
						.append('\n');
			}
		}
		return sb.toString();
	}

	/**
	 * Stringify style
	 *
	 * @param style style
	 * @return string for style
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public String toString(final Integer style)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		@NonNull final String[] strings = Utils.toStrings(style);
		sb.append("hidden=").append(strings[0]);
		sb.append(" line=").append(strings[1]);
		sb.append(" stroke=").append(strings[2]);
		sb.append(" width=").append(strings[3]);
		sb.append(" fromterminator=").append(strings[4]);
		sb.append(" toterminator=").append(strings[5]);
		return sb.toString();
	}

	@NonNull
	static private String toString(@Nullable final float[] floats)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		if (floats != null)
		{
			boolean first = true;
			for (float f : floats)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					sb.append(' ');
				}
				sb.append(f);
			}
		}
		return sb.toString();
	}
}
