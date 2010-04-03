/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PHPToolsLibraryPreferencePage extends PropertyAndPreferencePage implements IWorkbenchPreferencePage {

	public PHPToolsLibraryPreferencePage() {
		super();
		noDefaultAndApplyButton();
	}

	public PHPToolsLibraryPreferencePage(String title) {
		super();
	}

	public PHPToolsLibraryPreferencePage(String title, ImageDescriptor image) {
		super();
	}

	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);

		Label descLabel = new Label(comp, SWT.NONE);
		descLabel.setText("Expand the tree to edit PHP libraries preferences");

		return comp;
	}

	public void init(IWorkbench workbench) {
	}

	protected Control createPreferenceContent(Composite composite) {
		return null;
	}

	protected String getPreferencePageID() {
		return null;
	}

	protected String getPropertyPageID() {
		return null;
	}

	protected boolean hasProjectSpecificOptions(IProject project) {
		return false;
	}
}