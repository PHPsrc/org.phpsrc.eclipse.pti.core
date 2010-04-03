/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.wizards.fields;

import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.contentassist.ContentAssistHandler;

/**
 * Dialog field containing a label and a text control.
 */
public class ComboStringDialogField extends StringDialogField {

	protected String fSelection;
	protected String[] fSelectionItems;
	protected Combo fComboControl;

	public ComboStringDialogField() {
		super();
		fSelection = ""; //$NON-NLS-1$
		fSelectionItems = new String[0];
	}

	// ------- layout helpers

	/*
	 * @see DialogField#doFillIntoGrid
	 */
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);

		Label label = getLabelControl(parent);
		label.setLayoutData(gridDataForLabel(1));
		Combo combo = getComboControl(parent);
		Text text = getTextControl(parent);
		text.setLayoutData(gridDataForText(nColumns - 2));

		refresh();

		return new Control[] { label, combo, text };
	}

	/*
	 * @see DialogField#getNumberOfControls
	 */
	public int getNumberOfControls() {
		return 3;
	}

	// ------- ui creation

	/**
	 * Creates or returns the created text control.
	 * 
	 * @param parent
	 *            The parent composite or <code>null</code> when the widget has
	 *            already been created.
	 */
	public Text getTextControl(Composite parent) {
		if (fTextControl == null) {
			assertCompositeNotNull(parent);
			fTextControl = new Text(parent, SWT.SINGLE | SWT.BORDER);
			// moved up due to 1GEUNW2
			fTextControl.setText(fText);
			fTextControl.setFont(parent.getFont());
			fTextControl.addModifyListener(fModifyListener);

			fTextControl.setEnabled(isEnabled());
			if (fContentAssistProcessor != null) {
				ContentAssistHandler.createHandlerForText(fTextControl,
						createPHPContentAssistant(fContentAssistProcessor));
			}
		}
		return fTextControl;
	}

	public Combo getComboControl(Composite parent) {
		if (fComboControl == null) {
			assertCompositeNotNull(parent);
			fModifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					doModifyText(e);
				}
			};

			fComboControl = new Combo(parent, SWT.BORDER);
			// moved up due to 1GEUNW2
			fComboControl.setText(fSelection);
			fComboControl.setFont(parent.getFont());
			fComboControl.addModifyListener(fModifyListener);
			fComboControl.setEnabled(isEnabled());
		}
		return fComboControl;
	}

	// ------ enable / disable management

	/*
	 * @see DialogField#updateEnableState
	 */
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(fComboControl)) {
			fComboControl.setEnabled(isEnabled());
		}
	}

	protected void doModifyText(ModifyEvent e) {
		if (isOkToUse(fTextControl)) {
			fText = fTextControl.getText();
			fSelection = fComboControl.getText();
		}
		dialogFieldChanged();
	}

	public String getSelection() {
		return fSelection;
	}

	public void setSelection(String selection) {
		fSelection = selection;
		if (isOkToUse(fComboControl)) {
			fComboControl.setText(selection != null ? selection : "");
		} else {
			dialogFieldChanged();
		}
	}

	public void setSelectionItems(String[] items) {
		fSelectionItems = items;
		if (isOkToUse(fComboControl)) {
			fComboControl.setItems(items);
		} else {
			dialogFieldChanged();
		}
	}

	public void setTextWithoutUpdate(String text) {
		fText = text;
		if (isOkToUse(fTextControl)) {
			fTextControl.removeModifyListener(fModifyListener);
			fTextControl.setText(text);
			fTextControl.addModifyListener(fModifyListener);
		}
	}

	public void setText(String text) {
		fText = text;
		if (isOkToUse(fTextControl)) {
			fTextControl.setText(text);
		} else {
			dialogFieldChanged();
		}
	}

	/**
	 * Sets the text without triggering a dialog-changed event.
	 */
	public void setSelectionWithoutUpdate(String selection) {
		fSelection = selection;
		if (isOkToUse(fComboControl)) {
			fComboControl.removeModifyListener(fModifyListener);
			fComboControl.setText(selection);
			fComboControl.addModifyListener(fModifyListener);
		}
	}

	public void setSelectionItemsWithoutUpdate(String[] items) {
		fSelectionItems = items;
		if (isOkToUse(fComboControl)) {
			fComboControl.removeModifyListener(fModifyListener);
			fComboControl.setItems(items);
			fComboControl.addModifyListener(fModifyListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField#refresh()
	 */
	public void refresh() {
		super.refresh();
		if (isOkToUse(fComboControl)) {
			setSelectionItemsWithoutUpdate(fSelectionItems);
			setSelectionWithoutUpdate(fSelection);
		}
	}
}
