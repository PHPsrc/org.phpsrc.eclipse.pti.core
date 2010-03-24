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