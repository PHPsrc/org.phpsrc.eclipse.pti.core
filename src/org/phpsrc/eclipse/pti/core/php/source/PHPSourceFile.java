/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.php.source;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;

public class PHPSourceFile implements ISourceFile {

	private final IFile file;
	private ArrayList<Integer> lineStarts;
	private ArrayList<Integer> lineEnds;
	private ArrayList<Integer> lineStartTabCount;
	private int linesCount;
	/**
	 * @since 1.4.0
	 */
	private ISourceModule module;

	public PHPSourceFile(IFile file) throws CoreException, IOException {
		Assert.isNotNull(file);
		this.file = file;
		determineLinePositions(file);
		module = PHPToolkitUtil.getSourceModule(file);
	}

	private void determineLinePositions(IFile file) throws CoreException, IOException {
		lineStarts = new ArrayList<Integer>();
		lineEnds = new ArrayList<Integer>();
		lineStartTabCount = new ArrayList<Integer>();

		InputStreamReader isr;
		isr = new InputStreamReader(file.getContents());

		int last = -1;
		int i = 0;
		int c;
		boolean countTabs = true;
		int tabCount = 0;
		while ((c = isr.read()) != -1) {
			if ((char) c == '\n') {
				lineStarts.add(new Integer(last + 1));
				lineEnds.add(new Integer(i));
				lineStartTabCount.add(new Integer(tabCount));
				++linesCount;
				last = i;
				countTabs = true;
				tabCount = 0;
			} else if (countTabs && (char) c == '\t') {
				++tabCount;
			} else if ((char) c != ' ') {
				countTabs = false;
			}
			i++;
		}

		lineStarts.add(new Integer(last + 1));
		lineEnds.add(new Integer(i));
		lineStartTabCount.add(new Integer(tabCount));
		++linesCount;
	}

	public int lineStart(int lineNumber) throws IndexOutOfBoundsException {
		return lineStarts.get(lineNumber - 1);
	}

	public int lineEnd(int lineNumber) throws IndexOutOfBoundsException {
		return lineEnds.get(lineNumber - 1);
	}

	public int lineStartTabCount(int lineNumber) throws IndexOutOfBoundsException {
		return lineStartTabCount.get(lineNumber - 1);
	}

	public IFile getFile() {
		return file;
	}

	public int getNumberOfLines() {
		return linesCount;
	}

	/**
	 * @since 1.4.0
	 */
	public ISourceModule getSourceModule() {
		return module;
	}

	/**
	 * @since 1.4.0
	 */
	public int findLineNumberForOffset(int offset) throws IndexOutOfBoundsException {
		if (offset < 0)
			throw new IndexOutOfBoundsException();
		if (offset == 0)
			return 1;

		int count = lineEnds.size();
		for (int i = 0; i < count; ++i) {
			int end = lineEnds.get(i);
			if (end > offset)
				return i + 1;
		}

		throw new IndexOutOfBoundsException();
	}
}
