/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.jena;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import treebolic.annotations.NonNull;

/**
 * Stream factories
 */
public class StreamUtils
{
	/**
	 * Convert to stream
	 *
	 * @param sourceIterator source iterator
	 * @param <T>            type of objects in stream
	 * @return stream
	 */
	@NonNull
	public static <T> Stream<T> toStream2(@NonNull final Iterator<T> sourceIterator)
	{
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(sourceIterator, Spliterator.ORDERED | Spliterator.SORTED), false);
	}

	/**
	 * Convert to stream
	 *
	 * @param sourceIterator source iterator
	 * @param <T>            type of objects in stream
	 * @return stream
	 */
	@NonNull
	public static <T> Stream<T> toStream(final Iterator<T> sourceIterator)
	{
		@NonNull Iterable<T> iterable = () -> sourceIterator;
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
