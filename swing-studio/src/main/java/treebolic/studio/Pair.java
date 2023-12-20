/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

/**
 * Pair
 *
 * @param <T1> type of first member of the pair
 * @param <T2> type of second member of the pair
 * @author Bernard Bou
 */
public class Pair<T1, T2>
{
	/**
	 * Constructor
	 *
	 * @param first  first member of the pair
	 * @param second second member of the pair
	 */
	public Pair(final T1 first, final T2 second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * First member of the pair
	 */
	public final T1 first;

	/**
	 * Second member of the pair
	 */
	public final T2 second;
}
