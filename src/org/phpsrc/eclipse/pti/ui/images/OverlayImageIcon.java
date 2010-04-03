/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.ui.images;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class OverlayImageIcon extends CompositeImageDescriptor {
	public static final int POS_TOP_LEFT = 0;
	public static final int POS_TOP_RIGHT = 1;
	public static final int POS_BOTTOM_LEFT = 2;
	public static final int POS_BOTTOM_RIGHT = 3;

	private final Image baseImage;
	private final Image overlayImage;
	private final int location;
	private final Point sizeOfImage;

	public OverlayImageIcon(Image baseImage, Image overlayImage, int location) {
		this.baseImage = baseImage;
		this.overlayImage = overlayImage;
		this.location = location;
		this.sizeOfImage = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
	}

	/**
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int,
	 *      int) DrawCompositeImage is called to draw the composite image.
	 * 
	 */
	protected void drawCompositeImage(int arg0, int arg1) {
		// Draw the base image
		drawImage(baseImage.getImageData(), 0, 0);

		ImageData imageData = overlayImage.getImageData();
		switch (location) {
		// Draw on the top left corner
		case POS_TOP_LEFT:
			drawImage(imageData, 0, 0);
			break;

		// Draw on top right corner
		case POS_TOP_RIGHT:
			drawImage(imageData, sizeOfImage.x - imageData.width, 0);
			break;

		// Draw on bottom left
		case POS_BOTTOM_LEFT:
			drawImage(imageData, 0, sizeOfImage.y - imageData.height);
			break;

		// Draw on bottom right corner
		case POS_BOTTOM_RIGHT:
			drawImage(imageData, sizeOfImage.x - imageData.width, sizeOfImage.y - imageData.height);
			break;
		}
	}

	/**
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize() get
	 *      the size of the object
	 */
	protected Point getSize() {
		return sizeOfImage;
	}

	/**
	 * Get the image formed by overlaying different images on the base image
	 * 
	 * @return composite image
	 */
	public Image getImage() {
		return createImage();
	}
}