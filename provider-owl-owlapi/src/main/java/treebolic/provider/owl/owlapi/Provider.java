/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import java.util.Properties;

import treebolic.annotations.NonNull;

/**
 * Provider for OWL
 *
 * @author Bernard Bou
 */
public class Provider extends BaseProvider
{
	@NonNull
	protected OwlModelFactory factory(@NonNull Properties properties)
	{
		return new OwlModelFactory(properties);
	}
}
