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

package org.phpsrc.eclipse.pti.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.framework.Bundle;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;

public class Logger {
	private static final String PLUGIN_ID = PHPToolCorePlugin.PLUGIN_ID; //$NON-NLS-1$

	private static final String CONSOLE_NAME = "PHP Tool Output";

	public static final int OK = IStatus.OK; // 0
	public static final int INFO = IStatus.INFO; // 1
	public static final int WARNING = IStatus.WARNING; // 2
	public static final int ERROR = IStatus.ERROR; // 4

	protected static void _log(int level, String message, Throwable exception) {
		int severity = IStatus.OK;
		switch (level) {
		case INFO:
			severity = IStatus.INFO;
			break;
		case WARNING:
			severity = IStatus.WARNING;
			break;
		case ERROR:
			severity = IStatus.ERROR;
		}

		message = (message != null) ? message : "null"; //$NON-NLS-1$
		Status statusObj = new Status(severity, PLUGIN_ID, severity, message, exception);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (bundle != null)
			Platform.getLog(bundle).log(statusObj);
	}

	protected static void _trace(String message, Throwable exception) {
		message = (message != null) ? message : "null"; //$NON-NLS-1$
		Status statusObj = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, exception);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (bundle != null)
			Platform.getLog(bundle).log(statusObj);
	}

	protected static void _logToConsole(String output) {
		MessageConsole myConsole = findConsole(CONSOLE_NAME);
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(output);
	}

	public static void log(int level, String message) {
		_log(level, message, null);
	}

	public static void log(int level, String message, Throwable exception) {
		_log(level, message, exception);
	}

	public static void logException(Throwable exception) {
		_log(ERROR, exception.getMessage(), exception);
	}

	public static void logException(String message, Throwable exception) {
		_log(ERROR, message, exception);
	}

	public static void traceException(String message, Throwable exception) {
		_trace(message, exception);
	}

	public static void traceException(Throwable exception) {
		_trace(exception.getMessage(), exception);
	}

	public static void trace(String message) {
		_trace(message, null);
	}

	public static void logToConsole(String message) {
		_logToConsole(message);
	}

	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
