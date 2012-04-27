/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * Author: Lukas Degener (among others) 
 * E-mail: degenerl@cs.uni-bonn.de
 * WWW: http://roots.iai.uni-bonn.de/research/pdt 
 * Copyright (C): 2004-2006, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * In addition, you may at your option use, modify and redistribute any
 * part of this program under the terms of the GNU Lesser General Public
 * License (LGPL), version 2.1 or, at your option, any later version of the
 * same license, as long as
 * 
 * 1) The program part in question does not depend, either directly or
 *   indirectly, on parts of the Eclipse framework and
 *   
 * 2) the program part in question does not include files that contain or
 *   are derived from third-party work and are therefor covered by special
 *   license agreements.
 *   
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *   
 * ad 1: A program part is said to "depend, either directly or indirectly,
 *   on parts of the Eclipse framework", if it cannot be compiled or cannot
 *   be run without the help or presence of some part of the Eclipse
 *   framework. All java classes in packages containing the "pdt" package
 *   fragment in their name fall into this category.
 *   
 * ad 2: "Third-party code" means any code that was originaly written as
 *   part of a project other than the PDT. Files that contain or are based on
 *   such code contain a notice telling you so, and telling you the
 *   particular conditions under which they may be used, modified and/or
 *   distributed.
 ****************************************************************************/

package org.cs3.pdt.internal.editors;

import org.eclipse.swt.graphics.RGB;

public interface PDTColors {
	public static final RGB BACKGROUND        = new RGB(255, 255, 255);
	public static final RGB BACKGROUND_EXTERN = new RGB(240, 240, 240);
	public static final RGB DEFAULT           = new RGB(  0,   0,   0);   // black        
	public static final RGB STRING            = new RGB(  0,   0, 255);
	public static final RGB COMMENT           = new RGB( 63, 127,  95);   // RGB values for Eclipse Java comments.	
	public static final RGB VARIABLE          = new RGB(139,   0,   0);
	public static final RGB UNDEFINED         = new RGB(255,   0,   0);   // RED = Call to undefined  predicate
	public static final RGB BUILTIN           = new RGB(  0,   0, 128);   // 
	public static final RGB DYNAMIC           = new RGB(110,  40,  40);   // dark brown
	public static final RGB TRANSPARENT       = new RGB(255,  80, 180);   // pink for module_transparent
	public static final RGB META              = new RGB( 15, 160,  15);   // dark green for meta_predicate
	
	public static final String PREF_BACKGROUND = "pdt.editor.colors.background";
	public static final String PREF_BACKGROUND_EXTERNAL_FILES = "pdt.editor.colors.backgroundextern";
	public static final String PREF_DEFAULT = "pdt.editor.colors.default";
	public static final String PREF_STRING = "pdt.editor.colors.string";
	public static final String PREF_COMMENT = "pdt.editor.colors.comment";
	public static final String PREF_VARIABLE = "pdt.editor.colors.variable";
	public static final String PREF_UNDEFINED = "pdt.editor.colors.undefined";
	public static final String PREF_BUILTIN = "pdt.editor.colors.buildin";
	public static final String PREF_DYNAMIC = "pdt.editor.colors.dynamic";
	public static final String PREF_TRANSPARENT = "pdt.editor.colors.transparent";
	public static final String PREF_META = "pdt.editor.colors.meta";
	
	public static final String META_PREDICATE_STRING = "Meta-Predicate";
	public static final String MODULE_TRANSPARENT_STRING = "Module-Transparent";
	public static final String DYNAMIC_STRING = "Dynamic";
	public static final String BUILT_IN_STRING = "Built-In";
	public static final String UNDEFINED_STRING = "Undefined";
	public static final String VARIABLE_STRING = "Variable";
	public static final String COMMENT_STRING = "Comment";
	public static final String STRING_STRING = "Atom";
	public static final String DEFAULT_STRING = "Other";
	public static final String BACKGROUND_STRING = "Project files";
	public static final String BACKGROUND_EXTERN_STRING = "External files";
}
