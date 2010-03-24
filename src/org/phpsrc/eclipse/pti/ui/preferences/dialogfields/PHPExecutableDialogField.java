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

package org.phpsrc.eclipse.pti.ui.preferences.dialogfields;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class PHPExecutableDialogField extends DialogField {

	private static final String PHP_EXE_PAGE_ID = "org.eclipse.php.debug.ui.preferencesphps.PHPsPreferencePage"; //$NON-NLS-1$

	private String phpExecutable;
	protected Combo phpExecutableControl;
	private ModifyListener fModifyListener;

	public PHPExecutableDialogField() {
		super();
		phpExecutable = ""; //$NON-NLS-1$
	}

	/*
	 * @see DialogField#doFillIntoGrid
	 */
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);

		Label label = getLabelControl(parent);
		label.setLayoutData(gridDataForLabel(1));
		Combo combo = getTextControl(parent);
		combo.setLayoutData(gridDataForText(nColumns - 2));

		Link link = new Link(parent, SWT.NONE);
		link.setFont(parent.getFont());
		link.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));
		link.setText("<a>PHP Executables...</a>");
		link.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getCurrent()
						.getActiveShell(), PHP_EXE_PAGE_ID, null, null);
				dialog.setBlockOnOpen(true);
				dialog.addPageChangedListener(new IPageChangedListener() {
					public void pageChanged(PageChangedEvent event) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
							}
						});
					}
				});
				dialog.open();
			}
		});

		return new Control[] { label, combo, link };
	}

	/*
	 * @see DialogField#getNumberOfControls
	 */
	public int getNumberOfControls() {
		return 3;
	}

	public static GridData gridDataForText(int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		return gd;
	}

	/*
	 * @see DialogField#setFocus
	 */
	public boolean setFocus() {
		if (isOkToUse(phpExecutableControl)) {
			phpExecutableControl.setFocus();
		}
		return true;
	}

	/**
	 * Creates or returns the created text control.
	 * 
	 * @param parent
	 *            The parent composite or <code>null</code> when the widget has
	 *            already been created.
	 */
	public Combo getTextControl(Composite parent) {
		if (phpExecutableControl == null) {
			assertCompositeNotNull(parent);
			fModifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					doModifyText(e);
				}
			};

			phpExecutableControl = new Combo(parent, SWT.READ_ONLY);
			phpExecutableControl.setItems(preparePHPExecutableEntryList());
			setPhpExecutable(phpExecutable);
			phpExecutableControl.setFont(parent.getFont());
			phpExecutableControl.addModifyListener(fModifyListener);
			phpExecutableControl.setEnabled(isEnabled());

		}
		return phpExecutableControl;
	}

	private void doModifyText(ModifyEvent e) {
		if (isOkToUse(phpExecutableControl)) {
			phpExecutable = phpExecutableControl.getText();
		}
		dialogFieldChanged();
	}

	/*
	 * @see DialogField#updateEnableState
	 */
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(phpExecutableControl)) {
			phpExecutableControl.setEnabled(isEnabled());
		}
	}

	/**
	 * Gets the text. Can not be <code>null</code>
	 */
	public String getText() {
		return phpExecutable;
	}

	/**
	 * Sets the text. Triggers a dialog-changed event.
	 */
	public void setText(String text) {
		if (isOkToUse(phpExecutableControl)) {
			setPhpExecutable(text);
		} else {
			dialogFieldChanged();
		}
	}

	/**
	 * Sets the text without triggering a dialog-changed event.
	 */
	public void setTextWithoutUpdate(String text) {
		if (isOkToUse(phpExecutableControl)) {
			phpExecutableControl.removeModifyListener(fModifyListener);
			setPhpExecutable(text);
			phpExecutableControl.addModifyListener(fModifyListener);
		}
	}

	public void refresh() {
		super.refresh();
		if (isOkToUse(phpExecutableControl)) {
			setTextWithoutUpdate(phpExecutable);
		}
	}

	private void setPhpExecutable(String value) {
		PHPexes exes = PHPexes.getInstance();
		PHPexeItem[] items = exes.getAllItems();

		for (int i = 0; i < items.length; i++) {
			String name = items[i].getName();
			if (name.equals(value)) {
				phpExecutable = name;
				phpExecutableControl.setText(name);
				return;
			}
		}
	}

	private String[] preparePHPExecutableEntryList() {
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
}
