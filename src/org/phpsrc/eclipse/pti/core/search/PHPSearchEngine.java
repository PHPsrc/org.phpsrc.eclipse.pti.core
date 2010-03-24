/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
		return factory.createWorkspaceScope(false, PHPLanguageToolkit.getDefault());
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

	public static SearchMatch[] findClass(String className, IDLTKSearchScope scope) {
		return findClass(className, scope, SearchPattern.R_EXACT_MATCH);
	}

	public static SearchMatch[] findClass(String className, IDLTKSearchScope scope, int matchRule) {
		PatternQuerySpecification querySpec = new PatternQuerySpecification(className, IDLTKSearchConstants.TYPE,
				false, IDLTKSearchConstants.DECLARATIONS, scope, "");

		SearchPattern pattern = SearchPattern.createPattern(querySpec.getPattern(), querySpec.getSearchFor(), querySpec
				.getLimitTo(), matchRule, scope.getLanguageToolkit());

		return findMatches(pattern, scope);
	}

	private static SearchMatch[] findMatches(SearchPattern pattern, IDLTKSearchScope scope) {
		SearchEngine engine = new SearchEngine();
		try {
			PHPClassSearchRequestor requestor = new PHPClassSearchRequestor();
			engine.search(pattern, new SearchParticipant[] { new DLTKSearchParticipant() }, scope, requestor,
					new NullProgressMonitor());

			return requestor.getMatches();

		} catch (CoreException e) {
			Logger.logException(e);
		}

		return new SearchMatch[0];
	}
}
