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
 * VerticalSplitLayoutExtended
 * 
 * @author jllort
 *
 */
public class VerticalSplitLayoutExtended extends Composite {
	
    private SplitLayoutPanel verticalSplitLayotPanel;
    private int topHeight = 0;
    private int bottomHeight = 0;
    
    /**
     * VerticalSplitLayoutExtended
     * 
     * @param handler
     */
    public VerticalSplitLayoutExtended(final VerticalResizeHandler resizeHander) {
    	super();
        verticalSplitLayotPanel = new SplitLayoutPanel() {
            @Override
            public void onResize() {
                super.onResize();
                topHeight = Integer.parseInt(DOM.getStyleAttribute(DOM.getChild(verticalSplitLayotPanel.getElement(), 2),"top").replace("px", "").trim());
                bottomHeight = this.getOffsetHeight()-Integer.parseInt(DOM.getStyleAttribute(DOM.getChild(verticalSplitLayotPanel.getElement(), 3),"top").replace("px", "").trim());
                resizeHander.onResize(topHeight, bottomHeight);
            }           
        };
        initWidget(verticalSplitLayotPanel);
    }

    /**
     * getSplitPanel
     * 
     * @return
     */
    public SplitLayoutPanel getSplitPanel()
    {
    	return verticalSplitLayotPanel;
    }
    
    /**
     * getTopHeight
     */
    public int getTopHeight() {
    	return topHeight;
    }
    
    /**
     * getBottomHeight
     */
    public int getBottomHeight() {
    	return bottomHeight;
    }
    
    /**
     * setSplitPosition
     */
    public void setSplitPosition(int topHeight) {
    	DOM.setStyleAttribute(DOM.getChild(verticalSplitLayotPanel.getElement(), 1), "height", String.valueOf(topHeight));
    	DOM.setStyleAttribute(DOM.getChild(verticalSplitLayotPanel.getElement(), 2), "top", String.valueOf(topHeight));
    	DOM.setStyleAttribute(DOM.getChild(verticalSplitLayotPanel.getElement(), 2), "height", String.valueOf(10));
    	DOM.setStyleAttribute(DOM.getChild(DOM.getChild(verticalSplitLayotPanel.getElement(), 2),0),"height", String.valueOf(10));
    	DOM.setStyleAttribute(DOM.getChild(verticalSplitLayotPanel.getElement(), 3), "top", String.valueOf(topHeight+10));
    }
}