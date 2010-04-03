/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.core.jobs;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class MutexRule implements ISchedulingRule {

	public boolean contains(ISchedulingRule rule) {
		return rule == this;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return rule == this;
	}
}
