/**
 * Title : Treebolic
 * Description: Treebolic
 * Version: 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 * Update : Jan 8, 2016
 */

package treebolic.glue.component;

import java.awt.Dimension;
import java.awt.Font;

/**
 * @author bbou
 */
public class Constants
{
	public static final Dimension DIM_CONTAINER = new Dimension(900, 900);

	public static final Dimension DIM_PROGRESS = new Dimension(300, 80);

	public static final Dimension DIM_PROGRESS_PROGRESS = new Dimension(200, 32);

	public static final Dimension DIM_PROGRESS_TEXT = new Dimension(300, 32);

	public static final Dimension DIM_SEARCH_LABEL = new Dimension(200, 24);

	public static final Dimension DIM_STATUS_LABEL = new Dimension(400, 24);

	public static final Dimension DIM_STATUS_CONTENT = new Dimension(400, 34);

	public static final String FONT_FAMILY = Font.SANS_SERIF;
	
	public static final Font FONT_PROGRESS_LABEL = new Font(FONT_FAMILY, Font.BOLD, 20); 

	public static final Font FONT_PROGRESS_TEXT = new Font(FONT_FAMILY, Font.PLAIN, 10); 

	public static final Font FONT_WEB_HEADER = new Font(FONT_FAMILY, Font.BOLD, 16); 
}
