/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * Author: Lukas Degener (among others)
 * WWW: http://sewiki.iai.uni-bonn.de/research/pdt/start
 * Mail: pdt@lists.iai.uni-bonn.de
 * Copyright (C): 2004-2012, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 ****************************************************************************/

package org.cs3.pdt.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.cs3.pdt.runtime.PrologInterfaceRegistry;
import org.cs3.pdt.runtime.PrologRuntimePlugin;
import org.cs3.pdt.ui.util.DefaultErrorMessageProvider;
import org.cs3.pdt.ui.util.ErrorMessageProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class PDTCorePlugin extends AbstractUIPlugin implements IStartup{
	private static PDTCorePlugin plugin;
	
	public PDTCorePlugin() {
		super();
		plugin = this;
		try {
			ResourceBundle.getBundle("prg.cs3.pdt.PDTPluginResources");
		} catch (MissingResourceException x) {
		}
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static PDTCorePlugin getDefault() {
		return plugin;
	}	
	private void projectClosing(IProject project) {
		try {
			IPrologProject prologProject = PDTCoreUtils
					.getPrologProject(project);
			if (prologProject != null) {
				PrologInterfaceRegistry r = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
				r.removeSubscription(prologProject.getMetadataSubscription());
				r.removeSubscription(prologProject.getRuntimeSubscription());
			}
		} catch (CoreException e) {
			;
		}
	}

	private void projectOpened(IProject project) {
		try {
			IPrologProject prologProject = PDTCoreUtils
					.getPrologProject(project);
			if (prologProject != null) {
				PrologInterfaceRegistry r = PrologRuntimePlugin.getDefault().getPrologInterfaceRegistry();
				r.addSubscription(prologProject.getMetadataSubscription());
				r.addSubscription(prologProject.getRuntimeSubscription());
			}
		} catch (CoreException e) {
			;		
		}
	}

	private final class _ResourceDeltaVisitor implements IResourceDeltaVisitor {

		public _ResourceDeltaVisitor(IResourceChangeEvent event) {
			;
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			switch (delta.getResource().getType()) {
			case IResource.ROOT:
				return true;
			case IResource.PROJECT:
				IProject project = (IProject) delta.getResource();
				
					if (0 < (delta.getFlags() & IResourceDelta.OPEN)
							&& project.isOpen()){
						projectOpened(project);
					}
				return false;
			default:
				return false;
			}
		}
	}

	private final class _ResourceChangeListener implements
			IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			try {
				if(event.getType()==IResourceChangeEvent.POST_CHANGE&&delta!=null){
					delta.accept(new _ResourceDeltaVisitor(event));	
				}
				else if (event.getType()==IResourceChangeEvent.PRE_CLOSE){
					IProject project = (IProject) event.getResource();
					projectClosing(project);
				}
			} catch (CoreException e) {
				;
			}

		}

	}

	private ErrorMessageProvider errorMessageProvider;

	public ErrorMessageProvider getErrorMessageProvider() {
		if (errorMessageProvider == null) {
			errorMessageProvider = new DefaultErrorMessageProvider(this);
		}
		return errorMessageProvider;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ws.addResourceChangeListener(new _ResourceChangeListener());
	}

	/**
	 * look up a preference value.
	 * <p>
	 * will return user settings if available or default settings if not. If a
	 * system property with the given key is defined it will overrule any
	 * existing setting in the preference store. if the key is not defined, this
	 * method returns the given default..
	 * 
	 * @param key
	 * @return the value or specified default if no such key exists..
	 */
	public String getPreferenceValue(String key, String defaultValue) {

		IPreferencesService service = Platform.getPreferencesService();
		String qualifier = getBundle().getSymbolicName();
		String value = service.getString(qualifier, key, defaultValue, null);
		return System.getProperty(key, value);
	}


	public void reconfigure() {
		;
	}

	@Override
	public void earlyStartup() {
		
	}

}


