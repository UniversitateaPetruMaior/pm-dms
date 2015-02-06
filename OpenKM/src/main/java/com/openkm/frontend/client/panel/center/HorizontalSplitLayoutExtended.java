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

package com.openkm.frontend.client.panel.center;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * HorizontalSplitLayoutExtended
 * 
 * @author jllort
 */
public class HorizontalSplitLayoutExtended extends Composite {
	
	private SplitLayoutPanel horizontalSplitLayoutPanel;
	private int leftWidth = 0;
	private int rightWidth = 0;
	
	/**
	 * HorizontalSplitPanelExtendend
	 * 
	 * @param handler
	 */
	public HorizontalSplitLayoutExtended(final HorizontalResizeHandler resizeHander) {
		super();
		horizontalSplitLayoutPanel = new SplitLayoutPanel() {
			@Override
			public void onResize() {
				super.onResize();
				leftWidth = Integer.parseInt(DOM.getStyleAttribute(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 2),"left").replace("px", "").trim());
				rightWidth = this.getOffsetWidth()-Integer.parseInt(DOM.getStyleAttribute(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 3),"left").replace("px", "").trim());
				resizeHander.onResize(leftWidth, rightWidth);
			}
		};
		
		horizontalSplitLayoutPanel.setStyleName("okm-HorizontalSplitPanel");
		initWidget(horizontalSplitLayoutPanel);
	}
	
	/**
	 * getSplitPanel
	 * 
	 * @return
	 */
	public SplitLayoutPanel getSplitPanel() {
		return horizontalSplitLayoutPanel;
	}
	
	/**
	 * getLeftWidth
	 */
	public int getLeftWidth() {
		return leftWidth;
	}
	
	/**
	 * getRightWidth
	 */
	public int getRightWidth() {
		return rightWidth;
	}
	
	/**
     * setSplitPosition
     */
    public void setSplitPosition(int leftWidth) {
    	DOM.setStyleAttribute(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 1), "width", String.valueOf(leftWidth));
    	DOM.setStyleAttribute(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 2), "left", String.valueOf(leftWidth));
    	DOM.setStyleAttribute(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 2), "width", String.valueOf(10));
    	DOM.setStyleAttribute(DOM.getChild(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 2),0),"width", String.valueOf(10));
    	DOM.setStyleAttribute(DOM.getChild(horizontalSplitLayoutPanel.getElement(), 3), "left", String.valueOf(leftWidth+10));
    }
}