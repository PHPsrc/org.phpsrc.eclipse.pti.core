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

package org.phpsrc.eclipse.pti.core.search.ui.dialogs;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.core.search.PHPSearchEngine;
import org.phpsrc.eclipse.pti.core.search.PHPSearchMatch;

public class FilteredPHPClassSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String DIALOG_SETTINGS = "org.phpsrc.eclipse.pti.core.search.ui.dialogs.FilteredPHPClassSelectionDialog";

	private static final Image IMAGE_CLASS = DLTKPluginImages.DESC_OBJS_CLASS.createImage();

	private class DetailLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if (!(element instanceof PHPSearchMatch))
				return null;
			return ((PHPSearchMatch) element).toString();
		}

		public Image getImage(Object element) {
			if (!(element instanceof PHPSearchMatch))
				return null;
			return IMAGE_CLASS;
		}

	}

	private class ListLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if (!(element instanceof PHPSearchMatch))
				return null;
			return ((PHPSearchMatch) element).getElement().getElementName();
		}

		public Image getImage(Object element) {
			if (!(element instanceof PHPSearchMatch))
				return null;
			return DLTKPluginImages.DESC_OBJS_CLASS.createImage();
		}
	}

	public FilteredPHPClassSelectionDialog(Shell shell, boolean multi) {
		super(shell, multi);
		setTitle("Select PHP Class");
		setDetailsLabelProvider(new DetailLabelProvider());
		setListLabelProvider(new ListLabelProvider());
	}

	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	protected ItemsFilter createFilter() {
		return new ItemsFilter() {

			public boolean isConsistentItem(Object item) {
				if (!(item instanceof PHPSearchMatch))
					return false;

				return true;
			}

			public boolean matchItem(Object item) {
				if (!(item instanceof PHPSearchMatch))
					return false;

				return matches(((PHPSearchMatch) item).getElement().getElementName());
			}
		};
	}

	protected void fillContentProvider(AbstractContentProvider provider, ItemsFilter filter, IProgressMonitor monitor)
			throws CoreException {
		int matchRule = SearchPattern.R_PREFIX_MATCH;
		SearchMatch[] matches = PHPSearchEngine.findClass(filter.getPattern(), matchRule);
		for (SearchMatch match : matches) {
			provider.add(new PHPSearchMatch((SourceType) match.getElement(), match.getResource()), filter);
		}
	}

	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = PHPToolCorePlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);

		if (settings == null) {
			settings = PHPToolCorePlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}

		return settings;
	}

	public String getElementName(Object element) {
		return ((PHPSearchMatch) element).getElement().getElementName();
	}

	protected Comparator<Object> getItemsComparator() {
		return new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Collator collator = Collator.getInstance();
				return collator.compare(o1.toString(), o2.toString());
			}
		};
	}

	protected IStatus validateItem(Object item) {
		return new Status(IStatus.OK, PHPToolCorePlugin.PLUGIN_ID, IStatus.OK, "", null);
	}

}
