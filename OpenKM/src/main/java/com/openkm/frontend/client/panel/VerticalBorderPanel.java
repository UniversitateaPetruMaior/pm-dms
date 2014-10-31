/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2014  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.panel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * Used for Left and right border of the main panel
 * 
 * @author jllort
 *
 */
public class VerticalBorderPanel extends Composite {
	
	private Label leftBar;
	
	/**
	 * Vertical Border Panel
	 */
	public VerticalBorderPanel() {
		leftBar = new Label();
		leftBar.setStyleName("okm-VerticalBorderPanel");
		initWidget(leftBar);
	}
	
	/**
	 * Sets the size
	 * 
	 * @param width the width size
	 * @param height the height size
	 */
	public void setSize(int width, int height) {
		leftBar.setSize(""+width, ""+height);
	}
}