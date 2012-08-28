/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * WWW: http://sewiki.iai.uni-bonn.de/research/pdt/start
 * Mail: pdt@lists.iai.uni-bonn.de
 * Copyright (C): 2004-2012, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 ****************************************************************************/

package org.cs3.pdt.actions;

import org.cs3.pdt.internal.editors.PLEditor;
import org.cs3.pdt.transform.internal.wizards.MyWizard;
import org.cs3.prolog.common.logging.Debug;
import org.cs3.prolog.pif.PrologInterface;
import org.cs3.prolog.ui.util.UIUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;


public class ApplyTransformation implements IEditorActionDelegate{

	private IEditorPart editor;

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor=targetEditor;
	}

	@Override
	public void run(IAction action) {
		
		ApplyTransformationInfo info = new ApplyTransformationInfo(getCurrentPrologInterface());
		ApplyTransformationProcessor processor = new ApplyTransformationProcessor(info);
		ApplyTransformationRefactoring refac = new ApplyTransformationRefactoring(processor);
		MyWizard wizard = new MyWizard(refac);
		RefactoringWizardOpenOperation op 
	      = new RefactoringWizardOpenOperation( wizard );
	    try {
	      String titleForFailedChecks = ""; //$NON-NLS-1$
	      op.run( editor.getSite().getShell(), titleForFailedChecks );
	    } catch( final InterruptedException irex ) {
	      // operation was cancelled
	    }
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}
	
	public PrologInterface getCurrentPrologInterface(){
		IEditorPart editor = UIUtils.getActiveEditor();
		
		PLEditor plEditor=null;
		if (editor instanceof PLEditor) {			
			plEditor = (PLEditor) editor;
		}
		if(plEditor==null){
			return null;
		}
		IEditorInput input = plEditor.getEditorInput();
		IFileEditorInput fileInput=null;
		if (input instanceof IFileEditorInput) {			
			fileInput = (IFileEditorInput) input;			
		}
		if(fileInput==null){
			return null;
		}
		IProject project = fileInput.getFile().getProject();
		IPrologProject plProject=null;
		try {
			if(project.hasNature(PDTCore.NATURE_ID)){
				plProject=(IPrologProject) project.getNature(PDTCore.NATURE_ID);
			}
		} catch (CoreException e) {
			Debug.report(e);
			throw new RuntimeException(e);
		}
		if(plProject==null){
			return null;
		}
		return plProject.getMetadataPrologInterface();
	}
}


