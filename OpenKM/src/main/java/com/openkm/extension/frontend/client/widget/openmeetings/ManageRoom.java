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

package com.openkm.extension.frontend.client.widget.openmeetings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.bean.extension.GWTRoom;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.Util;

/**
 * ManageRoom
 * 
 * @author jllort
 * 
 */
public class ManageRoom extends Composite {
	private VerticalPanel vPanel;
	private FlexTable table;
	private TitleWidget createRoomTitle;
	private HTML nameText;
	private TextBox name;
	private HTML typeText;
	private ListBox typeList;
	private HTML participentText;
	private ListBox participentList;
	private HTML publicText;
	private CheckBox publicCheck;
	private HTML appointment;
	private CheckBox apointmentCheck;
	private HTML moderated;
	private CheckBox moderatedCheck;
	private HTML allowUserQuestions;
	private CheckBox allowUserQuestionsCheck;
	private HTML audioOnly;
	private CheckBox audioOnlyCheck;
	private HTML waitForRecording;
	private CheckBox waitForRecordingCheck;
	private HTML allowRecording;
	private CheckBox allowRecordingCheck;
	private HTML topBar;
	private CheckBox topBarCheck;
	private Button create;
	
	/**
	 * ManageRoom
	 */
	public ManageRoom() {
		// Main panel
		vPanel = new VerticalPanel();
		vPanel.setStyleName("okm-WorkflowFormPanel");
		
		// Table
		table = new FlexTable();
		table.setWidth("100%");
		table.setCellPadding(2);
		table.setCellSpacing(0);
		table.setStyleName("okm-NoWrap");
		
		// create new room title
		createRoomTitle = new TitleWidget(GeneralComunicator.i18nExtension("openmeetings.room.create"));
		createRoomTitle.setWidth("100%");
		createRoomTitle.setStyleName("okm-WorkflowFormPanel-Title");
		
		// Room name
		nameText = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.name") + "</b>");
		name = new TextBox();
		name.setWidth("200");
		name.setStyleName("okm-Input");
		
		// Room type
		typeText = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.type") + "</b>");
		typeList = new ListBox();
		
		// Room participent
		participentText = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.participent") + "</b>");
		participentList = new ListBox();
		participentList.addItem("5", "5");
		participentList.addItem("10", "10");
		participentList.addItem("15", "15");
		participentList.addItem("20", "20");
		participentList.addItem("30", "30");
		participentList.addItem("60", "60");
		participentList.addItem("100", "100");
		participentList.addItem("150", "150");
		participentList.setStyleName("okm-Input");
		
		// Room is public
		publicText = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.public") + "</b>");
		publicCheck = new CheckBox();
		
		// Appointment
		appointment = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.appointment") + "</b>");
		apointmentCheck = new CheckBox();
		
		// moderated
		moderated = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.moderated") + "</b>");
		moderatedCheck = new CheckBox();
		
		// allow user questions
		allowUserQuestions = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.allowuserquestions")
				+ "</b>");
		allowUserQuestionsCheck = new CheckBox();
		
		// audio only
		audioOnly = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.only.audio") + "</b>");
		audioOnlyCheck = new CheckBox();
		
		// wait for recording
		waitForRecording = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.waitforrecording")
				+ "</b>");
		waitForRecordingCheck = new CheckBox();
		
		// allow recording
		allowRecording = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.allow.recording") + "</b>");
		allowRecordingCheck = new CheckBox();
		
		// topbar visible
		topBar = new HTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.topbar.visible") + "</b>");
		topBarCheck = new CheckBox();
		topBarCheck.setValue(true);
		
		// Create button
		create = new Button(GeneralComunicator.i18n("button.create"));
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String roomName = name.getText();
				long roomType = new Long(typeList.getValue(typeList.getSelectedIndex())).longValue();
				long numberOfPartizipants = new Long(participentList.getValue(participentList.getSelectedIndex())).longValue();
				boolean isPublic = publicCheck.getValue();
				boolean isAppointment = apointmentCheck.getValue();
				boolean isModerated = moderatedCheck.getValue();
				boolean isAllowUserQuestions = allowUserQuestionsCheck.getValue();
				boolean isAudioOnly = audioOnlyCheck.getValue();
				boolean isWaitForRecording = waitForRecordingCheck.getValue();
				boolean isAllowRecording = allowRecordingCheck.getValue();
				boolean isTopBar = topBarCheck.getValue();
				OpenMeetings.get().toolBarBoxOpenMeeting.manager.createNewRoom(roomName, roomType, numberOfPartizipants,
						isPublic, isAppointment, isModerated, isAllowUserQuestions, isAudioOnly, isWaitForRecording,
						isAllowRecording, isTopBar);
				reset();
			}
		});
		create.setStyleName("okm-AddButton");
		
		int row = 0;
		table.setWidget(row, 0, nameText);
		table.setWidget(row, 1, name);
		table.setHTML(row++, 2, "");
		table.setWidget(row, 0, typeText);
		table.setWidget(row++, 1, typeList);
		table.setWidget(row, 0, participentText);
		table.setWidget(row++, 1, participentList);
		table.setWidget(row, 0, publicText);
		table.setWidget(row++, 1, publicCheck);
		table.setWidget(row, 0, appointment);
		table.setWidget(row++, 1, apointmentCheck);
		table.setWidget(row, 0, moderated);
		table.setWidget(row++, 1, moderatedCheck);
		table.setWidget(row, 0, allowUserQuestions);
		table.setWidget(row++, 1, allowUserQuestionsCheck);
		table.setWidget(row, 0, audioOnly);
		table.setWidget(row++, 1, audioOnlyCheck);
		table.setWidget(row, 0, waitForRecording);
		table.setWidget(row++, 1, waitForRecordingCheck);
		table.setWidget(row, 0, allowRecording);
		table.setWidget(row++, 1, allowRecordingCheck);
		table.setWidget(row, 0, topBar);
		table.setWidget(row++, 1, topBarCheck);
		table.getFlexCellFormatter().setWidth(0, 2, "100%");
		
		vPanel.add(createRoomTitle);
		vPanel.add(table);
		vPanel.add(create);
		HTML space = Util.hSpace("5");
		vPanel.add(space);;
		vPanel.setWidth("100%");
		vPanel.setCellHorizontalAlignment(create, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(space, "100%");
		
		firstTimeInitTypeList(); // Initialize type list ( waiting workspace is loaded ) 
		
		initWidget(vPanel);
	}
	
	/**
	 * firstTimeInitTypeList
	 */
	private void firstTimeInitTypeList() {
		if (GeneralComunicator.getWorkspace()!=null) {
			if (GeneralComunicator.getWorkspace().isAdminRole()) {
				typeList.addItem(GeneralComunicator.i18nExtension("openmeetings.room.type.conference"),
						String.valueOf(GWTRoom.TYPE_CONFERENCE));
			}
			typeList.addItem(GeneralComunicator.i18nExtension("openmeetings.room.type.restricted"),
					String.valueOf(GWTRoom.TYPE_RESTRICTED));
			typeList.addItem(GeneralComunicator.i18nExtension("openmeetings.room.type.interview"),
					String.valueOf(GWTRoom.TYPE_INTERVIEW));
			typeList.setStyleName("okm-Input");
		} else {
			Timer timer = new Timer() {
				@Override
				public void run() {
					firstTimeInitTypeList();
				}
			};
			timer.schedule(1000);
		}
	}
	
	/**
	 * reset
	 */
	public void reset() {
		name.setText("");
		typeList.setSelectedIndex(0); // By default creates restricted room
		participentList.setSelectedIndex(0);
		publicCheck.setValue(false);
		apointmentCheck.setValue(false);
		moderatedCheck.setValue(false);
		allowUserQuestionsCheck.setValue(false);
		audioOnlyCheck.setValue(false);
		waitForRecordingCheck.setValue(false);
		allowRecordingCheck.setValue(false);
		topBarCheck.setValue(true);
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		createRoomTitle.setTitle(GeneralComunicator.i18nExtension("openmeetings.room.create"));
		typeText.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.type") + "</b>");
		nameText.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.name") + "</b>");
		int selectedRoomType = typeList.getSelectedIndex();
		typeList.clear();
		if (GeneralComunicator.getWorkspace().isAdminRole()) {
			typeList.addItem(GeneralComunicator.i18nExtension("openmeetings.room.type.conference"),
					String.valueOf(GWTRoom.TYPE_CONFERENCE));
		}
		typeList.addItem(GeneralComunicator.i18nExtension("openmeetings.room.type.restricted"),
				String.valueOf(GWTRoom.TYPE_RESTRICTED));
		typeList.addItem(GeneralComunicator.i18nExtension("openmeetings.room.type.interview"),
				String.valueOf(GWTRoom.TYPE_INTERVIEW));
		typeList.setSelectedIndex(selectedRoomType);
		publicText.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.public") + "</b>");
		appointment.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.appointment") + "</b>");
		participentText.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.participent") + "</b>");
		moderated.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.moderated") + "</b>");
		allowUserQuestions.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.allowuserquestions")
				+ "</b>");
		audioOnly.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.only.audio") + "</b>");
		waitForRecording
				.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.waitforrecording") + "</b>");
		allowRecording.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.allow.recording") + "</b>");
		topBar.setHTML("<b>" + GeneralComunicator.i18nExtension("openmeetings.room.topbar.visible") + "</b>");
	}
	
	/**
	 * TitleWidget
	 */
	class TitleWidget extends HorizontalPanel {
		HTML title;
		
		/**
		 * TitleWidget
		 */
		public TitleWidget(String text) {
			super();
			
			title = new HTML("");
			setTitle(text);
			
			add(title);
			setCellHorizontalAlignment(title, HasAlignment.ALIGN_CENTER);
			setCellWidth(title, "22");
		}
		
		@Override
		public void setTitle(String text) {
			title.setHTML("<b>" + text.toUpperCase() + "</b>");
		}
	}
}