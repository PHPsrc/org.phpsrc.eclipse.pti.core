/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.php.inifile;

public class INIFileEntry {

	private static final String GLOBAL_SECTION = "__global__"; //$NON-NLS-1$

	private final String section;
	private final String name;
	private final String value;
	private final boolean additional;

	public INIFileEntry(String name, String value) {
		this(GLOBAL_SECTION, name, value);
	}

	public INIFileEntry(String name, String value, boolean additional) {
		this(GLOBAL_SECTION, name, value, additional);
	}

	public INIFileEntry(String section, String name, String value) {
		this(section, name, value, false);
	}

	public INIFileEntry(String section, String name, String value, boolean additional) {
		this.section = section;
		this.name = name;
		this.value = value;
		this.additional = additional;
	}

	public String getSection() {
		return section;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public boolean isAdditional() {
		return additional;
	}
}
