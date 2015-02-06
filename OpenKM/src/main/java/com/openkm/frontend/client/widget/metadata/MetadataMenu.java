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

package com.openkm.frontend.client.widget.metadata;

import com.google.gwt.user.client.ui.MenuBar;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * MetadataMenu menu
 * 
 * @author jllort
 *
 */
public class MetadataMenu extends MenuBase {
	
	private MenuBar dirMenu;
	
	public MetadataMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		dirMenu = new MenuBar(true);
		initWidget(dirMenu);
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
	}
	
	@Override
	public void langRefresh() {}
	@Override
	public void evaluateMenuOptions() {}
	@Override
	public void disableAllOptions() {}
	public void hide() {}
	public void show() {}
	@Override
	public void setOptions(ToolBarOption toolBarOption) {}
	@Override
	public void enableAddPropertyGroup() {}
	@Override
	public void disableAddPropertyGroup() {}
	@Override
	public void disablePdfMerge() {}
	@Override
	public void enablePdfMerge() {}
}