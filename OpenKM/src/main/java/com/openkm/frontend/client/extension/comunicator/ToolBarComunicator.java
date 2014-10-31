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

package com.openkm.frontend.client.extension.comunicator;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.ToolBarOption;

/**
 * ToolBarComunicator
 * 
 * @author jllort
 *
 */
public class ToolBarComunicator {
	
	/**
	 * getToolBarOption
	 * 
	 * @return
	 */
	public static ToolBarOption getToolBarOption() {
		return Main.get().mainPanel.topPanel.toolBar.getToolBarOption();
	}
	
	/**
	 * setToolBarOption
	 * 
	 * @param toolBarOption
	 */
	public static void setToolBarOption(ToolBarOption toolBarOption) {
		Main.get().mainPanel.topPanel.toolBar.setToolBarOption(toolBarOption);
	}
	
	/**
	 * evaluateShowIcons
	 */
	public static void evaluateShowIcons() {
		Main.get().mainPanel.topPanel.toolBar.evaluateShowIcons();
	}
	
	/**
	 * getActualNode
	 */
	public static Object getActualNode() {
		return Main.get().mainPanel.topPanel.toolBar.getActualNode();
	}
	
	public static HorizontalPanel getMainToolBarPanel() {
		return Main.get().mainPanel.topPanel.toolBar.getMainToolBarPanel();
	}
}