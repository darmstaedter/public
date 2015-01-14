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

package org.cs3.pdt.common.internal;

import org.cs3.pdt.common.callhierachy.CallHierarchyView;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;

public class PrologPerspective implements IPerspectiveFactory {
	
	private static final String CONSOLE_VIEW_ID = "org.cs3.pdt.console.internal.views.PrologConsoleView";
	public static final String CONSOLE_FOLDER = "prolog.perspective.console.folder";
	public static final String VIEWS_FOLDER = "prolog.perspective.views.folder";
	
	private static final String PACKAGE_EXPLORER = "org.eclipse.jdt.ui.PackageExplorer";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineViewShortCuts(layout);
		defineLayout(layout);
	}

	public void defineActions(IPageLayout layout) {
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		layout.addNewWizardShortcut("pdt.module.wizard");
	}

	private void defineViewShortCuts(IPageLayout layout) {
		layout.addShowViewShortcut(CallHierarchyView.ID);
		layout.addShowViewShortcut(CONSOLE_VIEW_ID);
	}
	
	public void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout consoleFolder = layout.createFolder(CONSOLE_FOLDER, IPageLayout.BOTTOM, 0.65f, editorArea);
		
		consoleFolder.addView(CONSOLE_VIEW_ID);
		consoleFolder.addView(IPageLayout.ID_PROBLEM_VIEW);
		consoleFolder.addView(NewSearchUI.SEARCH_VIEW_ID);
		
		layout.createFolder(VIEWS_FOLDER, IPageLayout.RIGHT, 0.5f, CONSOLE_FOLDER);
		
		IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
		if (viewRegistry.find(PACKAGE_EXPLORER) != null) {
			layout.addView(PACKAGE_EXPLORER, IPageLayout.LEFT, 0.2f, editorArea);
		} else {
			layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.2f, editorArea);
		}
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.8f, editorArea);
	}
	
}


