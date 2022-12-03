/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.jena;

import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream factories
 */
public class StreamUtils
{
	public static <T> Stream<T> toStream2(final Iterator<T> sourceIterator)
	{
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(sourceIterator, Spliterator.ORDERED | Spliterator.SORTED), false);
	}

	public static <T> Stream<T> toStream(final Iterator<T> sourceIterator)
	{
		Iterable<T> iterable = () -> sourceIterator;
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
