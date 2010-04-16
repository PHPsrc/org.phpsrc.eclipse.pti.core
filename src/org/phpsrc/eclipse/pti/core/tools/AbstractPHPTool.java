/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.tools;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.phpsrc.eclipse.pti.core.listener.IResultListener;

public abstract class AbstractPHPTool {
	protected ListenerList resultListenerList = new ListenerList();

	protected void notifyResultListener(final Object result) {
		for (Object listener : resultListenerList.getListeners()) {
			((IResultListener) listener).handleResult(result);
		}
	}

	public void addResultListener(IResultListener listener) {
		resultListenerList.add(listener);
	}

	public void removeResultListener(IResultListener listener) {
		resultListenerList.remove(listener);
	}

	protected static PHPexeItem getPHPExecutable(String phpExecutableId) {
		PHPexeItem[] items = PHPexes.getInstance().getAllItems();
		for (PHPexeItem item : items) {
			if (item.getName().equals(phpExecutableId))
				return item;
		}

		return null;
	}

	protected PHPexeItem getDefaultPhpExecutable() {
		PHPexeItem defaultPhpExec = null;
		for (PHPexeItem phpExec : PHPexes.getInstance().getAllItems()) {
			if (phpExec.isDefault()) {
				defaultPhpExec = phpExec;
			}
		}
		return defaultPhpExec;
	}

	protected File createTempDir(String name) {
		File tempDir = new File(System.getProperty("java.io.tmpdir"), name); //$NON-NLS-2$
		if (!tempDir.exists()) {
			tempDir.mkdir();
			tempDir.deleteOnExit();
		}
		return tempDir;
	}

	protected File createTempFile(File tempDir, String name) throws IOException {
		File tempFile = new File(tempDir, name);
		if (tempFile.exists())
			tempFile.delete();
		tempFile.createNewFile();
		tempFile.deleteOnExit();
		return tempFile;
	}
}
