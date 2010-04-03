/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.tools;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.source.ISourceFile;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;
import org.phpsrc.eclipse.pti.ui.Logger;

public abstract class AbstractPHPToolParser extends AbstractPHPTool {
	public IProblem[] parse(IFile file) throws CoreException, IOException {
		return parseOutput(new PHPSourceFile(file), launchFile(file));
	}

	protected String launchFile(IFile file) {

		String output = null;
		try {
			PHPToolLauncher launcher = getPHPToolLauncher(file.getProject());
			output = launcher.launch(file);
		} catch (Exception e) {
			Logger.logException(e);
		}

		if (output == null)
			return "";
		else
			return output;
	}

	protected abstract PHPToolLauncher getPHPToolLauncher(IProject project);

	protected abstract IProblem[] parseOutput(ISourceFile file, String output);
}
