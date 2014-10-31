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


package com.openkm.extension.frontend.client.widget.toolbar.downloadButton;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.HasWorkspaceEvent;
import com.openkm.frontend.client.extension.event.HasLanguageEvent.LanguageEventConstant;
import com.openkm.frontend.client.extension.event.HasWorkspaceEvent.WorkspaceEventConstant;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.openkm.frontend.client.extension.event.handler.WorkspaceHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;

/**
 * DownloadButton
 * 
 * @author jllort
 *
 */
public class DownloadButton {
	
	public static final String NO_RESTRICTION_ROLE = "NoDownloadingRestrictionRole";
	public static final String UUID = "df5eb783-fb06-4b4b-bc89-4fdaa244e888";
	
	private ToolBarButton button;
	private boolean enabled = false;

	public DownloadButton(List<String> uuidList) {
		if (isRegistered(uuidList)) {
			button = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.downloadDisabled()), 
					                   GeneralComunicator.i18nExtension("download.button.title"), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (enabled) {
						GeneralComunicator.downloadDocument(false);
					}
				}
			});
		}
	}
	
	/**
	 * ToolBarButtonExtension
	 * 
	 * @return
	 */
	public ToolBarButtonExtension getButton() {
		return button;
	}
	
	/**
	 * ToolBarButton
	 * 
	 * @author jllort
	 *
	 */
	private class ToolBarButton extends ToolBarButtonExtension implements LanguageHandlerExtension, WorkspaceHandlerExtension {
		
		public ToolBarButton(Image image, String title, ClickHandler handler) {
			super(image, title, handler);
		}

		@Override
		public void checkPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
			refreshButtonPermissions();
		}

		@Override
		public void checkPermissions(GWTDocument doc, GWTFolder folder) {
			refreshButtonPermissions();
		}

		@Override
		public void checkPermissions(GWTMail mail, GWTFolder folder) {
			refreshButtonPermissions();
		}

		@Override
		public void enable(boolean enable) {
			enabled = enable;
			evaluateShowIcon();
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}
		
		@Override
		public void onChange(LanguageEventConstant event) {
			if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
				setTitle(GeneralComunicator.i18nExtension("download.button.title"));
			}
		}
		
		@Override
		public void onChange(WorkspaceEventConstant event) {
			if (event.equals(HasWorkspaceEvent.STACK_CHANGED)) {
				refreshButtonPermissions();
			}
		}
		
		/**
		 * refreshButtonPermissions
		 */
		private void refreshButtonPermissions() {
			// Button permissions are the same as download 
			enabled = GeneralComunicator.getToolBarOption().downloadOption;
			if (enabled && !GeneralComunicator.getUserRoleList().contains(NO_RESTRICTION_ROLE)) {
				enabled = false;
			}
			evaluateShowIcon();
		}
		
		/**
		 * evaluateShowIcon
		 */
		private void evaluateShowIcon() {
			if (enabled) {
				enableDownload();
			} else {
				disableDownload();
			}
		}
		
		/**
		 * enableDownload
		 */
		private void enableDownload() {
			setStyleName("okm-ToolBar-button");
			setResource(OKMBundleResources.INSTANCE.download());
			setTitle(GeneralComunicator.i18nExtension("download.button.title"));
		}
		
		/**
		 * disableDownload
		 */
		private void disableDownload() {
			setStyleName("okm-ToolBar-button-disabled");
			setResource(OKMBundleResources.INSTANCE.downloadDisabled());
			setTitle(GeneralComunicator.i18nExtension("download.button.title"));
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