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
package com.openkm.extension.frontend.client.widget.openmeetings;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarBoxExtension;

/**
 * ToolBarBoxOpenMeeting
 * 
 * @author jllort
 *
 */
public class ToolBarBoxOpenMeeting {
	private ToolBarBoxExtension toolBarBoxExtension;
	private Panel sp;
	public OpenMeetingsManager manager;
	
	/**
	 * ToolBarBoxForum
	 */
	public ToolBarBoxOpenMeeting() {
		toolBarBoxExtension = new ToolBarBoxExtension(new Image(OKMBundleResources.INSTANCE.meeting()), GeneralComunicator.i18nExtension("meeting.title")) {
			@Override
			public Widget getWidget() {
				return sp;
			}
		};
		manager = new OpenMeetingsManager(100, 100);
		sp = new Panel();
		sp.add(manager);
	}
	
	/**
	 * ToolBarBoxExtension
	 * 
	 * @return
	 */
	public ToolBarBoxExtension getToolBarBox() {
		return toolBarBoxExtension;
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		manager.langRefresh();
	}
	
	/**
	 * refreshPixelSize
	 */
	public void refreshPixelSize() {
		sp.refreshPixelSize();
	}
	
	/**
	 * Panel
	 * 
	 * @author jllort
	 *
	 */
	class Panel extends SimplePanel {
		private int width = 0;
		private int height = 0;
		
		@Override
		public void setPixelSize(int width, int height) {
			this.width = width;
			this.height = height;
			refreshPixelSize();
		}
		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			manager.setVisible(visible);
		}
		
		/**
		 * refreshPixelSize
		 */
		public void refreshPixelSize() {
			super.setPixelSize(width, height);
			manager.setPixelSize(width, height);
			manager.setWidth(width);
		}
	}
}