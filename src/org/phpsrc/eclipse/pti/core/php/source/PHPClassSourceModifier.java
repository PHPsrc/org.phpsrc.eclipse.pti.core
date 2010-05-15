/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.php.source;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

public class PHPClassSourceModifier {
	protected String className;
	protected String superClassName = null;
	protected String sourceStart;
	protected HashMap<String, String> methodSources;
	protected StringBuffer newMethodsSource = new StringBuffer();
	protected StringBuffer sourceEnd = new StringBuffer();

	protected ArrayList<String> fMethods = new ArrayList<String>();

	public PHPClassSourceModifier(ISourceModule module, String className) throws InvalidClassException, ModelException {
		Assert.isNotNull(module);
		Assert.isTrue(module.exists());
		Assert.isNotNull(className);

		boolean found = false;
		for (IType type : module.getAllTypes()) {
			if (type.getElementName().equals(className)) {
				this.className = className;

				String[] superClasses = type.getSuperClasses();
				if (superClasses != null && superClasses.length > 0)
					this.superClassName = superClasses[0];

				for (IMethod method : type.getMethods()) {
					fMethods.add(method.getElementName());
				}
				parseSourceCode(module, type);
				found = true;
				break;
			}
		}

		if (!found)
			throw new InvalidClassException("Class " + className + " not found");
	}

	private void parseSourceCode(ISourceModule module, IType type) throws ModelException {
		String fileSource = module.getSource();

		StringBuffer buffer = new StringBuffer();

		ISourceRange range = type.getSourceRange();
		if (range.getOffset() > 0)
			buffer.append(fileSource.substring(0, range.getOffset()).trim() + "\n");

		String classSource = type.getSource();
		buffer.append(classSource.substring(0, classSource.lastIndexOf('}')));

		sourceEnd.append(classSource.substring(classSource.lastIndexOf('}')));

		int offsetEnd = range.getOffset() + range.getLength();
		if (offsetEnd + 1 < fileSource.length())
			sourceEnd.append(fileSource.substring(offsetEnd));

		sourceStart = buffer.toString();
	}

	public boolean hasMethod(IMethod method) {
		Assert.isNotNull(method);
		return fMethods.contains(method.getElementName());
	}

	public void addMethod(IMethod method) throws ModelException {
		if (!hasMethod(method)) {
			fMethods.add(method.getElementName());
			newMethodsSource.append("\n    " + method.getSource() + "\n");
		}
	}

	public String getSource() throws ModelException {
		return sourceStart + newMethodsSource.toString() + sourceEnd;
	}

	public void setSuperClass(String superClass) {
		sourceStart = sourceStart.replaceFirst("(extends[ \\n\\r]+)" + this.superClassName, "$1" + superClass);
		this.superClassName = superClass;
	}
}
