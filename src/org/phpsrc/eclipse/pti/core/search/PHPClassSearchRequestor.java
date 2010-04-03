/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.search;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchRequestor;

public class PHPClassSearchRequestor extends SearchRequestor {
	private ArrayList<SearchMatch> searchMatches;

	public void beginReporting() {
		searchMatches = new ArrayList<SearchMatch>();
		super.beginReporting();
	}

	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.isExact()) {
			searchMatches.add(match);
		}
	}

	public SearchMatch[] getMatches() {
		return searchMatches.toArray(new SearchMatch[0]);
	}
}
