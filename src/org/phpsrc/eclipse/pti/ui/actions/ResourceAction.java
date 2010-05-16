/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.ui.Logger;

public abstract class ResourceAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private IResource[] selectedResources;

	public void dispose() {
		selectedResources = null;
	}

	public void init(IWorkbenchWindow window) {
		selectedResources = new IResource[0];
		this.window = window;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		ArrayList<IResource> resources = new ArrayList<IResource>(1);
		if (selection.isEmpty()) {
			addActiveEditorFileToList(resources);
		} else if (selection instanceof ITextSelection) {
			addActiveEditorFileToList(resources);
		} else if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			resources = new ArrayList<IResource>(structuredSelection.size());
			Iterator<?> iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object entry = iterator.next();
				try {
					if (entry instanceof IResource) {
						addResourceToList(resources, (IResource) entry);
					} else if (entry instanceof ISourceModule) {
						if (((ISourceModule) entry).exists()) {
							IFile file = (IFile) ((ISourceModule) entry).getCorrespondingResource();
							if (PHPToolkitUtil.isPhpFile(file)) {
								addResourceToList(resources, file);
							}
						}
					} else if (entry instanceof IOpenable) {
						if (((IOpenable) entry).exists()) {
							addResourceToList(resources, ((IOpenable) entry).getCorrespondingResource());
						}
					} else if (entry instanceof IMember) {
						if (((IMember) entry).exists()) {
							addResourceToList(resources, ((IMember) entry).getResource());
						}
					} else if (entry instanceof IFileEditorInput) {
						if (((IFileEditorInput) entry).exists()) {
							addResourceToList(resources, ((IFileEditorInput) entry).getFile());
						}
					} else if (entry instanceof IScriptFolder) {
						if (((IScriptFolder) entry).exists()) {
							addResourceToList(resources, ((IScriptFolder) entry).getResource());
						}
					}
				} catch (ModelException e) {
					Logger.logException(e);
				}
			}
		} else {
			addActiveEditorFileToList(resources);
		}

		selectedResources = resources.toArray(new IResource[0]);
	}

	public void setSelectedResources(IResource[] resources) {
		selectedResources = resources;
	}

	public IResource[] getSelectedResources() {
		return selectedResources;
	}

	private void addResourceToList(ArrayList<IResource> list, IResource resource) {
		if (resource != null && resource.exists() && !list.contains(resource) && canAddResourceToList(resource))
			list.add(resource);
	}

	protected void addActiveEditorFileToList(ArrayList<IResource> list) {
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				if (input != null && input instanceof IFileEditorInput) {
					addResourceToList(list, ((IFileEditorInput) input).getFile());
				}
			}
		}
	}

	protected boolean canAddResourceToList(IResource resource) {
		return true;
	}
}