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

package org.cs3.pdt.internal.actions;

import java.util.ResourceBundle;

import org.cs3.pdt.PDT;
import org.cs3.pdt.core.IPrologProject;
import org.cs3.pdt.core.PDTCore;
import org.cs3.pdt.internal.editors.PLEditor;
import org.cs3.pdt.internal.search.DefinitionsSearchQuery;
import org.cs3.pdt.ui.util.UIUtils;
import org.cs3.pl.common.Debug;
import org.cs3.pl.metadata.GoalData;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;


/**
 * @see IWorkbenchWindowActionDelegate
 */
public class FindDefinitionsActionDelegate extends TextEditorAction {
	public FindDefinitionsActionDelegate(ITextEditor editor) {
		super(ResourceBundle.getBundle(PDT.RES_BUNDLE_UI),FindDefinitionsActionDelegate.class.getName(), editor);
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@Override
	public void run() {

		UIUtils.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					PLEditor editor = (PLEditor) UIUtils
					.getActiveEditor();
					IPrologProject plProject =null;
					if(editor.getEditorInput() instanceof IFileEditorInput){
						IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
						plProject = (IPrologProject) editorInput.getFile().getProject().getNature(PDTCore.NATURE_ID);
					} 
//					else {
//						FileStoreEditorInput fStoreInput = (FileStoreEditorInput)editor.getEditorInput();
//						//Path filepath = new Path(fStoreInput.getURI().getPath());
//						IFile[] file = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(fStoreInput.getURI());
//					}
					GoalData data = editor.getSelectedPrologElement();
					if(data == null){
						Debug.warning("data is null");
						return;
					}
					ISearchQuery query = new DefinitionsSearchQuery(plProject==null?null:plProject.getMetadataPrologInterface(),data);
					NewSearchUI.activateSearchResultView();
					NewSearchUI.runQueryInForeground(null,query);
					if(plProject!=null) plProject.updateMarkers();
				} catch (Exception e) {
					Debug.report(e);
				}
			}
		});
	}

	public void dispose() {
	}
}