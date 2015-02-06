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

import java.util.ArrayList;
import java.util.List;

import com.openkm.frontend.client.extension.comunicator.DashboardComunicator;
import com.openkm.frontend.client.extension.event.HasDashboardEvent;
import com.openkm.frontend.client.extension.event.HasDashboardEvent.DashboardEventConstant;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.handler.DashboardHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;

/**
 * OpenMeetings
 * 
 * @author jllort
 *
 */
public class OpenMeetings implements DashboardHandlerExtension, LanguageHandlerExtension {
	private static final String UUID = "905075bd-f969-4d95-91ea-4900adc90471";
	private static OpenMeetings singleton;
	
	public Status generalStatus;
	public ToolBarBoxOpenMeeting toolBarBoxOpenMeeting;
	
	/**
	 * OpenMeetings
	 * 
	 * @param uuidList
	 */
	public OpenMeetings(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			singleton = this;
			toolBarBoxOpenMeeting = new ToolBarBoxOpenMeeting();
			generalStatus = new Status(toolBarBoxOpenMeeting.manager);
			generalStatus.setStyleName("okm-StatusPopup");
		}
	}
	
	/**
	 * getExtensions
	 * 
	 * @return
	 */
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<Object>();
		extensions.add(singleton);
		extensions.add(toolBarBoxOpenMeeting.getToolBarBox());
		return extensions;
	}
	
	/**
	 * get
	 * 
	 * @return
	 */
	public static OpenMeetings get() {
		return singleton;
	}
	
	@Override
	public void onChange(DashboardEventConstant event) {
		if (event.equals(HasDashboardEvent.TOOLBOX_CHANGED)) {
			if (DashboardComunicator.isWidgetExtensionVisible(toolBarBoxOpenMeeting.getToolBarBox().getWidget())) {
				toolBarBoxOpenMeeting.refreshPixelSize();
			}
		}
	}
	
	@Override
	public void onChange(LanguageEventConstant event) {
		if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
			toolBarBoxOpenMeeting.langRefresh();
		}
	}
	
	/**
	 * isRegistered
	 * 
	 * @param uuidList
	 * @return
	 */
	public static boolean isRegistered(List<String> uuidList) {
		return uuidList.contains(UUID);
	}
}