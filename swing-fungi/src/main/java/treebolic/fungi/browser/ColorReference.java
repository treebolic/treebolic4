/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.fungi.browser;

/**
 * @author Bernard Bou
 */
public enum ColorReference
{
	// @formatter:off
	/** Backcolor */ BACKCOLOR("backcolor"), //
	/** Domain backcolor */ DOMAIN_BGCOLOR("d_bcolor"), //
	/** Domain forecolor */ DOMAIN_FGCOLOR("d_fcolor"), //
	/** Class backcolor */ CLASS_BGCOLOR("c_bcolor"), //
	/** Class forecolor*/ CLASS_FGCOLOR("c_fcolor"), //
	/** Order backcolor */ ORDER_BGCOLOR("o_bcolor"), //
	/** Order forecolor */ ORDER_FGCOLOR("o_fcolor"), //
	/** Family backcolor */ FAMILY_BGCOLOR("f_bcolor"), //
	/** Family forecolor */ FAMILY_FGCOLOR("f_fcolor"), //
	/** Genus backcolor */ GENUS_BGCOLOR("g_bcolor"), //
	/** Genus forecolor */ GENUS_FGCOLOR("g_fcolor"), //
	/** Species backcolor */ SPECIES_BGCOLOR("s_bcolor"), //
	/** Species forecolor */ SPECIES_FGCOLOR("s_fcolor"), //
	// @formatter:on
	;

	/**
	 * Key
	 */
	public final String key;

	ColorReference(final String key)
	{
		this.key = key;
	}

	/**
	 * Get label
	 *
	 * @return label
	 */
	public final String getLabel()
	{
		return Messages.getString("ColorReference." + this.key);
	}
}
