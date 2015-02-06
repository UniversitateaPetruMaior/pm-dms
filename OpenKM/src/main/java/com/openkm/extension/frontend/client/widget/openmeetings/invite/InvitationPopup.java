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

package com.openkm.extension.frontend.client.widget.openmeetings.invite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.extension.frontend.client.widget.openmeetings.OpenMeetings;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.WorkspaceComunicator;
import com.openkm.frontend.client.util.Util;

/**
 * InvitationPopup
 * 
 * @author jllort
 * 
 */
public class InvitationPopup extends DialogBox {
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button closeButton;
	private Button sendButton;
	private TextArea message;
	private ScrollPanel messageScroll;
	private InvitationPanel notifyPanel;
	private HTML commentTXT;
	private HTML subjectTXT;
	private TextBox subject;
	private HTML errorNotify;
	private String users;
	private String roles;
	private long roomId;
	
	public InvitationPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		
		setText(GeneralComunicator.i18nExtension("openmeetings.invitate.title"));
		users = "";
		roles = "";
		
		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		notifyPanel = new InvitationPanel();
		message = new TextArea();
		
		errorNotify = new HTML(GeneralComunicator.i18nExtension("openmeetings.invitate.must.select.users"));
		errorNotify.setWidth("365");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");
		
		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ GeneralComunicator.i18nExtension("openmeetings.invitate.comment"));
		subjectTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ GeneralComunicator.i18nExtension("openmeetings.invitate.subject"));
		
		closeButton = new Button(GeneralComunicator.i18nExtension("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				reset();
			}
		});
		
		sendButton = new Button(GeneralComunicator.i18nExtension("button.send"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Only sends if there's some user selected
				users = notifyPanel.getUsersToNotify();
				roles = notifyPanel.getRolesToNotify();
				if (!users.equals("") || !roles.equals("")) {
					errorNotify.setVisible(false);
					OpenMeetings.get().toolBarBoxOpenMeeting.manager.sendInvitation(roomId, users, roles,
							subject.getText(), message.getText());
					hide();
					reset();
				} else {
					errorNotify.setVisible(true);
				}
			}
		});
		
		hPanel.add(closeButton);
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.add(sendButton);
		
		hPanel.setCellWidth(space, "40");
		
		message.setSize("375", "60");
		message.setStyleName("okm-TextArea");
		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);
		
		subject = new TextBox();
		subject.setWidth("375");
		subject.setMaxLength(150);
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(subjectTXT);
		vPanel.add(subject);
		vPanel.add(new HTML("<br>"));
		vPanel.add(commentTXT);
		vPanel.add(messageScroll);
		vPanel.add(errorNotify);
		vPanel.add(new HTML("<br>"));
		vPanel.add(notifyPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(errorNotify, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(messageScroll, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(notifyPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(subject, VerticalPanel.ALIGN_CENTER);
		
		vPanel.setWidth("100%");
		
		closeButton.setStyleName("okm-NoButton");
		sendButton.setStyleName("okm-YesButton");
		
		subjectTXT.addStyleName("okm-DisableSelect");
		commentTXT.addStyleName("okm-DisableSelect");
		notifyPanel.addStyleName("okm-DisableSelect");
		subject.setStyleName("okm-Input");
		
		setWidget(vPanel);
	}
	
	/**
	 * langRefresh
	 * 
	 * Refreshing lang
	 */
	public void langRefresh() {
		setText(GeneralComunicator.i18nExtension("openmeetings.invitate.title"));
		closeButton.setHTML(GeneralComunicator.i18nExtension("button.close"));
		sendButton.setHTML(GeneralComunicator.i18nExtension("button.send"));
		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ GeneralComunicator.i18nExtension("openmeetings.invitate.comment"));
		subjectTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ GeneralComunicator.i18nExtension("openmeetings.invitate.subject"));
		errorNotify.setHTML(GeneralComunicator.i18nExtension("openmeetings.invitate.must.select.users"));
		notifyPanel.langRefresh();
	}
	
	/**
	 * executeSendInvitation
	 * 
	 * @param TYPE
	 */
	public void executeSendInvitation(long roomId) {
		this.roomId = roomId;
		reset();
		super.center();
		if (WorkspaceComunicator.getWorkspace().isAdvancedFilters()) {
			enableAdvancedFilter();
		}
		// TODO:Solves minor bug with IE
		if (Util.getUserAgent().startsWith("ie")) {
			notifyPanel.tabPanel.setWidth("374");
			notifyPanel.tabPanel.setWidth("375");
		}
	}
	
	/**
	 * Reset values
	 */
	private void reset() {
		users = "";
		roles = "";
		message.setText("");
		subject.setText("");
		notifyPanel.reset();
		notifyPanel.getAll();
		errorNotify.setVisible(false);
	}
	
	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		notifyPanel.enableAdvancedFilter();
	}
	
	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		errorNotify.setVisible(false);
	}
}
