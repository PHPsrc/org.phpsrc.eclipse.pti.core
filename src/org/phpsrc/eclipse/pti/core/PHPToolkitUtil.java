/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.phpsrc.eclipse.pti.core.search.PHPSearchEngine;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPToolkitUtil {

	public static boolean isPhpElement(final IModelElement modelElement) {
		Assert.isNotNull(modelElement);
		IModelElement sourceModule = modelElement.getAncestor(IModelElement.SOURCE_MODULE);
		if (sourceModule != null) {
			return isPhpFile((ISourceModule) sourceModule);
		}
		return false;
	}

	public static boolean isPhpFile(final ISourceModule sourceModule) {
		try {
			IResource resource = sourceModule.getCorrespondingResource();
			if (resource instanceof IFile) {
				IContentDescription contentDescription = ((IFile) resource).getContentDescription();
				return IPHPCoreConstants.ContentTypeID_PHP.equals(contentDescription.getContentType().getId());
			}
		} catch (CoreException e) {
		}
		return hasPhpExtention(sourceModule.getElementName());
	}

	public static boolean isPhpFile(final IFile file) {
		IContentDescription contentDescription = null;
		if (!file.exists()) {
			return hasPhpExtention(file);
		}
		try {
			contentDescription = file.getContentDescription();
		} catch (final CoreException e) {
			return hasPhpExtention(file);
		}

		if (contentDescription == null) {
			return hasPhpExtention(file);
		}

		return IPHPCoreConstants.ContentTypeID_PHP.equals(contentDescription.getContentType().getId());
	}

	public static boolean hasPhpExtention(final IFile file) {
		final String fileName = file.getName();
		final int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = fileName.substring(index + 1);
		final IContentType type = Platform.getContentTypeManager().getContentType(IPHPCoreConstants.ContentTypeID_PHP);
		final String[] validExtensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
		for (String validExtension : validExtensions) {
			if (extension.equalsIgnoreCase(validExtension)) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasPhpExtention(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException();
		}

		final int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return false;
		}
		String extension = fileName.substring(index + 1);

		final IContentType type = Platform.getContentTypeManager().getContentType(IPHPCoreConstants.ContentTypeID_PHP);
		final String[] validExtensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
		for (String validExtension : validExtensions) {
			if (extension.equalsIgnoreCase(validExtension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks weather the given project is a php project
	 * 
	 * @param project
	 * @return true for php projects
	 * @throws CoreException
	 */
	public static boolean isPhpProject(IProject project) throws CoreException {
		if (project == null || !project.isAccessible()) {
			return false;
		}

		final IProjectNature nature = project.getNature(IPHPCoreConstants.PHPNatureID);
		return nature != null;
	}

	/**
	 * Retrieves the source module related to the provide model element
	 * 
	 * @param element
	 * @return the source module related to the provide model element
	 */
	public static final ISourceModule getSourceModule(Object element) {

		if (element instanceof IFile) {
			return (ISourceModule) DLTKCore.create((IFile) element);
		}

		if (element instanceof IModelElement) {
			return getSourceModule((IModelElement) element);
		}

		if (element instanceof String) {
			return getSourceModule((String) element);
		}

		return null;
	}

	/**
	 * retrieves the source module from a model element
	 * 
	 * @param element
	 * @return source module
	 */
	public static ISourceModule getSourceModule(IModelElement element) {
		IModelElement mElement = (IModelElement) element;

		if (mElement.getElementType() == IModelElement.SOURCE_MODULE) {
			return (ISourceModule) element;
		}

		if (element instanceof IMember) {
			return ((IMember) element).getSourceModule();
		}

		return null;
	}

	/**
	 * retrieves the source module from a path
	 * 
	 * @param element
	 * @return source module
	 */
	public static ISourceModule getSourceModule(String element) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(element));
		if (resource != null && resource instanceof IFile) {
			return (ISourceModule) DLTKCore.create((IFile) resource);
		}
		return null;
	}

	public static File createTempFile(String fileName) {
		File tmpFile = null;
		try {
			// Create temporary directory:
			File tempDir = new File(System.getProperty("java.io.tmpdir"), "eclipse_pti"); //$NON-NLS-1$ //$NON-NLS-2$
			if (!tempDir.exists()) {
				tempDir.mkdir();
				tempDir.deleteOnExit();
			}
			tempDir = File.createTempFile("session", null, tempDir); //$NON-NLS-1$
			tempDir.delete(); // delete temp file
			tempDir.mkdir();
			tempDir.deleteOnExit();

			// Create empty configuration file:
			tmpFile = new File(tempDir, fileName);
			tmpFile.createNewFile();
			tmpFile.deleteOnExit();
		} catch (Exception e) {
			Logger.logException(e);
		}
		return tmpFile;
	}

	public static boolean hasSuperClass(IResource resource, String className) {
		ISourceModule module = PHPToolkitUtil.getSourceModule(resource);
		if (module != null)
			return hasSuperClass(module, className);

		return false;
	}

	public static boolean hasSuperClass(ISourceModule module, String className) {
		try {
			IType[] types = module.getAllTypes();
			if (types.length > 0) {
				String[] classes = types[0].getSuperClasses();
				for (String c : classes) {
					if (c.equals(className)) {
						return true;
					} else {
						SearchMatch[] matches = PHPSearchEngine.findClass(c, PHPSearchEngine.createProjectScope(module
								.getScriptProject().getProject()));
						for (SearchMatch match : matches) {
							if (hasSuperClass(match.getResource(), className))
								return true;
						}
					}
				}
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return false;
	}

	public static IMethod getClassMethod(ISourceModule module, String methodName) {
		Assert.isNotNull(module);
		Assert.isNotNull(methodName);
		try {
			IType newClass = module.getAllTypes()[0];
			for (IMethod method : newClass.getMethods()) {
				if (method.getElementName().equals(methodName)) {
					return method;
				}
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return null;
	}

	/**
	 * @since 1.4.0
	 */
	public static IMethod getClassMethod(ISourceModule module, String className, String methodName) {
		Assert.isNotNull(module);
		Assert.isNotNull(className);
		Assert.isNotNull(methodName);
		try {
			for (IType newClass : module.getAllTypes()) {
				if (newClass.getElementName().equals(className)) {
					for (IMethod method : newClass.getMethods()) {
						if (method.getElementName().equals(methodName)) {
							return method;
						}
					}
				}
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return null;
	}

	/**
	 * @since 1.4.0
	 */
	public static IType getClassType(ISourceModule module, String className) {
		Assert.isNotNull(module);
		Assert.isNotNull(className);
		try {
			for (IType newClass : module.getAllTypes()) {
				if (newClass.getElementName().equals(className)) {
					return newClass;
				}
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return null;
	}

	/**
	 * @since 1.4.0
	 */
	public static IMethod getFunction(ISourceModule module, String functionName) {
		Assert.isNotNull(module);
		Assert.isNotNull(functionName);
		try {
			for (IModelElement child : module.getChildren()) {
				if (child.getElementType() == IModelElement.METHOD && child.getElementName().equals(functionName)) {
					if (child instanceof IMethod)
						return (IMethod) child;
					else
						return null;
				}
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return null;
	}
}
