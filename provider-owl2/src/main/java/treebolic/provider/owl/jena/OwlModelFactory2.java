/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.jena;

import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Image;
import treebolic.model.Model;
import treebolic.model.MutableNode;

/**
 * OWL model factory
 *
 * @author Bernard Bou
 */
public class OwlModelFactory2 extends OwlModelFactory
{
	/**
	 * ImageFactory
	 *
	 * @param imageUrls image urls
	 * @return images
	 */
	public Image[] makeImages(@NonNull final String[] imageUrls)
	{
		Image[] images = new Image[imageUrls.length];
		for (int i = 0; i < imageUrls.length; i++)
		{
			images[i] = new treebolic.glue.Image(Provider2.class.getResource("images/" + imageUrls[i]));
		}
		return images;
	}

	// D E C O R A T I O N   M E M B E R S

	static Image[] images2;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public OwlModelFactory2(final Properties properties)
	{
		super(properties);
		images2 = makeImages(images);
		loadBalancer.setGroupNode(null, LOADBALANCING_BACKCOLOR, LOADBALANCING_FORECOLOR, LOADBALANCING_EDGECOLOR, LOADBALANCING_EDGE_STYLE, LOADBALANCING_IMAGEINDEX, LOADBALANCING_IMAGE, null);
		instancesLoadBalancer.setGroupNode(null, LOADBALANCING_INSTANCES_BACKCOLOR, LOADBALANCING_INSTANCES_FORECOLOR, LOADBALANCING_INSTANCES_EDGECOLOR, LOADBALANCING_INSTANCES_EDGE_STYLE, LOADBALANCING_INSTANCES_IMAGEINDEX, LOADBALANCING_INSTANCES_IMAGE, null);
		propertiesLoadBalancer.setGroupNode(null, LOADBALANCING_PROPERTIES_BACKCOLOR, LOADBALANCING_PROPERTIES_FORECOLOR, LOADBALANCING_PROPERTIES_EDGECOLOR, LOADBALANCING_PROPERTIES_EDGE_STYLE, LOADBALANCING_PROPERTIES_IMAGEINDEX, LOADBALANCING_PROPERTIES_IMAGE, null);
	}

	// P A R S E

	/**
	 * Make model
	 *
	 * @param ontologyUrlString ontology URL string
	 * @return model if successful
	 */
	@Override
	public Model makeModel(final String ontologyUrlString)
	{
		Model model = super.makeModel(ontologyUrlString);
		return new Model(model.tree, model.settings, images2);
	}

	// D E C O R A T E
	@Override
	protected void setNodeImage(final MutableNode node, @Nullable final String imageFile, @Nullable final ImageIndex index)
	{
		if (imageFile != null)
		{
			node.setImageFile(imageFile);
		}
		else if (index != null)
		{
			node.setImageIndex(index.ordinal());
		}
	}

	@Override
	protected void setNodeEdgeImage(final MutableNode node, @Nullable final String edgeImageFile, @Nullable final ImageIndex index)
	{
		if (edgeImageFile != null)
		{
			node.setEdgeImageFile(edgeImageFile);
		}
		else if (index != null)
		{
			node.setEdgeImageIndex(index.ordinal());
		}
	}
}
