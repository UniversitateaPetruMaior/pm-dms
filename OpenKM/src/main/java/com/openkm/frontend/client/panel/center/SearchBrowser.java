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

import com.google.gwt.user.client.ui.Composite;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.searchin.SearchIn;
import com.openkm.frontend.client.widget.searchresult.SearchResult;

/**
 * Search panel
 * 
 * @author jllort
 *
 */
public class SearchBrowser extends Composite {
	private final static int PANEL_TOP_HEIGHT 	= 210;
	public final static int SPLITTER_HEIGHT 	= 10;
	
	private VerticalSplitLayoutExtended verticalSplitLayoutPanel;
	
	public SearchIn searchIn;
	public SearchResult searchResult;
	
	public int width = 0;
	public int height = 0;
	public int topHeight = 0;
	public int bottomHeight = 0;
	
	/**
	 * SearchBrowser
	 */
	public SearchBrowser() {
		verticalSplitLayoutPanel = new VerticalSplitLayoutExtended(new VerticalResizeHandler() {
			@Override
			public void onResize(int topHeight, int bottomHeight) {
				resizePanels();
			}
		});
		searchIn = new SearchIn();
		searchResult = new SearchResult();
		verticalSplitLayoutPanel.getSplitPanel().addNorth(searchIn,100);
		verticalSplitLayoutPanel.getSplitPanel().add(searchResult);
		
		searchIn.setStyleName("okm-Input");
		initWidget(verticalSplitLayoutPanel);
	}
	
	/**
	 * Refresh language values
	 */
	public void langRefresh() {
		searchIn.langRefresh();	
		searchResult.langRefresh();
	}
	
	/**
	 * Sets the size on initialization
	 * 
	 * @param width The max width of the widget
	 * @param height The max height of the widget
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		topHeight = PANEL_TOP_HEIGHT;
		bottomHeight = height - (topHeight + SPLITTER_HEIGHT);
		verticalSplitLayoutPanel.setSize(""+width, ""+height);
		verticalSplitLayoutPanel.setSplitPosition(topHeight);
		resize();
	}
	
	/**
	 * resize
	 */
	private void resize() {
		verticalSplitLayoutPanel.setWidth(""+width);
		
		// We substract 2 pixels for width and heigh generated by border line
		searchIn.setPixelSize(width, topHeight);
		
		// Resize the scroll panel on tab properties 
		// We substract 2 pixels for width and heigh generated by border line
		int searchResultWidth = width-2;
		int searchResultHeight = bottomHeight-2;
		if (searchResultWidth < 0) {
			searchResultWidth = 0;
		}
		if (searchResultHeight < 0) {
			searchResultHeight = 0;
		}
		searchResult.setPixelSize(searchResultWidth, searchResultHeight);
		
		// TODO:Solves minor bug with IE 
		if (Util.getUserAgent().startsWith("ie")) {
			searchResult.setPixelSize(width, bottomHeight);
		}
	}
	
	
	/**
	 * Sets the panel width on resizing
	 */
	private void resizePanels() {
		topHeight = verticalSplitLayoutPanel.getTopHeight();
		bottomHeight = verticalSplitLayoutPanel.getBottomHeight();		
		resize();
	}
	
	/**
	 * setWidth
	 */
	public void setWidth(int width) {
		this.width = width;
		resize();
	}
	
	/**
	 * setLoadFinish
	 */
	public void setLoadFinish() {
		searchIn.setLoadFinish();
	}
}