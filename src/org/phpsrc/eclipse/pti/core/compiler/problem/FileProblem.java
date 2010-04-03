/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.compiler.problem;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.compiler.problem.DefaultProblem;

public class FileProblem extends DefaultProblem {

	protected IFile originatingFile;

	public FileProblem(IFile originatingFile, String message, int id, String[] stringArguments, int severity,
			int startPosition, int endPosition, int line, int column) {
		super(originatingFile.getFullPath().toOSString(), message, id, stringArguments, severity, startPosition,
				endPosition, line, column);

		this.originatingFile = originatingFile;
	}

	public FileProblem(IFile originatingFile, String message, int id, String[] stringArguments, int severity,
			int startPosition, int endPosition, int line) {
		super(originatingFile.getFullPath().toOSString(), message, id, stringArguments, severity, startPosition,
				endPosition, line);

		this.originatingFile = originatingFile;
	}

	public void setOriginatingFile(IFile originatingFile) {
		this.originatingFile = originatingFile;
		setOriginatingFileName(originatingFile.getFullPath().toOSString());
	}

	public IFile getOriginatingFile() {
		return this.originatingFile;
	}
}
