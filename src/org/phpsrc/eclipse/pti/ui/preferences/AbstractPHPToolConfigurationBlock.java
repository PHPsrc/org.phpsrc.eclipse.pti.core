/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.core.IPHPCoreConstants;
import org.phpsrc.eclipse.pti.ui.Logger;

public abstract class AbstractPHPToolConfigurationBlock extends OptionsConfigurationBlock {

	private static final String PHP_EXE_PAGE_ID = "org.eclipse.php.debug.ui.preferencesphps.PHPsPreferencePage"; //$NON-NLS-1$

	protected Label phpExecutableNameLabel;
	protected Combo phpExecutableCombo;

	protected Button debugPrintOutputCheckbox;

	public AbstractPHPToolConfigurationBlock(IStatusChangeListener context, IProject project, Key[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}

	public Control createContents(Composite parent) {
		setShell(parent.getShell());

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		createVersionContent(composite);
		unpackPHPExecutable();

		createDebugContent(composite);

		Composite toolComposite = createToolContents(composite);
		toolComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return composite;
	}

	protected abstract Composite createToolContents(Composite parent);

	protected Composite createVersionContent(Composite parent) {
		Group composite = new Group(parent, SWT.RESIZE);
		composite.setText("PHP Executable");

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		phpExecutableNameLabel = new Label(composite, SWT.NONE);
		phpExecutableNameLabel.setText("PHP Executable:");

		phpExecutableCombo = new Combo(composite, SWT.READ_ONLY);

		phpExecutableCombo.setItems(preparePHPExecutableEntryList());
		phpExecutableCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String selectedValue = phpExecutableCombo.getText();
				setPhpExecutable(selectedValue);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		IPageChangedListener listener = new IPageChangedListener() {
			public void pageChanged(PageChangedEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						phpExecutableCombo.setItems(preparePHPExecutableEntryList());
					}
				});
			}
		};

		addLink(composite, "<a>PHP Executables...</a>", PHP_EXE_PAGE_ID, listener);

		return composite;
	}

	protected Composite createDebugContent(Composite parent) {
		Group composite = new Group(parent, SWT.RESIZE);
		composite.setText("Debug");

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		debugPrintOutputCheckbox = new Button(composite, SWT.CHECK);
		debugPrintOutputCheckbox.setText("print PHP output to console");
		debugPrintOutputCheckbox.setSelection(getBooleanValue(getDebugPrintOutputKey()));
		debugPrintOutputCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				boolean selection = debugPrintOutputCheckbox.getSelection();
				setValue(getDebugPrintOutputKey(), selection);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return composite;
	}

	protected void setPhpExecutable(String value) {
		PHPexes exes = PHPexes.getInstance();
		PHPexeItem[] items = exes.getAllItems();

		for (int i = 0; i < items.length; i++) {
			String name = items[i].getName();
			if (name.equals(value)) {
				phpExecutableCombo.setText(name);
				setValue(getPHPExecutableKey(), name);
				validateSettings(getPHPExecutableKey(), null, null);
				return;
			}
		}
	}

	protected String[] preparePHPExecutableEntryList() {
		PHPexes exes = PHPexes.getInstance();
		PHPexeItem[] items = exes.getAllItems();

		if (items == null || items.length == 0) {
			return new String[] { "None Defined" };
		}

		String[] entryList = new String[items.length];

		for (int i = 0; i < items.length; i++) {
			entryList[i] = items[i].getName();
		}

		return entryList;
	}

	protected void unpackPHPExecutable() {
		String value = getValue(getPHPExecutableKey());
		if (value != null)
			phpExecutableCombo.setText(value);
	}

	protected abstract Key getPHPExecutableKey();

	protected abstract Key getDebugPrintOutputKey();

	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	protected Link addLink(Composite parent, String label, final String propertyPageID) {
		return addLink(parent, label, propertyPageID, null);
	}

	protected Link addLink(Composite parent, String label, final String propertyPageID,
			final IPageChangedListener listener) {
		Link link = new Link(parent, SWT.NONE);
		link.setFont(parent.getFont());
		link.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));
		link.setText(label);
		link.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), propertyPageID, null,
						null);
				dialog.setBlockOnOpen(true);
				if (listener != null) {
					dialog.addPageChangedListener(listener);
				}
				dialog.open();
			}
		});

		return link;
	}

	protected void makeFontItalic(Control label) {
		Font font = label.getFont();
		FontData[] data = font.getFontData();
		if (data.length > 0) {
			data[0].setStyle(data[0].getStyle() | SWT.ITALIC);
		}
		label.setFont(new Font(font.getDevice(), data));
	}

	protected void clearProjectLauncherCache(QualifiedName propertyName) {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] projects = root.getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isOpen()) {
				try {
					IProjectNature nature = project.getNature(IPHPCoreConstants.PHPNatureID);
					if (nature != null) {
						project.setSessionProperty(propertyName, null);
					}
				} catch (CoreException e) {
					Logger.logException(e);
				}
			}
		}
	}

	protected Group createDialogFieldsWithInfoText(Composite folder, DialogField[] fields, String groupText,
			String[] infoTexts) {
		GridLayout fieldLayout = new GridLayout();
		fieldLayout.marginHeight = 5;
		fieldLayout.marginWidth = 0;
		fieldLayout.numColumns = 3;
		fieldLayout.marginLeft = 4;
		fieldLayout.marginRight = 4;

		Group fieldGroup = new Group(folder, SWT.NULL);
		fieldGroup.setText(groupText);
		fieldGroup.setLayout(fieldLayout);
		fieldGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (int i = 0; i < fields.length; i++) {
			fields[i].doFillIntoGrid(fieldGroup, 3);

			if (infoTexts != null && infoTexts.length > i && infoTexts[i] != null && !"".equals(infoTexts[i])) {
				Label ignorePatternInfoLabel = new Label(fieldGroup, SWT.NULL);
				ignorePatternInfoLabel.setText(infoTexts[i]);
				GridData infoData = new GridData(GridData.FILL_HORIZONTAL);
				infoData.horizontalSpan = 3;
				ignorePatternInfoLabel.setLayoutData(infoData);
				makeFontItalic(ignorePatternInfoLabel);
			}
		}

		return fieldGroup;
	}

	protected void createDialogFieldWithInfoLink(Composite folder, DialogField field, String groupText,
			String infoText, String propertyPageID) {
		GridLayout fieldLayout = new GridLayout();
		fieldLayout.marginHeight = 5;
		fieldLayout.marginWidth = 0;
		fieldLayout.numColumns = 3;
		fieldLayout.marginLeft = 4;
		fieldLayout.marginRight = 4;

		Group fieldGroup = new Group(folder, SWT.NULL);
		fieldGroup.setText(groupText);
		fieldGroup.setLayout(fieldLayout);
		fieldGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		field.doFillIntoGrid(fieldGroup, 3);

		// Label ignorePatternInfoLabel = new Label(ignorePatternGroup,
		// SWT.NULL);
		// ignorePatternInfoLabel.setText(infoText);
		Link link = addLink(fieldGroup, infoText, propertyPageID);
		GridData infoData = new GridData(GridData.FILL_HORIZONTAL);
		infoData.horizontalSpan = 3;
		link.setLayoutData(infoData);
		makeFontItalic(link);
	}

	protected void unpackPrefValue(StringDialogField field, Key key) {
		unpackPrefValue(field, key, null);
	}

	protected void unpackPrefValue(StringDialogField field, Key key, String defaultValue) {
		String value = getValue(key);
		if (value != null)
			field.setText(value);
		else if (defaultValue != null)
			field.setText(defaultValue);
	}
}