/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Status
 * 
 * @author jllort
 *
 */
public class Status extends PopupPanel {
	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;
	private Widget widget;
	
	private boolean flag_getRooms = false;
	private boolean flag_createRoom = false;
	private boolean flag_deleteRoom = false;
	private boolean flag_sendInvitation = false;
	private boolean flag_addDocument = false;

	/**
	 * The status
	 */
	public Status(Widget widget) {
		super(false,true);
		this.widget = widget;
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");
		
		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);
		
		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");
		
		hPanel.setHeight("25px");
		
		msg.setStyleName("okm-NoWrap");
		
		super.hide();
		setWidget(hPanel);
	}
	
	/**
	 * Refreshing the panel
	 */
	public void refresh() {
		if (flag_getRooms | flag_createRoom | flag_deleteRoom | flag_sendInvitation | flag_addDocument) {
			int left = widget.getAbsoluteLeft() + (widget.getOffsetWidth() - 200) / 2;
			int top = widget.getAbsoluteTop() + (widget.getOffsetHeight() - 40) / 2;
			
			if (left > 0 && top > 0) {
				setPopupPosition(left, top);
				super.show();
			}
		} else {
			super.hide();
		}
	}
	
	/**
	 * Sets the get rooms flag
	 */
	public void setGetRooms() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.openmeeting.get.rooms"));
		flag_getRooms = true;
		refresh();
	}
	
	/**
	 * Unset the get rooms flag
	 */
	public void unsetGetRooms() {
		flag_getRooms = false;
		refresh();
	}
	
	/**
	 * Sets the create room flag
	 */
	public void setCreateRoom() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.openmeeting.create.rooms"));
		flag_createRoom = true;
		refresh();
	}
	
	/**
	 * Unset the create room flag
	 */
	public void unsetCreateRoom() {
		flag_createRoom = false;
		refresh();
	}
	
	/**
	 * Sets the delete room flag
	 */
	public void setDeleteRoom() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.openmeeting.delete.rooms"));
		flag_deleteRoom = true;
		refresh();
	}
	
	/**
	 * Unset the delete room flag
	 */
	public void unsetDeleteRoom() {
		flag_deleteRoom = false;
		refresh();
	}
	
	/**
	 * Sets the send invitation flag
	 */
	public void setSendInvitation() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.openmeeting.send.invitation"));
		flag_sendInvitation = true;
		refresh();
	}
	
	/**
	 * Unset the send invitation flag
	 */
	public void unsetSendInvitation() {
		flag_sendInvitation = false;
		refresh();
	}
	
	/**
	 * Sets the add document flag
	 */
	public void setAddDocument() {
		msg.setHTML(GeneralComunicator.i18nExtension("status.openmeeting.add.document"));
		flag_addDocument = true;
		refresh();
	}
	
	/**
	 * Unset the add document flag
	 */
	public void unsetAddDocument() {
		flag_addDocument = false;
		refresh();
	}
}