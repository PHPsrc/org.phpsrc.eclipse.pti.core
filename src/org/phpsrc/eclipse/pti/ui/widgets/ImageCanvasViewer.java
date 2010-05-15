/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.widgets;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;

public class ImageCanvasViewer extends Composite {

	private final static String KEY_ACTION = "ImageCanvasViewer.Action"; //$NON-NLS-1$

	public final static int DEFAULT_ACTIONS = -1;
	public final static int ACTION_ZOOM_OUT = 1;
	public final static int ACTION_ZOOM_IN = 2;
	public final static int ACTION_ORIGINAL = 4;
	public final static int ACTION_FIT = 8;

	private ToolBar fToolBar;
	private ImageCanvas fImageCanvas;
	private SelectionListener selectionListener;

	public ImageCanvasViewer(Composite parent, int style) {
		super(parent, style);
		GridLayoutFactory.fillDefaults().spacing(0, 0).equalWidth(true).applyTo(this);

		fToolBar = new ToolBar(this, SWT.NONE);
		GridDataFactory.swtDefaults().grab(true, false).applyTo(fToolBar);

		selectionListener = new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				doAction(((Integer) e.widget.getData(KEY_ACTION)).intValue());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};

		ImageRegistry imageRegistry = PHPToolCorePlugin.getDefault().getImageRegistry();
		createToolBarItem(imageRegistry.get(PHPToolCorePlugin.IMG_IMAGE_ZOOM_OUT), "Zoom out", ACTION_ZOOM_OUT);
		createToolBarItem(imageRegistry.get(PHPToolCorePlugin.IMG_IMAGE_ZOOM_IN), "Zoom in", ACTION_ZOOM_IN);

		createToolBarItem(imageRegistry.get(PHPToolCorePlugin.IMG_IMAGE_ORIGINAL), "Original size", ACTION_ORIGINAL);
		createToolBarItem(imageRegistry.get(PHPToolCorePlugin.IMG_IMAGE_FIT), "Fit window", ACTION_FIT);

		fImageCanvas = new ImageCanvas(this, style);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(fImageCanvas);
		fImageCanvas.setBackground(new Color(this.getDisplay(), 255, 255, 255));
	}

	private void doAction(int action) {
		switch (action) {
		case ACTION_ZOOM_OUT:
			fImageCanvas.zoomOut();
			break;
		case ACTION_ZOOM_IN:
			fImageCanvas.zoomIn();
			break;
		case ACTION_ORIGINAL:
			fImageCanvas.showOriginal();
			break;
		case ACTION_FIT:
			fImageCanvas.fitCanvas();
			break;
		}
	}

	private void createToolBarItem(Image image, String tooltip, int action) {
		ToolItem item = new ToolItem(fToolBar, SWT.PUSH);
		item.setImage(image);
		item.setToolTipText(tooltip);
		item.setData(KEY_ACTION, new Integer(action));
		item.addSelectionListener(selectionListener);
	}

	public ImageCanvas getImageCanvas() {
		return fImageCanvas;
	}

	public void setBackground(Color color) {
		super.setBackground(color);
		fToolBar.setBackground(color);
		fImageCanvas.setBackground(color);
	}
}
