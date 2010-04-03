/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.phpsrc.eclipse.pti.ui.Logger;

public abstract class AbstractPHPToolPlugin extends AbstractUIPlugin {
	public URL resolvePluginResourceURL(String resource) {
		URL u = getBundle().getEntry(resource);
		if (u != null) {
			try {
				return FileLocator.resolve(u);
			} catch (IOException e) {
				Logger.logException(e);
			}
		}

		return null;
	}

	public IPath resolvePluginResource(String resource) {
		URL u = resolvePluginResourceURL(resource);
		if (u != null)
			return new Path(new java.io.File(u.getFile()).getAbsolutePath());

		return null;
	}

	public static IFile resolveProjectFile(String filePath) {
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(
				new java.io.File(filePath).toURI());
		if (files != null && files.length > 0)
			return files[0];

		return null;
	}

	public abstract IPath[] getPluginIncludePaths(IProject project);
}
