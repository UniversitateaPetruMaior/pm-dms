/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2014 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.extension.frontend.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.extension.widget.tabworkspace.TabWorkspaceExtension;

/**
 * @author jllort
 * 
 */
public class TabWorkspaceExample extends TabWorkspaceExtension {
	private VerticalPanel vPanel;
	
	/**
	 * TabWorkspaceExample
	 */
	public TabWorkspaceExample() {
		vPanel = new VerticalPanel();
		vPanel.add(new HTML("new workspace example"));
		vPanel.setStyleName("okm-Input");
		
		initWidget(vPanel);
	}
	
	@Override
	public String getTabText() {
		return "tab workspace";
	}

	@Override
	public void setTab(TabBar tabBar, int tabIndex) {
		// TODO Auto-generated method stub
		
	}
}