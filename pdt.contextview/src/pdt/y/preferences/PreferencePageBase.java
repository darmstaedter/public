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

package pdt.y.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbenchPreferencePage;

import pdt.y.main.PluginActivator;
import pdt.y.main.PreferencesUpdateListener;

public abstract class PreferencePageBase 
		extends FieldEditorPreferencePage 
		implements IWorkbenchPreferencePage, PreferencesUpdateListener {

	private List<FieldEditor> editors = new LinkedList<FieldEditor>();
	private GridData parentData;
	protected GridData defaultAligmentData;
	
	public PreferencePageBase() {
		super(GRID);
		setPreferenceStore(PluginActivator.getDefault().getPreferenceStore());
		PluginActivator.getDefault().addPreferencesUpdateListener(this);
		
		parentData = new GridData(GridData.FILL_HORIZONTAL);
        parentData.horizontalSpan = 2;
        parentData.verticalIndent = 5;
        
        defaultAligmentData = new GridData();
        defaultAligmentData.horizontalIndent = 9;
        defaultAligmentData.verticalIndent = 5;
	}
	
	@Override
	public boolean performOk() {
		boolean res = super.performOk();
		if (res) {
			//PluginActivator.getDefault().preferencesUpdated();
		}
		return res;
	}
	
	@Override
	protected void addField(FieldEditor editor) {
		editors.add(editor);
		super.addField(editor);
	}
	
	@Override
	public void preferencesUpdated() {
		for (FieldEditor e : editors) {
			if (e != null) {
				e.load();
			}
		}
	}
	
	@Override
	public void dispose() {
		PluginActivator.getDefault().removePreferencesUpdateListener(this);
		super.dispose();
	}
	
	protected Group createGroup(String title, Layout groupLayout) {
		
		Group predicateBgColor = new Group(getFieldEditorParent(), SWT.NONE);
		predicateBgColor.setText(title);
		predicateBgColor.setLayout(groupLayout);
		predicateBgColor.setLayoutData(parentData);
		return predicateBgColor;
	}
	
	protected Composite wrap(Composite parent) {
		return wrap(parent, null);
	}
	
	protected Composite wrap(Composite parent, Object layoutData) {
		
		Composite w = new Composite(parent, SWT.NONE);
		if (layoutData == null) {
			layoutData = defaultAligmentData;
		}
		w.setLayoutData(layoutData);
		return w;
	}
}

