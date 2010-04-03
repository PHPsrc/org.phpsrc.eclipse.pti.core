/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.php.inifile;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public class INIFileUtil {
	public static INIFileEntry createIncludePathEntry(IPath[] includePaths) {
		StringBuffer sb = new StringBuffer();
		if (includePaths != null && includePaths.length > 0) {
			sb.append(includePaths[0]);
			for (int i = 1; i < includePaths.length; i++) {
				sb.append(File.pathSeparator);
				sb.append(includePaths[i].toOSString());
			}
		}

		return new INIFileEntry("PHP", "include_path", sb.toString(), true);
	}
}
