package pdt.y.preferences;

import pdt.y.model.labels.BracketLabel;
import pdt.y.model.labels.MiddleLabel;
import pdt.y.model.labels.PostfixLabel;
import pdt.y.model.labels.PrefixLabel;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	/* UPDATE MODE */
	
	public static final String UPDATE_MODE = "UPDATE_MODE";
	
	public static final String UPDATE_MODE_MANUAL = "UPDATE_MODE_MANUAL";
	
	public static final String UPDATE_MODE_AUTOMATIC = "UPDATE_MODE_AUTOMATIC";
	
	public static final String SHOW_TOOLTIPS = "SHOW_TOOLTIPS";
	
	/* NAME CROPPING */
	
	public static final String NAME_CROPPING = "NAME_CROPPING";
	
	public static final String NAME_CROPPING_PREFIX = PrefixLabel.class.getSimpleName();
	
	public static final String NAME_CROPPING_POSTFIX = PostfixLabel.class.getSimpleName();
	
	public static final String NAME_CROPPING_BRACKET = BracketLabel.class.getSimpleName();
	
	public static final String NAME_CROPPING_MIDDLE = MiddleLabel.class.getSimpleName();
	
	/* NODE SIZE */
	
	public static final String NODE_SIZE = "NODE_SIZE";
	
	public static final String NODE_SIZE_FIXED = "NODE_SIZE_FIXED";
	
	public static final String NODE_SIZE_FIXED_WIDTH = "NODE_SIZE_FIXED_WIDTH";
	
	public static final String NODE_SIZE_FIXED_HEIGHT = "NODE_SIZE_FIXED_HEIGHT";
	
	public static final String NODE_SIZE_MEDIAN = "NODE_SIZE_MEDIAN";
	
	public static final String NODE_SIZE_MAXIMUM = "NODE_SIZE_MAXIMUM";
	
	public static final String NODE_SIZE_INDIVIDUAL = "NODE_SIZE_INDIVIDUAL";
	
	/* LAYOUT ALGORITHM */
	
	public static final String LAYOUT = "LAYOUT";
	
	public static final String LAYOUT_HIERARCHY = "LAYOUT_HIERARCHY";
	
	public static final String LAYOUT_ORGANIC = "LAYOUT_ORGANIC";

	
	/* Appearance */
	
	public static final String APPEARANCE_PREDICATE_COLOR = "APPEARANCE_PREDICATE_COLOR";
	
	public static final String APPEARANCE_EXPORTED_PREDICATE_COLOR = "APPEARANCE_EXPORTED_PREDICATE_COLOR";
	
	public static final String APPEARANCE_BORDER_COLOR = "APPEARANCE_BORDER_COLOR";
	
	public static final String APPEARANCE_UNUSED_PREDICATE_BORDER_COLOR = "APPEARANCE_UNUSED_PREDICATE_BORDER_COLOR";
	
	public static final String APPEARANCE_MODULE_HEADER_COLOR = "APPEARANCE_MODULE_HEADER_COLOR";
	
	public static final String APPEARANCE_FILE_HEADER_COLOR = "APPEARANCE_FILE_HEADER_COLOR";
	
	public static final String APPEARANCE_MODULE_FILE_BACKGROUND_COLOR = "APPEARANCE_MODULE_FILE_BACKGROUND_COLOR";
	
	public static final String APPEARANCE_LINE_COLOR = "APPEARANCE_LINE_COLOR";
	
	public static final String APPEARANCE_BORDER_STYLE = "APPEARANCE_BORDER_STYLE";

	public static final int APPEARANCE_BORDER_STYLE_SOLID = 0;
	
	public static final int APPEARANCE_BORDER_STYLE_DASHED = 1;
	
	public static final int APPEARANCE_BORDER_STYLE_DOTTED = 2;
	
	public static final int APPEARANCE_BORDER_STYLE_DASHED_DOTTED = 3;
	
	public static final String APPEARANCE_DYNAMIC_PREDICATE_BORDER_STYLE = "APPEARANCE_DYNAMIC_PREDICATE_BORDER_STYLE";
}
