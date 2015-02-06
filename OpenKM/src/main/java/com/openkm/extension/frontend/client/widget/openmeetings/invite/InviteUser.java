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

package com.openkm.extension.frontend.client.widget.openmeetings.invite;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

/**
 * InviteUser
 * 
 * @author jllort
 *
 */
public class InviteUser extends Composite {
	
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	
	private HorizontalPanel hPanel;
	private UserScrollTable inviteUsersTable;
	private UserScrollTable userTable;
	private VerticalPanel buttonPanel;
	private Image addButton;
	private Image removeButton;
	
	/**
	 * NotifyUser
	 */
	public InviteUser() {
		hPanel = new HorizontalPanel();
		inviteUsersTable = new UserScrollTable(true);
		userTable = new UserScrollTable(false);
		
		buttonPanel = new VerticalPanel();
		addButton = new Image(OKMBundleResources.INSTANCE.add());
		removeButton = new Image(OKMBundleResources.INSTANCE.remove());
		
		HTML space = new HTML("");
		buttonPanel.add(addButton);
		buttonPanel.add(space); // separator
		buttonPanel.add(removeButton);
		
		buttonPanel.setCellHeight(space, "40");
		
		addButton.addClickHandler(addButtonHandler);
		removeButton.addClickHandler(removeButtonHandler);
		
		hPanel.setSize("374","140");
		
		hPanel.add(userTable);
		hPanel.add(buttonPanel);
		hPanel.add(inviteUsersTable);
		hPanel.setCellVerticalAlignment(buttonPanel,VerticalPanel.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(buttonPanel,HorizontalPanel.ALIGN_CENTER);
		hPanel.setCellWidth(buttonPanel,"20");
		
		userTable.addStyleName("okm-Border-Left");
		userTable.addStyleName("okm-Border-Right");
		userTable.addStyleName("okm-Border-Bottom");
		inviteUsersTable.addStyleName("okm-Border-Left");
		inviteUsersTable.addStyleName("okm-Border-Right");
		inviteUsersTable.addStyleName("okm-Border-Bottom");
		
		reset();
		
		initWidget(hPanel);
	}
	
	/**
	 * reset
	 */
	public void reset() {
		inviteUsersTable.reset();
		userTable.reset();
	}
	
	/**
	 * resetAvailableUsersTable
	 */
	public void resetAvailableUsersTable() {
		userTable.reset();
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		inviteUsersTable.langRefresh();
		userTable.langRefresh();
	}
	
	/**
	 * Add button handler
	 */
	ClickHandler addButtonHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (userTable.getUser() != null) {
				inviteUsersTable.addRow(userTable.getUser());	
				inviteUsersTable.selectLastRow();
				userTable.removeSelectedRow();
				Main.get().fileUpload.disableErrorNotify();  // Used in both widgets
				Main.get().notifyPopup.disableErrorNotify(); // has no bad effect disabling both
			}
		}
	};
	
	/**
	 * Remove button handler
	 */
	ClickHandler removeButtonHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (inviteUsersTable.getUser() != null) {
				userTable.addRow(inviteUsersTable.getUser());				
				userTable.selectLastRow();
				inviteUsersTable.removeSelectedRow();
			}
		}
	};
	
	/**
	 * Call back get all users
	 */
	final AsyncCallback<List<GWTUser>> callbackAllUsers = new AsyncCallback<List<GWTUser>>() {
		public void onSuccess(List<GWTUser> result) {			
			for (GWTUser user : result) {
				userTable.addRow(user);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetAllUsers", caught);
		}
	};
	
	/**
	 * Gets all users
	 */
	public void getAllUsers() {	
		authService.getAllUsers(callbackAllUsers);
	}
	
	/**
	 * Gets the all users by filter
	 */
	public void getFilteredAllUsers(String filter) {	
		authService.getFilteredAllUsers(filter, inviteUsersTable.getUsersToNotifyList(), callbackAllUsers);
	}
	
	/**
	 * getUsersToNotify
	 * 
	 * @return
	 */
	public String getUsersToNotify() {
		return inviteUsersTable.getUsersToNotify();
	}
}