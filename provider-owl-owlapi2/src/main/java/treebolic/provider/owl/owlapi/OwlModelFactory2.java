/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.MutableEdge;
import treebolic.model.MutableNode;

/**
 * OWL model factory
 *
 * @author Bernard Bou
 */
public class OwlModelFactory2 extends OwlModelFactory
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public OwlModelFactory2(final Properties properties)
	{
		super(properties);
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
	@Nullable
	@Override
	public Model makeModel(final String ontologyUrlString)
	{
		@Nullable Model model = super.makeModel(ontologyUrlString);
		if (model == null)
		{
			return null;
		}
		return new Model(model.tree, model.settings, ImageFactory.images);
	}

	// D E C O R A T E

	@Override
	public void setNodeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setImageIndex(index);
		}
	}

	@Override
	public void setTreeEdgeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setEdgeImageIndex(index);
		}
	}

	@Override
	public void setEdgeImage(@NonNull final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			edge.setImageIndex(index);
		}
	}
}
