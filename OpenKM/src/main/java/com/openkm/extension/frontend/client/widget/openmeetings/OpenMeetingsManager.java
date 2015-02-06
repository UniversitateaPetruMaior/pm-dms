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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.extension.frontend.client.service.OKMOpenMeetingsService;
import com.openkm.extension.frontend.client.service.OKMOpenMeetingsServiceAsync;
import com.openkm.extension.frontend.client.widget.openmeetings.finddocument.FindDocumentSelectPopup;
import com.openkm.extension.frontend.client.widget.openmeetings.invite.InvitationPopup;
import com.openkm.frontend.client.bean.extension.GWTRoom;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * OpenMeetings
 * 
 * @author jllort
 * 
 */
public class OpenMeetingsManager extends Composite {
	private final OKMOpenMeetingsServiceAsync openMeetingsService = (OKMOpenMeetingsServiceAsync) GWT
			.create(OKMOpenMeetingsService.class);
	private static final int TAB_HEIGHT = 20;
	private final int NUMBER_OF_COLUMNS = 2;
	
	private TabLayoutPanel tabPanel;
	private RoomPanel conferenceRooms;
	private RoomPanel privateConferenceRooms;
	private RoomPanel restrictedRooms;
	private RoomPanel interviewRooms;
	private ManageRoom manageRoom;
	
	private Status conferenceStatus;
	private Status privateConferenceStatus;
	private Status restrictedStatus;
	private Status interviewStatus;
	
	public ScrollPanel scrollPanel;
	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;
	private List<Long> userLoggedRoom;
	
	private InvitationPopup invitationPopup;
	private FindDocumentSelectPopup findDocumentSelectPopup;
	
	/**
	 * OpenMeetingsManager
	 * 
	 * @param width
	 * @param height
	 */
	public OpenMeetingsManager(int width, int height) {
		userLoggedRoom = new ArrayList<Long>();
		invitationPopup = new InvitationPopup();
		invitationPopup.setWidth("400px");
		invitationPopup.setHeight("100px");
		invitationPopup.setStyleName("okm-Popup");
		findDocumentSelectPopup = new FindDocumentSelectPopup();
		findDocumentSelectPopup.setWidth("700px");
		findDocumentSelectPopup.setHeight("390px");
		findDocumentSelectPopup.setStyleName("okm-Popup");
		findDocumentSelectPopup.addStyleName("okm-DisableSelect");
		
		tabPanel = new TabLayoutPanel(TAB_HEIGHT, Unit.PX);
		tabPanel.setPixelSize(width, height);
		
		conferenceRooms = new RoomPanel("openmeetings.room.public.conference", true, false, false);
		privateConferenceRooms = new RoomPanel("openmeetings.room.private.conference", true, true, true);
		restrictedRooms = new RoomPanel("openmeetings.room.restricted", true, true, true);
		interviewRooms = new RoomPanel("openmeetings.room.interview", true, true, true);
		
		conferenceStatus = new Status(conferenceRooms);
		privateConferenceStatus = new Status(privateConferenceRooms);
		restrictedStatus = new Status(restrictedRooms);
		interviewStatus = new Status(interviewRooms);
		conferenceStatus.setStyleName("okm-StatusPopup");
		restrictedStatus.setStyleName("okm-StatusPopup");
		interviewStatus.setStyleName("okm-StatusPopup");
		
		manageRoom = new ManageRoom();
		
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();
		scrollPanel = new ScrollPanel(hPanel);
		
		vPanelLeft.add(conferenceRooms);
		vPanelLeft.add(privateConferenceRooms);
		vPanelLeft.add(restrictedRooms);
		vPanelLeft.add(interviewRooms);
		vPanelRight.add(manageRoom);
		
		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);
		
		tabPanel.add(scrollPanel, GeneralComunicator.i18nExtension("openmeetings.rooms"));
		
		hPanel.setHeight("100%");
		vPanelRight.setHeight("100%");
		
		initDeletePublicRooms();
		
		initWidget(tabPanel);
	}
	
	/**
	 * firstTimeInitDeletePublicRoom
	 */
	private void initDeletePublicRooms() {
		if (GeneralComunicator.getWorkspace() != null) {
			if (GeneralComunicator.getWorkspace().isAdminRole()) {
				conferenceRooms.setDelete(true);
			}
			reset(); // First time get rooms
		} else {
			Timer timer = new Timer() {
				@Override
				public void run() {
					initDeletePublicRooms();
				}
			};
			timer.schedule(1000);
		}
	}
	
	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		scrollPanel.setPixelSize(width, height - TAB_HEIGHT);
	}
	
	/**
	 * setWidth
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		int columnWidth = width / NUMBER_OF_COLUMNS;
		
		// Trying to distribute widgets on columns with max size
		conferenceRooms.setWidth(columnWidth);
		privateConferenceRooms.setWidth(columnWidth);
		restrictedRooms.setWidth(columnWidth);
		interviewRooms.setWidth(columnWidth);
		manageRoom.setWidth("" + columnWidth);
		manageRoom.setHeight("100%");
	}
	
	/**
	 * reset
	 */
	public void reset() {
		getRoomsPublic(conferenceRooms, GWTRoom.TYPE_CONFERENCE);
		getPrivateConferenceUserRooms(privateConferenceRooms);
		getRestrictedUserRooms(restrictedRooms);
		getInterviewUserRooms(interviewRooms);
	}
	
	/**
	 * getRoomsPublic
	 * 
	 * @param rp
	 * @param roomType
	 */
	private void getRoomsPublic(final RoomPanel rp, final int roomType) {
		conferenceStatus.setGetRooms();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.getRoomsPublic(SID, roomType, new AsyncCallback<List<GWTRoom>>() {
					@Override
					public void onSuccess(List<GWTRoom> result) {
						rp.addRooms(result);
						conferenceStatus.unsetGetRooms();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("getRoomsPublic", caught);
						conferenceStatus.unsetGetRooms();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				conferenceStatus.unsetGetRooms();
			}
		});
	}
	
	/**
	 * getRestrictedUserRooms
	 */
	private void getPrivateConferenceUserRooms(final RoomPanel rp) {
		privateConferenceStatus.setGetRooms();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.getPrivateConferenceUserRooms(SID, new AsyncCallback<List<GWTRoom>>() {
					@Override
					public void onSuccess(List<GWTRoom> result) {
						rp.addRooms(result);
						privateConferenceStatus.unsetGetRooms();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("getPrivateConferenceUserRooms", caught);
						privateConferenceStatus.unsetGetRooms();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				privateConferenceStatus.unsetGetRooms();
			}
		});
	}
	
	/**
	 * getRestrictedUserRooms
	 */
	private void getRestrictedUserRooms(final RoomPanel rp) {
		restrictedStatus.setGetRooms();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.getRestrictedUserRooms(SID, new AsyncCallback<List<GWTRoom>>() {
					@Override
					public void onSuccess(List<GWTRoom> result) {
						rp.addRooms(result);
						restrictedStatus.unsetGetRooms();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("getRestrictedUserRooms", caught);
						restrictedStatus.unsetGetRooms();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				restrictedStatus.unsetGetRooms();
			}
		});
	}
	
	/**
	 * getInterviewUserRooms
	 */
	private void getInterviewUserRooms(final RoomPanel rp) {
		interviewStatus.setGetRooms();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.getInterviewUserRooms(SID, new AsyncCallback<List<GWTRoom>>() {
					@Override
					public void onSuccess(List<GWTRoom> result) {
						rp.addRooms(result);
						interviewStatus.unsetGetRooms();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("getInterviewUserRooms", caught);
						interviewStatus.unsetGetRooms();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				interviewStatus.unsetGetRooms();
			}
		});
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		invitationPopup.langRefresh();
		findDocumentSelectPopup.langRefresh();
		conferenceRooms.langRefresh();
		privateConferenceRooms.langRefresh();
		restrictedRooms.langRefresh();
		interviewRooms.langRefresh();
		manageRoom.langRefresh();
		int selecteTab = tabPanel.getSelectedIndex();
		List<Widget> widgetList = new ArrayList<Widget>();
		// Save widgets
		for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
			widgetList.add(tabPanel.getWidget(i));
		}
		tabPanel.clear();
		// Restores widgets
		for (Widget widget : widgetList) {
			if (widget instanceof ScrollPanel) {
				tabPanel.add(widget, GeneralComunicator.i18nExtension("openmeetings.rooms"));
			} else if (widget instanceof TabRoom) {
				TabRoom tabRoom = (TabRoom) widget;
				tabPanel.add(tabRoom, tabRoom.getRoomName());
				;
			}
		}
		tabPanel.selectTab(selecteTab);
	}
	
	/**
	 * enterPublicRoom
	 * 
	 * @param roomId
	 */
	public void enterPublicRoom(final String roomName, final long roomId) {
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				String lang = GeneralComunicator.getLang();
				boolean moderator = GeneralComunicator.getWorkspace().isAdminRole();
				boolean showAudioVideoTest = true;
				openMeetingsService.getPublicRoomURL(SID, roomId, moderator, showAudioVideoTest, lang,
						new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								TabRoom tabRoom = new TabRoom(result, roomName, roomId);
								tabPanel.add(tabRoom, roomName);
								tabPanel.selectTab(tabRoom); // Show actual view
								userLoggedRoom.add(new Long(roomId));
								reset();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("getPublicRoomURL", caught);
							}
						});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
			}
		});
	}
	
	/**
	 * createNewRoom
	 */
	public void createNewRoom(final String name, final long roomType, final long numberOfPartizipants,
			final boolean isPublic, final boolean appointment, final boolean moderated,
			final boolean allowUserQuestions, final boolean audioOnly, final boolean waitForRecording,
			final boolean allowRecording, final boolean topBar) {
		OpenMeetings.get().generalStatus.setCreateRoom();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.createNewRoom(SID, name, roomType, numberOfPartizipants, isPublic, appointment,
						moderated, allowUserQuestions, audioOnly, waitForRecording, allowRecording, topBar,
						new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								reset();
								OpenMeetings.get().generalStatus.unsetCreateRoom();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("createNewRoom", caught);
								OpenMeetings.get().generalStatus.unsetCreateRoom();
							}
						});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				OpenMeetings.get().generalStatus.unsetCreateRoom();
			}
		});
	}
	
	/**
	 * deleteRoom
	 * 
	 * @param roomId
	 */
	public void deleteRoom(final long roomId) {
		OpenMeetings.get().generalStatus.setDeleteRoom();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.deleteRoom(SID, roomId, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						// Remove tabPanel if user is connected
						for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
							if (tabPanel.getWidget(i) instanceof TabRoom) {
								TabRoom tabRoom = (TabRoom) tabPanel.getWidget(i);
								if (tabRoom.getRoomId() == roomId) {
									tabPanel.remove(i);
									break;
								}
							}
						}
						reset();
						OpenMeetings.get().generalStatus.unsetDeleteRoom();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("deleteRoom", caught);
						OpenMeetings.get().generalStatus.unsetDeleteRoom();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				OpenMeetings.get().generalStatus.unsetDeleteRoom();
			}
		});
	}
	
	/**
	 * sendInvitation
	 */
	public void sendInvitation(final long roomId, final String users, final String roles, final String subject,
			final String message) {
		OpenMeetings.get().generalStatus.setSendInvitation();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.sendInvitation(SID, roomId, users, roles, subject, message,
						GeneralComunicator.getLang(), new AsyncCallback<Object>() {
							@Override
							public void onSuccess(Object result) {
								OpenMeetings.get().generalStatus.unsetSendInvitation();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								GeneralComunicator.showError("sendInvitation", caught);
								OpenMeetings.get().generalStatus.unsetSendInvitation();
							}
						});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				OpenMeetings.get().generalStatus.unsetSendInvitation();
			}
		});
	}
	
	/**
	 * executeInviteUsers
	 */
	public void executeInviteUsers(long roomId) {
		invitationPopup.executeSendInvitation(roomId);
	}
	
	/**
	 * addDocumentToRoom
	 */
	public void addDocumentToRoom(final long roomId, final String path) {
		OpenMeetings.get().generalStatus.setAddDocument();
		openMeetingsService.loginUser(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				String SID = result;
				openMeetingsService.addDocumentToRoom(SID, roomId, path, new AsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						OpenMeetings.get().generalStatus.unsetAddDocument();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GeneralComunicator.showError("addDocumentToRoom", caught);
						OpenMeetings.get().generalStatus.unsetAddDocument();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GeneralComunicator.showError("loginUser", caught);
				OpenMeetings.get().generalStatus.unsetAddDocument();
			}
		});
	}
	
	/**
	 * executeFindDocument
	 */
	public void executeFindDocument(long roomId) {
		findDocumentSelectPopup.show(roomId);
	}
	
	/**
	 * closeLoggedRoom
	 * 
	 * @param roomId
	 */
	public void closeLoggedRoom(long roomId) {
		// Removing from logged list
		for (Long id : userLoggedRoom) {
			if (id.longValue() == roomId) {
				userLoggedRoom.remove(id);
				break;
			}
		}
		// Remove tabPanel if user is connected
		for (int i = 0; i < tabPanel.getWidgetCount(); i++) {
			if (tabPanel.getWidget(i) instanceof TabRoom) {
				TabRoom tabRoom = (TabRoom) tabPanel.getWidget(i);
				if (tabRoom.getRoomId() == roomId) {
					tabPanel.remove(i);
					break;
				}
			}
		}
		reset();
	}
	
	/**
	 * isUserLooged
	 */
	public boolean isUserLooged(long roomId) {
		boolean found = false;
		for (Long id : userLoggedRoom) {
			if (id.longValue() == roomId) {
				found = true;
				break;
			}
		}
		return found;
	}
}