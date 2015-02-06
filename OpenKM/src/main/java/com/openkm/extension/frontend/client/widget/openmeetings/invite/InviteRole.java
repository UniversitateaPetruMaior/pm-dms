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

import java.util.Iterator;
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
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

/**
 * InviteRole
 * 
 * @author jllort
 *
 */
public class InviteRole extends Composite {
	
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	
	private HorizontalPanel hPanel;
	private RoleScrollTable inviteRolesTable;
	private RoleScrollTable rolesTable;
	private VerticalPanel buttonPanel;
	private Image addButton;
	private Image removeButton;
	
	/**
	 * NotifyUser
	 */
	public InviteRole() {
		hPanel = new HorizontalPanel();
		inviteRolesTable = new RoleScrollTable(true);
		rolesTable = new RoleScrollTable(false);
		
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
		
		hPanel.add(rolesTable);
		hPanel.add(buttonPanel);
		hPanel.add(inviteRolesTable);
		hPanel.setCellVerticalAlignment(buttonPanel,VerticalPanel.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(buttonPanel,HorizontalPanel.ALIGN_CENTER);
		hPanel.setCellWidth(buttonPanel,"20");

		inviteRolesTable.addStyleName("okm-Border-Left");
		inviteRolesTable.addStyleName("okm-Border-Right");
		inviteRolesTable.addStyleName("okm-Border-Bottom");
		rolesTable.addStyleName("okm-Border-Left");
		rolesTable.addStyleName("okm-Border-Right");
		rolesTable.addStyleName("okm-Border-Bottom");
		
		reset();
		
		initWidget(hPanel);
	}
	
	/**
	 * reset
	 */
	public void reset() {
		inviteRolesTable.reset();
		rolesTable.reset();
	}
	
	/**
	 * resetAvailableRoles
	 */
	public void resetAvailableRolesTable() {
		rolesTable.reset();
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		inviteRolesTable.langRefresh();
		rolesTable.langRefresh();
	}
	
	/**
	 * Add button handler
	 */
	ClickHandler addButtonHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (rolesTable.getRole() != null) {
				inviteRolesTable.addRow(rolesTable.getRole());	
				inviteRolesTable.selectLastRow();
				rolesTable.removeSelectedRow();
				Main.get().fileUpload.disableErrorNotify();  // Used in both widgets
				Main.get().notifyPopup.disableErrorNotify(); // has no bad efeccts disabling 
			}
		}
	};
	
	/**
	 * Remove button handler
	 */
	ClickHandler removeButtonHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (inviteRolesTable.getRole() != null) {
				rolesTable.addRow(inviteRolesTable.getRole());
				rolesTable.selectLastRow();
				inviteRolesTable.removeSelectedRow();
			}
		}
	};
	
	/**
	 * Call back get all roles
	 */
	final AsyncCallback<List<String>> callbackAllRoles = new AsyncCallback<List<String>>() {
		public void onSuccess(List<String> result) {			
			for (Iterator<String> it = result.iterator(); it.hasNext(); ) {
				rolesTable.addRow(it.next());
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetAllRoles", caught);
		}
	};
	
	/**
	 * Gets all roles
	 */
	public void getAllRoles() {
		authService.getAllRoles(callbackAllRoles);
	}
	
	/**
	 * Gets all roles
	 */
	public void getFilteredAllRoles(String filter) {
		authService.getFilteredAllRoles(filter, inviteRolesTable.getRolesToNotifyList() ,callbackAllRoles);
	}
	
	/**
	 * getRolesToNotify
	 * 
	 * @return
	 */
	public String getRolesToNotify() {
		return inviteRolesTable.getRolesToNotify();
	}
}