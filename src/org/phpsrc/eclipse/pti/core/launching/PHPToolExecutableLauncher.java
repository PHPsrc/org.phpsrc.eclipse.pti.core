/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.PHPDebugCoreMessages;
import org.eclipse.php.internal.debug.core.launching.PHPLaunch;
import org.eclipse.php.internal.debug.core.launching.PHPLaunchUtilities;
import org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil;
import org.eclipse.swt.widgets.Display;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.core.listener.IOutputListener;

@SuppressWarnings("restriction")
public class PHPToolExecutableLauncher {
	protected ListenerList outputListenerList = new ListenerList();

	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		return new PHPLaunch(configuration, mode, null);
	}

	public IProcess launch(ILaunchConfiguration configuration) throws CoreException {
		return launch(configuration, new NullProgressMonitor());
	}

	public IProcess launch(ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		return launch(configuration, getLaunch(configuration, ILaunchManager.RUN_MODE), monitor);
	}

	public IProcess launch(ILaunchConfiguration configuration, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		String phpExeString = configuration.getAttribute(
				IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, (String) null);
		String phpIniPath = configuration.getAttribute(IPHPDebugConstants.ATTR_INI_LOCATION,
				(String) null);
		String fileName = configuration.getAttribute(IPHPDebugConstants.ATTR_FILE_FULL_PATH,
				(String) null);

		if (monitor.isCanceled()) {
			return null;
		}

		if (fileName == null || fileName.equals("")) {
			displayErrorMessage("Please set a valid PHP file for this launch.");
			return null;
		}

		if (phpExeString == null || phpExeString.equals("")) {
			displayErrorMessage("Please set a valid PHP executable for this launch.");
			return null;
		}

		SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 10);

		// Locate the php.ini by using the attribute. If the attribute was null,
		// try to locate an php.ini that exists next to the executable.
		File phpIni = (phpIniPath != null && new File(phpIniPath).exists()) ? new File(phpIniPath)
				: PHPINIUtil.findPHPIni(phpExeString);
		launch.setAttribute(IDebugParametersKeys.PHP_INI_LOCATION, phpIni.getAbsolutePath());

		// resolve location
		IPath phpExe = new Path(phpExeString);

		String[] envp = DebugPlugin.getDefault().getLaunchManager().getEnvironment(configuration);
		File phpExeFile = new File(phpExeString);
		String phpIniLocation = launch.getAttribute(IDebugParametersKeys.PHP_INI_LOCATION);

		// Determine PHP configuration file location:
		String phpConfigDir = phpExeFile.getParent();
		if (phpIniLocation != null && !phpIniLocation.equals("")) {
			phpConfigDir = new File(phpIniLocation).getParent();
		}

		String[] args = PHPLaunchUtilities.getProgramArguments(launch.getLaunchConfiguration());

		String[] cmdLine = PHPLaunchUtilities.getCommandLine(launch.getLaunchConfiguration(),
				OperatingSystem.escapeShellFileArg(phpExeString), phpConfigDir,
				OperatingSystem.escapeShellFileArg(fileName), args);

		notifyOutputListener(cmdLine, ' ');

		// Set library search path:
		if (!OperatingSystem.WINDOWS) {
			StringBuffer buf = new StringBuffer();
			if (OperatingSystem.MAC) { //$NON-NLS-1$ //$NON-NLS-2$
				buf.append("DYLD_LIBRARY_PATH"); //$NON-NLS-1$
			} else {
				buf.append("LD_LIBRARY_PATH"); //$NON-NLS-1$
			}
			buf.append('=');
			buf.append(phpExeFile.getParent());
			String[] envpNew = new String[envp == null ? 1 : envp.length + 1];
			if (envp != null) {
				System.arraycopy(envp, 0, envpNew, 0, envp.length);
			}
			envpNew[envpNew.length - 1] = buf.toString();
			envp = envpNew;
		}

		if (monitor.isCanceled()) {
			return null;
		}

		File workingDir = new File(fileName).getParentFile();
		Process p = workingDir.exists() ? DebugPlugin.exec(cmdLine, workingDir, envp) : DebugPlugin
				.exec(cmdLine, null, envp);

		IProcess process = null;

		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		String programName = phpExe.lastSegment();
		String extension = phpExe.getFileExtension();

		if (extension != null) {
			programName = programName.substring(0, programName.length() - (extension.length() + 1));
		}

		programName = programName.toLowerCase();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, programName);

		if (p != null) {
			subMonitor = new SubProgressMonitor(monitor, 80); // 10+80 of 100;
			subMonitor
					.beginTask(
							MessageFormat.format(
									"start launch", new Object[] { configuration.getName() }), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			process = DebugPlugin.newProcess(launch, p, phpExe.toOSString(), processAttributes);
			if (process == null) {
				p.destroy();
				throw new CoreException(new Status(IStatus.ERROR, PHPToolCorePlugin.PLUGIN_ID, 0,
						null, null));
			}

			IStreamListener outputListener = new IStreamListener() {
				public void streamAppended(String text, IStreamMonitor monitor) {
					notifyOutputListener(text);
				}
			};

			process.getStreamsProxy().getErrorStreamMonitor().addListener(outputListener);
			process.getStreamsProxy().getOutputStreamMonitor().addListener(outputListener);
			subMonitor.done();
		}
		process.setAttribute(IProcess.ATTR_CMDLINE, fileName);

		if (!CommonTab.isLaunchInBackground(configuration)) {
			// wait for process to exit
			while (!process.isTerminated()) {
				try {
					if (monitor.isCanceled()) {
						process.terminate();
						break;
					}
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}

			// refresh resources
			subMonitor = new SubProgressMonitor(monitor, 10); // 10+80+10 of
			// 100;
			RefreshTab.refreshResources(configuration, subMonitor);
		}

		return process;
	}

	private void displayErrorMessage(final String message) {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(display.getActiveShell(),
						PHPDebugCoreMessages.Debugger_LaunchError_title, message);
			}
		});
	}

	protected void notifyOutputListener(String output) {
		for (Object listener : outputListenerList.getListeners()) {
			((IOutputListener) listener).handleOutput(output);
		}
	}

	protected void notifyOutputListener(String[] output, char glue) {
		StringBuffer str = new StringBuffer();
		for (String line : output) {
			if (str.length() > 0)
				str.append(glue);
			str.append(line);
		}
		notifyOutputListener(str.toString());
	}

	public void addOutputListener(IOutputListener listener) {
		outputListenerList.add(listener);
	}

	public void removeOutputListener(IOutputListener listener) {
		outputListenerList.remove(listener);
	}
}
