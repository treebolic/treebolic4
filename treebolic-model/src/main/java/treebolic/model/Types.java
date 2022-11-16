/*
 * Copyright (c) 2019-2022. Bernard Bou
 */

package treebolic.model;

/**
 * Types
 *
 * @author Bernard Bou
 */
public class Types
{
	/**
	 * Search Commands
	 */
	public enum SearchCommand
	{
		SEARCH, CONTINUE, RESET
	}

	/**
	 * Match scope
	 */
	public enum MatchScope
	{
		LABEL, CONTENT, LINK, ID
	}

	/**
	 * Match mode
	 */
	public enum MatchMode
	{
		EQUALS, STARTSWITH, INCLUDES
	}
}
