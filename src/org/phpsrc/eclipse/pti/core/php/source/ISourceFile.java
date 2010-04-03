/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.php.source;

import org.eclipse.core.resources.IFile;

public interface ISourceFile {
	public int lineStart(int lineNumber) throws IndexOutOfBoundsException;

	public int lineEnd(int lineNumber) throws IndexOutOfBoundsException;

	public int lineStartTabCount(int lineNumber) throws IndexOutOfBoundsException;

	public IFile getFile();

	public int getNumberOfLines();
}
