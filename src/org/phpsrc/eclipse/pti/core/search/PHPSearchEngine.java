/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.search.DLTKSearchParticipant;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPSearchEngine {

	public static IDLTKSearchScope createWorkspaceScope() {
		DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
		return factory.createWorkspaceScope(false,
				PHPLanguageToolkit.getDefault());
	}

	public static IDLTKSearchScope createProjectScope(IProject project) {
		return createProjectScope(DLTKCore.create(project));
	}

	public static IDLTKSearchScope createProjectScope(IScriptProject project) {
		DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
		return factory.createProjectSearchScope(project, false);
	}

	public static SearchMatch[] findClass(String className) {
		return findClass(className, createWorkspaceScope());
	}

	public static SearchMatch[] findClass(String className, int matchRule) {
		return findClass(className, createWorkspaceScope(), matchRule);
	}

	public static SearchMatch[] findClass(String className,
			IDLTKSearchScope scope) {
		return findClass(className, scope, SearchPattern.R_EXACT_MATCH);
	}

	public static SearchMatch[] findClass(String className,
			IDLTKSearchScope scope, int matchRule) {
		PatternQuerySpecification querySpec = new PatternQuerySpecification(
				className, IDLTKSearchConstants.TYPE, false,
				IDLTKSearchConstants.DECLARATIONS, scope, "");

		SearchPattern pattern = SearchPattern.createPattern(
				querySpec.getPattern(), querySpec.getSearchFor(),
				querySpec.getLimitTo(), matchRule, scope.getLanguageToolkit());

		return findMatches(pattern, scope);
	}

	private static SearchMatch[] findMatches(SearchPattern pattern,
			IDLTKSearchScope scope) {
		SearchEngine engine = new SearchEngine();
		try {
			PHPClassSearchRequestor requestor = new PHPClassSearchRequestor();
			engine.search(pattern,
					new SearchParticipant[] { new DLTKSearchParticipant() },
					scope, requestor, new NullProgressMonitor());

			return requestor.getMatches();

		} catch (CoreException e) {
			Logger.logException(e);
		}

		return new SearchMatch[0];
	}
}
