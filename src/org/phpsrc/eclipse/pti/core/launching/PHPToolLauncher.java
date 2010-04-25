/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.launching;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.ui.PHPDebugUIPlugin;
import org.eclipse.swt.widgets.Display;
import org.phpsrc.eclipse.pti.core.IPHPCoreConstants;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.core.listener.IOutputListener;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileModifier;
import org.phpsrc.eclipse.pti.ui.Logger;

@SuppressWarnings("restriction")
public class PHPToolLauncher {

	public final static String COMMANDLINE_PLACEHOLDER_FILE = "%file%"; //$NON-NLS-1$
	public final static String COMMANDLINE_PLACEHOLDER_FOLDER = "%folder%"; //$NON-NLS-1$

	private final QualifiedName tool;
	private final PHPexeItem phpExe;
	private final IPath phpScript;
	private final INIFileEntry[] iniEntries;
	private String commandLineArgs;
	private boolean printOutput = false;
	private Hashtable<String, String> attributes = new Hashtable<String, String>();

	public PHPToolLauncher(QualifiedName tool, PHPexeItem phpExe, IPath phpScript) {
		this(tool, phpExe, phpScript, "");
	}

	public PHPToolLauncher(QualifiedName tool, PHPexeItem phpExe, IPath phpScript, String commandLineArgs) {
		this(tool, phpExe, phpScript, commandLineArgs, new INIFileEntry[0]);
	}

	public PHPToolLauncher(QualifiedName tool, PHPexeItem phpExe, IPath phpScript, INIFileEntry[] iniEntries) {
		this(tool, phpExe, phpScript, "", iniEntries);
	}

	/**
	 * 
	 * @param phpExe
	 * @param phpScript
	 * @param commandLineArgs
	 * @param iniEntries
	 * @throws NullPointerException
	 */
	public PHPToolLauncher(QualifiedName tool, PHPexeItem phpExe, IPath phpScript, String commandLineArgs,
			INIFileEntry[] iniEntries) {
		this.tool = tool;
		this.phpExe = phpExe;
		this.phpScript = phpScript;
		this.commandLineArgs = commandLineArgs;
		this.iniEntries = iniEntries;
		Assert.isNotNull(tool);
	}

	public String launch(IFile file) {
		String phpFileLocation = null;
		IPath location = file.getLocation();
		if (location != null) {
			phpFileLocation = location.toOSString();
		} else {
			phpFileLocation = file.getFullPath().toString();
		}

		return launch(file.getProject(), phpFileLocation);
	}

	public String launch(IProject project) {
		return launch(project, "");
	}

	protected String launch(IProject project, String phpFileLocation) {
		if (phpExe == null)
			return null;

		try {
			if (phpFileLocation == null) {
				// Could not find target to launch
				throw new CoreException(new Status(IStatus.ERROR, PHPDebugUIPlugin.ID, IStatus.OK,
						"Launch target not found", null));
			}

			ILaunchConfiguration config = findLaunchConfiguration(project, phpScript.toOSString(), phpScript
					.toOSString(), phpExe, ILaunchManager.RUN_MODE, getPHPExeLaunchConfigType());

			if (config != null) {
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();

				String arguments = commandLineArgs.replace(COMMANDLINE_PLACEHOLDER_FILE, OperatingSystem
						.escapeShellFileArg(phpFileLocation));

				int lastPos = phpFileLocation.lastIndexOf("\\") != -1 ? phpFileLocation.lastIndexOf("\\")
						: phpFileLocation.lastIndexOf("/");
				String folderPath = lastPos != -1 ? OperatingSystem.escapeShellFileArg(phpFileLocation.substring(0,
						lastPos)) : "";
				arguments = arguments.replace(COMMANDLINE_PLACEHOLDER_FOLDER, folderPath);

				wc.setAttribute(IDebugParametersKeys.EXE_CONFIG_PROGRAM_ARGUMENTS, arguments);
				config = wc.doSave();

				PHPToolExecutableLauncher php = new PHPToolExecutableLauncher();

				if (printOutput) {
					php.addOutputListener(new IOutputListener() {
						public void handleOutput(String output) {
							Logger.logToConsole(output, true);
						}
					});
				}

				IProcess process = php.launch(config);
				IStreamsProxy proxy = process.getStreamsProxy();
				String output = proxy.getOutputStreamMonitor().getContents();

				if (printOutput)
					Logger.logToConsole(output, true);

				return output;
			} else {
				// Could not find launch configuration
				throw new CoreException(new Status(IStatus.ERROR, PHPDebugUIPlugin.ID, IStatus.OK,
						"Launch configuration could not be created for the selected file.", null));
			}
		} catch (CoreException ce) {
			if (printOutput)
				Logger.logToConsole(ce.getMessage());

			final IStatus stat = ce.getStatus();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(PHPToolCorePlugin.getActiveWorkbenchShell(), "Error",
							"Unable to execute file", stat);
				}
			});
		}

		return null;
	}

	protected static ILaunchConfigurationType getPHPExeLaunchConfigType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType(IPHPCoreConstants.LaunchType);
	}

	/**
	 * Locate a configuration to relaunch for the given type. If one cannot be
	 * found, create one.
	 * 
	 * @return a re-useable config or <code>null</code> if none
	 */
	protected ILaunchConfiguration findLaunchConfiguration(IProject phpProject, String phpPathString,
			String phpFileFullLocation, PHPexeItem defaultEXE, String mode, ILaunchConfigurationType configType) {

		ILaunchConfiguration config = null;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(
					configType);

			int numConfigs = configs == null ? 0 : configs.length;

			for (int i = 0; i < numConfigs; i++) {
				String fileName = configs[i].getAttribute(IPHPDebugConstants.ATTR_FILE, (String) null);
				String exeName = configs[i].getAttribute(IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, (String) null);
				boolean isPti = configs[i].getAttribute(PHPToolCorePlugin.PLUGIN_ID, false);

				if (isPti && phpPathString.equals(fileName) && defaultEXE.getExecutable().toString().equals(exeName)) {
					String iniLocation = configs[i].getAttribute(IPHPDebugConstants.ATTR_INI_LOCATION, (String) null);
					String toolName = configs[i].getAttribute(IPHPToolLaunchConstants.ATTR_PHP_TOOL_QUALIFIED_NAME,
							(String) null);
					if (iniLocation == null || !(new File(iniLocation).exists()) || toolName == null
							|| !toolName.equals(tool.toString())) {
						configs[i].delete();
					} else {
						config = configs[i];
					}
					break;
				}
			}

			if (config == null) {
				String iniFile = null;
				File PHPINIFile = createCustomPHPINIFile(config, defaultEXE, iniEntries);
				if (PHPINIFile != null)
					iniFile = PHPINIFile.getAbsolutePath().toString();

				config = createConfiguration(phpProject, phpPathString, phpFileFullLocation, defaultEXE, configType,
						iniFile);
			}
		} catch (CoreException ce) {
			Logger.logException(ce);
		}

		return config;
	}

	public static void deleteAllConfigs(String phpPathString) {
		if (phpPathString == null)
			return;

		ILaunchConfigurationType configType = getPHPExeLaunchConfigType();

		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(
					configType);

			int numConfigs = configs == null ? 0 : configs.length;

			for (int i = 0; i < numConfigs; i++) {
				String fileName = configs[i].getAttribute(IPHPDebugConstants.ATTR_FILE, (String) null);
				boolean isPti = configs[i].getAttribute(PHPToolCorePlugin.PLUGIN_ID, false);

				if (isPti && phpPathString.equals(fileName)) {
					configs[i].delete();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected File createCustomPHPINIFile(ILaunchConfiguration config, PHPexeItem defaultEXE, INIFileEntry[] fileEntries) {
		File oldPHPINIFile = PHPINIUtil.findPHPIni(defaultEXE.getExecutable().toString());

		if (oldPHPINIFile == null) {
			try {
				String iniLocation = config != null ? config.getAttribute(IPHPDebugConstants.ATTR_INI_LOCATION,
						(String) null) : null;
				oldPHPINIFile = iniLocation != null ? new File(iniLocation) : null;
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}

		File tmpPHPINIFile;
		if (oldPHPINIFile != null)
			tmpPHPINIFile = PHPINIUtil.createTemporaryPHPINIFile(oldPHPINIFile);
		else
			tmpPHPINIFile = PHPINIUtil.createTemporaryPHPINIFile();

		if (fileEntries != null && fileEntries.length > 0) {
			try {
				INIFileModifier modifier = new INIFileModifier(tmpPHPINIFile);
				for (INIFileEntry entry : fileEntries) {
					String newValue = entry.getValue();
					if (entry.isAdditional()) {
						String oldValue = modifier.getEntry(entry.getSection(), entry.getName());
						if (oldValue != null)
							newValue = oldValue + ";" + newValue;
					}

					modifier.addEntry(entry.getSection(), entry.getName(), newValue, true, null);
				}
				modifier.close();
			} catch (IOException e) {
				Logger.logException(e);
			}
		}

		return tmpPHPINIFile;
	}

	/**
	 * Create & return a new configuration
	 */
	protected ILaunchConfiguration createConfiguration(IProject phpProject, String phpPathString,
			String phpFileFullLocation, PHPexeItem defaultEXE, ILaunchConfigurationType configType, String iniPath)
			throws CoreException {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getNewConfigurationName(phpPathString));

		// Set the delegate class according to selected executable.
		wc.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, defaultEXE.getDebuggerID());
		AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry.getDebuggerConfiguration(defaultEXE
				.getDebuggerID());
		wc.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, debuggerConfiguration
				.getScriptLaunchDelegateClass());
		wc.setAttribute(IPHPDebugConstants.ATTR_FILE, phpPathString);
		wc.setAttribute(IPHPDebugConstants.ATTR_FILE_FULL_PATH, phpFileFullLocation);
		wc.setAttribute(IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, defaultEXE.getExecutable().getAbsolutePath()
				.toString());
		if (iniPath == null)
			iniPath = defaultEXE.getINILocation() != null ? defaultEXE.getINILocation().toString() : null;
		wc.setAttribute(IPHPDebugConstants.ATTR_INI_LOCATION, iniPath);
		wc.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, false);
		wc.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, false);
		wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, true);
		wc.setAttribute(PHPToolCorePlugin.PLUGIN_ID, true);
		wc.setAttribute(IPHPToolLaunchConstants.ATTR_PHP_TOOL_QUALIFIED_NAME, tool.toString());

		config = wc.doSave();

		return config;
	}

	/**
	 * Returns a name for a newly created launch configuration according to the
	 * given file name. In case the name generation fails, return the
	 * "New_configuration" string.
	 * 
	 * @param fileName
	 *            The original file name that this shortcut shoul execute.
	 * @return The new configuration name, or "New_configuration" in case it
	 *         fails for some reason.
	 */
	protected static String getNewConfigurationName(String fileName) {
		String configurationName = "New_configuration";
		try {
			IPath path = Path.fromOSString(fileName);
			String fileExtention = path.getFileExtension();
			String lastSegment = path.lastSegment();
			if (lastSegment != null) {
				if (fileExtention != null) {
					lastSegment = lastSegment.replaceFirst("." + fileExtention, "");
				}
				configurationName = lastSegment;
			}
		} catch (Exception e) {
			Logger.log(Logger.WARNING, "Could not generate configuration name for " + fileName
					+ ".\nThe default name will be used.", e);
		}

		configurationName = "pti_" + configurationName;

		return DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(configurationName);
	}

	public void setCommandLineArgs(String commandLineArgs) {
		this.commandLineArgs = commandLineArgs;
	}

	public void setPrintOuput(boolean printOutput) {
		this.printOutput = printOutput;
	}

	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}
}
