/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.provider.IProvider;

/**
 * Provider for OWL
 *
 * @author Bernard Bou
 */
public class Provider2 extends BaseProvider
{
	protected OwlModelFactory factory(@NonNull Properties properties)
	{
		return new OwlModelFactory2(properties);
	}
}
