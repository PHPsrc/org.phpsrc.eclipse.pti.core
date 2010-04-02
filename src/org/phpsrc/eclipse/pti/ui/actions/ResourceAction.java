/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.ui.Logger;

public abstract class ResourceAction implements IWorkbenchWindowActionDelegate {
	private IResource[] selectedResources;

	public void dispose() {
		selectedResources = null;
	}

	public void init(IWorkbenchWindow window) {
		selectedResources = new IResource[0];
	}

	public void selectionChanged(IAction action, ISelection selection) {
		ArrayList<IResource> resources = new ArrayList<IResource>(1);
		if (selection instanceof ITextSelection) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null && page.getActiveEditor() != null) {
				IEditorInput input = page.getActiveEditor().getEditorInput();
				if (input != null && input instanceof IFileEditorInput) {
					addResourceToList(resources, ((IFileEditorInput) input).getFile());
				}
			}
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
	
	protected boolean canAddResourceToList(IResource resource) {
		return true;
	}
}