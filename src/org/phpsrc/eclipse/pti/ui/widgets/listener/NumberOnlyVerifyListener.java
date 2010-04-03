/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.widgets.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class NumberOnlyVerifyListener implements Listener {

	public static final int TYPE_INT = 1;
	public static final int TYPE_FLOAT = 2;

	public static final char SIGNED = '-';
	public static final char UNSIGNED = '+';

	private int type;
	private int sign;

	public NumberOnlyVerifyListener() {
		this(TYPE_INT, UNSIGNED);
	}

	public NumberOnlyVerifyListener(int type, char sign) {
		this.type = type;
		this.sign = sign;
	}

	public void handleEvent(Event e) {
		String string = e.text;

		char[] chars = new char[string.length()];
		string.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!('0' <= chars[i] && chars[i] <= '9')) {
				if ((type == TYPE_INT || chars[i] != '.') && (sign == UNSIGNED || chars[i] != '-')) {
					e.doit = false;
					return;
				}
			}
		}
	}
}
