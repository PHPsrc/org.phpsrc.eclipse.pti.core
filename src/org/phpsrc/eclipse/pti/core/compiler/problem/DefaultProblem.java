package org.phpsrc.eclipse.pti.core.compiler.problem;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;

public class DefaultProblem extends
		org.eclipse.dltk.compiler.problem.DefaultProblem {

	public DefaultProblem(String originatingFileName, String message,
			IProblemIdentifier id, String[] stringArguments,
			ProblemSeverity severity, int startPosition, int endPosition,
			int line, int column) {
		super(originatingFileName, message, id, stringArguments, severity,
				startPosition, endPosition, line, column);
	}

}
