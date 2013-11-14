package org.cs3.pdt.common.internal.externalproject;

import org.cs3.prolog.ui.util.ExternalPrologFilesProjectUtils;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class IsExternalPropertyTester extends PropertyTester {

	public static final String IN_PROJECT = "isInExternalProject";
	public static final String IS_PROJECT = "isExternalProject";
	public static final String IS_OR_IN_PROJECT = "isOrInExternalProject";
	
	public IsExternalPropertyTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		try {
			if (receiver instanceof IFile){
				if (IN_PROJECT.equals(property) || IS_OR_IN_PROJECT.equals(property)) {
					return isExternalProject(((IFile) receiver).getProject());
				}
			} else if (receiver instanceof IFolder) {
				if (IN_PROJECT.equals(property) || IS_OR_IN_PROJECT.equals(property)) {
					return isExternalProject(((IFolder) receiver).getProject());
				}
			} else if (receiver instanceof IProject) {
				if (IS_PROJECT.equals(property) || IS_OR_IN_PROJECT.equals(property)) {
					return isExternalProject(((IProject) receiver).getProject());
				}
			}
		} catch (CoreException e) {
		}
		return false;
	}

	private boolean isExternalProject(IProject project) throws CoreException {
		return project.equals(ExternalPrologFilesProjectUtils.getExternalPrologFilesProject());
	}

}
