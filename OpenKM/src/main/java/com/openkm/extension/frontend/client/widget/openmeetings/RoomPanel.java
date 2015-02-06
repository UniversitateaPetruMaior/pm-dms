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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.extension.GWTRoom;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * RoomPanel
 * 
 * @author jllort
 * 
 */
public class RoomPanel extends Composite {
	private static int HEADER_SQUARE = 24;
	private static int SEPARATOR_HEIGHT = 20;
	private static int SEPARATOR_WIDTH = 20;
	
	private VerticalPanel vPanel;
	private SimplePanel spTop;
	private HorizontalPanel hPanel;
	private SimplePanel spLeft;
	private VerticalPanel vCenterPanel;
	private SimplePanel spRight;
	private Header header;
	private SimplePanel panelData;
	private FlexTable table;
	private Image zoomImage;
	private String headerTextKey;
	private boolean zoom = false;
	private boolean flagZoom = true;
	private boolean delete = false;
	private boolean invitate = false;
	
	/**
	 * RoomPanel
	 */
	public RoomPanel(String headerTextKey, boolean zoom, boolean delete, boolean invitate) {
		spTop = new SimplePanel();
		spLeft = new SimplePanel();
		spRight = new SimplePanel();
		panelData = new SimplePanel();
		table = new FlexTable();
		vCenterPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		header = new Header(zoom);
		vPanel = new VerticalPanel();
		this.headerTextKey = headerTextKey;
		this.delete = delete;
		this.invitate = invitate;
		
		// Sets or unsets visible table
		table.setVisible(zoom);
		
		header.setHeaderText(GeneralComunicator.i18nExtension(headerTextKey));
		
		panelData.add(table);
		
		vCenterPanel.add(header);
		vCenterPanel.add(panelData);
		
		hPanel.add(spLeft);
		hPanel.add(vCenterPanel);
		hPanel.add(spRight);
		
		vPanel.add(spTop);
		vPanel.add(hPanel);
		
		spTop.setHeight("" + SEPARATOR_HEIGHT);
		spLeft.setWidth("" + SEPARATOR_WIDTH);
		spRight.setWidth("" + SEPARATOR_WIDTH);
		
		vPanel.setStyleName("okm-DashboardWidget ");
		panelData.setStyleName("data");
		table.setStyleName("okm-NoWrap");
		
		panelData.setWidth("99.6%");
		header.setWidth("100%");
		
		table.setCellPadding(0);
		table.setCellSpacing(0);
		
		vPanel.addStyleName("okm-DisableSelect");
		
		initWidget(vPanel);
	}
	
	/**
	 * setDelete
	 * 
	 * @param delete
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	
	/**
	 * addRooms
	 * 
	 * @param rooms
	 */
	public void addRooms(List<GWTRoom> rooms) {
		table.removeAllRows();
		for (final GWTRoom room : rooms) {
			int row = table.getRowCount();
			table.setWidget(row, 0, new Image(OKMBundleResources.INSTANCE.roomIn()));
			// Delete
			if (delete) {
				Image delete = new Image(OKMBundleResources.INSTANCE.delete());
				delete.setTitle(GeneralComunicator.i18n("button.delete"));
				delete.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						OpenMeetings.get().toolBarBoxOpenMeeting.manager.deleteRoom(room.getId());
					}
				});
				delete.setStyleName("okm-Hyperlink");
				table.setWidget(row, 1, delete);
			} else {
				table.setHTML(row, 1, "");
			}
			// Invitation
			if (invitate) {
				Image inviteUsers = new Image(OKMBundleResources.INSTANCE.user());
				inviteUsers.setTitle(GeneralComunicator.i18nExtension("openmeetings.invitate.users.title"));
				inviteUsers.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						OpenMeetings.get().toolBarBoxOpenMeeting.manager.executeInviteUsers(room.getId());
					}
				});
				inviteUsers.setStyleName("okm-Hyperlink");
				table.setWidget(row, 2, inviteUsers);
			} else {
				table.setHTML(row, 2, "");
			}
			// Logged
			boolean logged = OpenMeetings.get().toolBarBoxOpenMeeting.manager.isUserLooged(room.getId());
			if (logged) {
				Image closeRoom = new Image(OKMBundleResources.INSTANCE.roomClose());
				closeRoom.setTitle(GeneralComunicator.i18n("button.close"));
				closeRoom.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						OpenMeetings.get().toolBarBoxOpenMeeting.manager.closeLoggedRoom(room.getId());
					}
				});
				closeRoom.setStyleName("okm-Hyperlink");
				table.setWidget(row, 3, closeRoom);
			} else {
				table.setWidget(row, 3, new HTML(""));
			}
			// Add file ( delete indicates user owner grants )
//			if (delete) {
//				Image addFile = new Image(OKMBundleResources.INSTANCE.roomAddFile());
//				addFile.setTitle(GeneralComunicator.i18nExtension("openmeetings.add.file"));
//				addFile.addClickHandler(new ClickHandler() {
//					@Override
//					public void onClick(ClickEvent event) {
//						OpenMeetings.get().toolBarBoxOpenMeeting.manager.executeFindDocument(room.getId());
//					}
//				});
//				addFile.setStyleName("okm-Hyperlink");
//				table.setWidget(row, 4, addFile);
//			} else {
//				table.setWidget(row, 4, new HTML(""));
//			}
			table.setWidget(row, 4, new HTML(""));
			// Name
			Anchor roomName = new Anchor();
			roomName.setText(room.getName());
			roomName.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Enter room only if user is not yet logged into
					if (!OpenMeetings.get().toolBarBoxOpenMeeting.manager.isUserLooged(room.getId())) {
						OpenMeetings.get().toolBarBoxOpenMeeting.manager.enterPublicRoom(room.getName(), room.getId());
					}
				}
			});
			roomName.setStyleName("okm-Hyperlink");
			table.setWidget(row, 5, roomName);
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			table.setHTML(row, 6, dtf.format(room.getStart()));
			table.getCellFormatter().setWidth(row, 0, "20");
			if (delete) {
				table.getCellFormatter().setWidth(row, 1, "20");
			}
			if (invitate) {
				table.getCellFormatter().setWidth(row, 2, "20");
			}
			if (logged) {
				table.getCellFormatter().setWidth(row, 3, "20");
			}
//			if (delete) {
//				table.getCellFormatter().setWidth(row, 4, "20");
//			}
			table.getCellFormatter().setWidth(row, 6, "100%"); // Table sets de 100% of space
			table.getCellFormatter().setHorizontalAlignment(row, 6, HasAlignment.ALIGN_RIGHT);
		}
		header.setNumberOfRooms(rooms.size());
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		header.setHeaderText(GeneralComunicator.i18nExtension(headerTextKey));
		for (int i=0; i<table.getRowCount(); i++) {
			if (delete) {
				Image delete = (Image) table.getWidget(i, 1);
				delete.setTitle(GeneralComunicator.i18n("button.delete"));
			}
			if (invitate) {
				Image inviteUsers = (Image) table.getWidget(i, 2);
				inviteUsers.setTitle(GeneralComunicator.i18nExtension("openmeetings.invitate.users.title"));
			}
			if (table.getWidget(i, 3) instanceof Image) {
				Image closeRoom = (Image) table.getWidget(i, 3);
				closeRoom.setTitle(GeneralComunicator.i18n("button.close"));
			}
//			if (delete) {
//				Image addFile = (Image) table.getWidget(i, 4);
//				addFile.setTitle(GeneralComunicator.i18nExtension("openmeetings.add.file"));
//			}
		}
	}
	
	/**
	 * setWidth
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		vCenterPanel.setWidth("" + (width - 2 * SEPARATOR_WIDTH));
	}
	
	/**
	 * Header
	 */
	private class Header extends HorizontalPanel implements HasClickHandlers {
		private SimplePanel spLeft;
		private SimplePanel spRight;
		private SimplePanel iconImagePanel;
		private HorizontalPanel center;
		private HorizontalPanel titlePanel;
		private HTML headerText;
		private HTML numberOfRooms;
		private Image iconImage;
		
		/**
		 * Header
		 */
		public Header(boolean visible) {
			super();
			sinkEvents(Event.ONCLICK);
			iconImage = new Image(OKMBundleResources.INSTANCE.room());
			zoom = visible;
			
			if (zoom) {
				zoomImage = new Image(OKMBundleResources.INSTANCE.zoomOut());
			} else {
				zoomImage = new Image(OKMBundleResources.INSTANCE.zoomIn());
			}
			
			zoomImage.setStyleName("okm-Hyperlink");
			
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (flagZoom) {
						zoom = !zoom;
						table.setVisible(zoom);
						
						if (zoom) {
							zoomImage.setResource(OKMBundleResources.INSTANCE.zoomOut());
						} else {
							zoomImage.setResource(OKMBundleResources.INSTANCE.zoomIn());
						}
					} else {
						flagZoom = true;
					}
				}
			});
			
			setHeight("" + HEADER_SQUARE);
			
			spLeft = new SimplePanel();
			spRight = new SimplePanel();
			iconImagePanel = new SimplePanel();
			center = new HorizontalPanel();
			titlePanel = new HorizontalPanel();
			headerText = new HTML("");
			numberOfRooms = new HTML("");
			
			iconImagePanel.add(iconImage);
			
			titlePanel.add(headerText);
			titlePanel.add(numberOfRooms);
			
			center.add(iconImagePanel);
			center.add(titlePanel);
			center.add(zoomImage);
			
			spLeft.setSize("" + HEADER_SQUARE, "" + HEADER_SQUARE);
			center.setWidth("100%");
			center.setCellVerticalAlignment(iconImagePanel, HasAlignment.ALIGN_MIDDLE);
			center.setCellHorizontalAlignment(zoomImage, HasAlignment.ALIGN_RIGHT);
			center.setCellVerticalAlignment(titlePanel, HasAlignment.ALIGN_MIDDLE);
			center.setCellVerticalAlignment(zoomImage, HasAlignment.ALIGN_MIDDLE);
			center.setCellWidth(iconImagePanel, "22");
			center.setCellWidth(zoomImage, "16");
			center.setHeight("100%");
			spRight.setSize("" + HEADER_SQUARE, "" + HEADER_SQUARE);
			
			titlePanel.setCellVerticalAlignment(numberOfRooms, HasAlignment.ALIGN_MIDDLE);
			titlePanel.setCellHorizontalAlignment(numberOfRooms, HasAlignment.ALIGN_LEFT);
			
			add(spLeft);
			add(center);
			add(spRight);
			
			spLeft.setStyleName("topLeft");
			center.setStyleName("topCenter");
			spRight.setStyleName("topRight");
			
			setCellWidth(spLeft, "" + HEADER_SQUARE);
			setCellWidth(spRight, "" + HEADER_SQUARE);
			setCellVerticalAlignment(center, HasAlignment.ALIGN_MIDDLE);
		}
		
		/**
		 * setHeaderText
		 */
		public void setHeaderText(String text) {
			headerText.setHTML(text);
		}
		
		/**
		 * setNumberOfRooms
		 */
		public void setNumberOfRooms(int value) {
			numberOfRooms.setHTML("&nbsp;&nbsp;(" + value + ")&nbsp;&nbsp;");
		}
		
		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addHandler(handler, ClickEvent.getType());
		}
	}
	
	/**
	 * Unsets the refreshing
	 */
	public void unsetRefreshing() {
		// status.unsetFlag_getDashboard();
	}
}