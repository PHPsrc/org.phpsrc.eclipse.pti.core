/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.preferences;

public abstract class AbstractPHPToolPreferences {
	protected String phpExecutable;
	protected boolean printOutput;

	protected AbstractPHPToolPreferences(String phpExecutable, boolean printOutput) {
		this.phpExecutable = phpExecutable;
		this.printOutput = printOutput;
	}

	public String getPhpExecutable() {
		return phpExecutable;
	}

	public boolean isPrintOutput() {
		return printOutput;
	}
}
