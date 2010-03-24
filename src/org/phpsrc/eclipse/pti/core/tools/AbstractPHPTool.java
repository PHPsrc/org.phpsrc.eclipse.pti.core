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
