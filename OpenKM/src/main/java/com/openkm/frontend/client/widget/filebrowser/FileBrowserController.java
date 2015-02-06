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

package com.openkm.frontend.client.widget.filebrowser;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPaginated;
import com.openkm.frontend.client.bean.GWTProfilePagination;
import com.openkm.frontend.client.bean.form.GWTValidator;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.util.validator.ValidatorBuilder;
import com.openkm.frontend.client.widget.searchin.CalendarWidget;

import eu.maydu.gwt.validation.client.DefaultValidationProcessor;
import eu.maydu.gwt.validation.client.ValidationProcessor;
import eu.maydu.gwt.validation.client.actions.FocusAction;

/**
 * FileBrowserController
 * 
 * @author jllort
 *
 */
public class FileBrowserController extends Composite {
	private static final int CALENDAR_FIRED_NONE = -1;
	private static final int CALENDAR_FIRED_START = 0;
	private static final int CALENDAR_FIRED_END = 1;
	
	private HorizontalPanel hPanel;
	private Image folder;
	private Image document;
	private Image mail;
	private HTML vSeparator;
	private HTML vSeparator2;
	private ListBox rowsLimit; 
	private Controller controller;
	private CheckBox paginated;
	private HTML total;
	private HTML pageInfo;
	private HTML paginate;
	private Image gotoStart;
	private Image previous;
	private Image next;
	private Image gotoEnd;
	private HTML orderByText;
	private ListBox orderBy; 
	private HTML reverseText;
	private CheckBox reverse;
	private Image filter;
	private FilterPopup fPopup;
	private boolean previousEnabled = false;
	private boolean nextEnabled = false;
	private boolean showPaginated = false;
	private ValidationProcessor validationProcessor;
	private int calendarFired = CALENDAR_FIRED_NONE;
	
	/**
	 * FileBrowserController
	 */
	public FileBrowserController() {
		hPanel = new HorizontalPanel();
		
		// Folder
		folder = new Image(OKMBundleResources.INSTANCE.folder());
		folder.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (controller.isShowFolder()) {
					folder.setResource(OKMBundleResources.INSTANCE.folderDisabled());
					controller.setShowFolders(false);
				} else {
					folder.setResource(OKMBundleResources.INSTANCE.folder());
					controller.setShowFolders(true);
				}
				controller.setOffset(0);
				refreshFileBrowser();
			}
		});
		folder.setStyleName("okm-Hyperlink");
		// Document
		document = new Image(OKMBundleResources.INSTANCE.document());
		document.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (controller.isShowDocuments()) {
					document.setResource(OKMBundleResources.INSTANCE.documentDisabled());
					controller.setShowDocuments(false);
				} else {
					document.setResource(OKMBundleResources.INSTANCE.document());
					controller.setShowDocuments(true);
				}
				controller.setOffset(0);
				refreshFileBrowser();
			}
		});
		document.setStyleName("okm-Hyperlink");
		// Mail
		mail = new Image(OKMBundleResources.INSTANCE.mail());
		mail.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (controller.isShowMails()) {
					mail.setResource(OKMBundleResources.INSTANCE.mailDisabled());
					controller.setMails(false);
				} else {
					mail.setResource(OKMBundleResources.INSTANCE.mail());
					controller.setMails(true);
				}
				controller.setOffset(0);
				refreshFileBrowser();
			}
		});
		mail.setStyleName("okm-Hyperlink");
		
		// Paginate
		paginated = new CheckBox();
		paginated.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.setPaginated(paginated.getValue());
				rowsLimit.setVisible(paginated.getValue());
				total.setVisible(paginated.getValue());
				gotoStart.setVisible(paginated.getValue());
				previous.setVisible(paginated.getValue());
				pageInfo.setVisible(paginated.getValue());
				next.setVisible(paginated.getValue());
				gotoEnd.setVisible(paginated.getValue());
				orderByText.setVisible(paginated.getValue());
				orderBy.setVisible(paginated.getValue());
				reverseText.setVisible(paginated.getValue());
				reverse.setVisible(paginated.getValue());
				refreshFileBrowser();
			}
		});
		
		// Rows limit
		rowsLimit = new ListBox();
		rowsLimit.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				controller.setOffset(0);
				refreshFileBrowser();
			}
		});
		rowsLimit.setStyleName("okm-Input");
		
		// images
		gotoStart = new Image(OKMBundleResources.INSTANCE.gotoStart());
		previous = new Image(OKMBundleResources.INSTANCE.previousDisabled());
		next = new Image(OKMBundleResources.INSTANCE.nextDisabled());
		gotoEnd = new Image(OKMBundleResources.INSTANCE.gotoEnd());
		
		gotoStart.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (previousEnabled) {
					controller.setOffset(0);
					refreshFileBrowser();
				}
			}
		});
		gotoStart.setStyleName("okm-Hyperlink");
		
		previous.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (previousEnabled) {
					controller.setOffset(getOffset()-getLimit());
					refreshFileBrowser();
				}
			}
		});
		previous.setStyleName("okm-Hyperlink");
		
		next.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (nextEnabled) {
					controller.setOffset(getOffset()+getLimit());
					refreshFileBrowser();
				}
			}
		});
		next.setStyleName("okm-Hyperlink");
		
		gotoEnd.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (nextEnabled) {
					controller.setOffset(controller.getTotal()+1);
					refreshFileBrowser();
				}
			}
		});
		gotoEnd.setStyleName("okm-Hyperlink");
		
		//  Text
		total = new HTML(Main.i18n("filebrowser.controller.total") +": 0" );
		pageInfo = new HTML("0" + "&nbsp;" + Main.i18n("filebrowser.controller.to") + "&nbsp;" + "0");
		paginate = new HTML(Main.i18n("filebrowser.controller.paginate"));
		
		// Ordering
		orderByText = new HTML(Main.i18n("filebrowser.controller.orderby") + "&nbsp;");
		orderBy = new ListBox();
		orderBy.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refreshFileBrowser();
			}
		});
		orderBy.setStyleName("okm-Input");
		
		// Reverse 
		reverseText = new HTML(Main.i18n("filebrowser.controller.reverse") + "&nbsp;");
		reverse = new CheckBox();
		reverse.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.setReverse(reverse.getValue());
				refreshFileBrowser();
			}
		});
		
		// Filter 
		fPopup = new FilterPopup();
		fPopup.setWidth("150px");
		fPopup.setHeight("10px");
		fPopup.setStyleName("okm-Popup");
		filter = new Image(OKMBundleResources.INSTANCE.filter());
		filter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fPopup.setPopupPosition(event.getClientX(), event.getClientY());
				fPopup.reset();
				fPopup.show();
			}
		});
		filter.setStyleName("okm-Hyperlink");
		
		hPanel.setStyleName("gwt-controller");
		hPanel.setHeight("22");
		hPanel.setWidth("100%");
		
		initWidget(hPanel);
	}
	
	/**
	 * refreshFileBrowser
	 */
	private void refreshFileBrowser() {
		Main.get().mainPanel.desktop.browser.fileBrowser.refreshOnlyFileBrowser();
	}
	
	
	/**
	 * getController
	 * 
	 * @return
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * setController
	 * 
	 * @param controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
		if (controller.isShowFolder()) {
			folder.setResource(OKMBundleResources.INSTANCE.folder());
		} else {
			folder.setResource(OKMBundleResources.INSTANCE.folderDisabled());
		}
		if (controller.isShowDocuments()) {
			document.setResource(OKMBundleResources.INSTANCE.document());
		} else {
			document.setResource(OKMBundleResources.INSTANCE.documentDisabled());
		}
		if (controller.isShowMails()) {
			mail.setResource(OKMBundleResources.INSTANCE.mail());
		} else {
			mail.setResource(OKMBundleResources.INSTANCE.mailDisabled());
		}
		rowsLimit.setSelectedIndex(controller.getSelectedRowsLimit());
		orderBy.setSelectedIndex(controller.getSelectedOrderBy());;
		total.setText(String.valueOf(controller.getTotal()));
		paginated.setValue(controller.isPaginated());
		fPopup.setMapFilter(controller.getMapFilter());
	}
	
	/**
	 * getLimit
	 * 
	 * @return
	 */
	public int getLimit() {
		return Integer.parseInt(rowsLimit.getValue(rowsLimit.getSelectedIndex()));
	}
	
	/**
	 * setPageList
	 * 
	 * @param pageList
	 */
	public void setPageList(String pageList) {
		rowsLimit.clear();
		String page[] = pageList.split(";");
		for (String value : page) {
			rowsLimit.addItem(value, value);
		}
	}
	

	
	/**
	 * getOffset
	 * 
	 * @return
	 */
	public int getOffset() {
		return controller.getOffset();
	}
	
	/**
	 * setOffset
	 * 
	 * @return
	 */
	public void setOffset(int offset) {
		controller.setOffset(offset);
	}
	
	/**
	 * updateTotal
	 * 
	 * @param total
	 */
	public void updateTotal(int total) {
		controller.setTotal(total);
		this.total.setText(String.valueOf(total));
	}
	
	/**
	 * isPaginated
	 * 
	 * @return
	 */
	public boolean isPaginated() {
		return controller.isPaginated();
	}
	
	/**
	 * addOrderByItem
	 * 
	 * @param item
	 * @param value
	 */
	public void addOrderByItem(String item, String value) {
		orderBy.addItem(item, value);
		fPopup.addItem(item, value);
	}
	
	/**
	 * clearOrderBy
	 */
	public void clearOrderBy() {
		orderBy.clear();
	}
	
	/**
	 * getSelectedOrderBy
	 * 
	 * @return
	 */
	public int getSelectedOrderBy() {
		return Integer.parseInt(orderBy.getValue(orderBy.getSelectedIndex()));
	}
	
	/**
	 * isReverse
	 * 
	 * @return
	 */
	public boolean isReverse() {
		return controller.isReverse();
	}
	
	/**
	 * refresh
	 */
	public void refresh() {
		if (controller.isPaginated()) {
			if (controller.getTotal()==0) {
				nextEnabled = previousEnabled = false;
			} else {
				nextEnabled = (getOffset()+getLimit()<controller.getTotal());
				previousEnabled = (getOffset()>0);
			}
		}
		total.setHTML(Main.i18n("filebrowser.controller.total") +": " + controller.getTotal());
		rowsLimit.setVisible(showPaginated);
		total.setVisible(showPaginated);
		orderByText.setVisible(showPaginated);
		orderBy.setVisible(showPaginated);
		reverseText.setVisible(showPaginated);
		reverse.setVisible(showPaginated);
		// Hide in case total = 0
		gotoStart.setVisible((controller.getTotal()!=0) && showPaginated);
		previous.setVisible((controller.getTotal()!=0) && showPaginated);
		pageInfo.setVisible((controller.getTotal()!=0) && showPaginated);
		next.setVisible((controller.getTotal()!=0) && showPaginated);
		gotoEnd.setVisible((controller.getTotal()!=0) && showPaginated);
		if (nextEnabled) {
			pageInfo.setHTML((getOffset()+1) + "&nbsp;" + Main.i18n("filebrowser.controller.to") + "&nbsp;" + (getOffset()+getLimit()));
		} else {
			pageInfo.setHTML((getOffset()+1) + "&nbsp;" + Main.i18n("filebrowser.controller.to") + "&nbsp;" + controller.getTotal());
		}
		evaluateIcons();
	}
	
	/**
	 * refreshChangeView
	 */
	public void refreshChangeView() {
		paginated.setValue(controller.isPaginated());
		if (!controller.isPaginated()) {
			rowsLimit.setVisible(false);
			total.setVisible(false);
			gotoStart.setVisible(false);
			previous.setVisible(false);
			pageInfo.setVisible(false);
			next.setVisible(false);
			gotoEnd.setVisible(false);
			orderByText.setVisible(false);
			orderBy.setVisible(false);
			reverseText.setVisible(false);
			reverse.setVisible(false);
		}
	}
	
	/**
	 * cleanAllByOpenFolderPath
	 */
	public void cleanAllByOpenFolderPath() {
		folder.setResource(OKMBundleResources.INSTANCE.folder()); 
		controller.setShowFolders(true);
		document.setResource(OKMBundleResources.INSTANCE.document());
		controller.setShowDocuments(true);
		mail.setResource(OKMBundleResources.INSTANCE.mail());
		controller.setMails(true);
		rowsLimit.setSelectedIndex(0);
		orderBy.setSelectedIndex(0);
		controller.setReverse(false);
		reverse.setValue(false);
		total.setText("");
		controller.setOffset(0);
		for (String key : controller.getMapFilter().keySet()) { // Reset all filter values
			GWTFilter filter = controller.getMapFilter().get(key);
			filter.setFilterValue1("");
			filter.setFilterValue2("");
			filter.setSizeType1(0);
			filter.setSizeType2(0);
			filter.setFrom(null);
			filter.setTo(null);
		}
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		orderByText.setHTML(Main.i18n("filebrowser.controller.orderby") + "&nbsp;");
		reverseText.setHTML(Main.i18n("filebrowser.controller.reverse") + "&nbsp;");
		fPopup.langRefresh();
		refresh();
	}
	
	/**
	 * Evaluate icons image
	 */
	private void evaluateIcons(){
		if (previousEnabled) {
			gotoStart.setResource(OKMBundleResources.INSTANCE.gotoStart());
			previous.setResource(OKMBundleResources.INSTANCE.previous());
		} else {
			gotoStart.setResource(OKMBundleResources.INSTANCE.gotoStartDisabled());
			previous.setResource(OKMBundleResources.INSTANCE.previousDisabled());
		}
		
		if (nextEnabled) {
			next.setResource(OKMBundleResources.INSTANCE.next());
			gotoEnd.setResource(OKMBundleResources.INSTANCE.gotoEnd());
		} else {
			next.setResource(OKMBundleResources.INSTANCE.nextDisabled());
			gotoEnd.setResource(OKMBundleResources.INSTANCE.gotoEndDisabled());
		}
	}
	
	/**
	 * isFolder
	 * 
	 * @return
	 */
	public boolean isShowFolder() {
		return controller.isShowFolder();
	}
	
	/**
	 * isDocument
	 * 
	 * @return
	 */
	public boolean isShowDocument() {
		return controller.isShowDocuments();
	}
	
	/**
	 * isMail
	 * 
	 * @return
	 */
	public boolean isShowMail() {
		return controller.isShowMails();
	}
	
	/**
	 * getSelectedRowId
	 * 
	 * @return
	 */
	public String getSelectedRowId() {
		return controller.getSelectedRowId();
	}

	/**
	 * setSelectedRowId
	 * 
	 * @param selectedRowId
	 */
	public void setSelectedRowId(String selectedRowId) {
		controller.setSelectedRowId(selectedRowId);
	}
	
	/**
	 * setProfilePagination
	 * 
	 * @param profilePagination
	 */
	public void setProfilePagination(GWTProfilePagination profilePagination) {
		if (profilePagination.isTypeFilterEnabled()) {
			hPanel.add(Util.hSpace("5"));
			hPanel.add(folder);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(document);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(mail);
			hPanel.add(Util.hSpace("5"));
			hPanel.setCellVerticalAlignment(folder, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(document, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(mail, HasAlignment.ALIGN_MIDDLE);
			if (profilePagination.isMiscFilterEnabled() || profilePagination.isPaginationEnabled()) {
				vSeparator = new HTML();
				vSeparator.setHeight("22");
				vSeparator.setStyleName("separator");
				hPanel.add(vSeparator);
			}
		}
		if (profilePagination.isMiscFilterEnabled()) {
			hPanel.add(Util.hSpace("5"));
			hPanel.add(filter);
			hPanel.add(Util.hSpace("5"));
			hPanel.setCellVerticalAlignment(filter, HasAlignment.ALIGN_MIDDLE);
			if (profilePagination.isPaginationEnabled()) {
				vSeparator2 = new HTML();
				vSeparator2.setHeight("22");
				vSeparator2.setStyleName("separator");
				hPanel.add(vSeparator2);
			}
		}
		showPaginated = profilePagination.isPaginationEnabled();
		if (profilePagination.isPaginationEnabled()) {
			hPanel.add(paginated);
			hPanel.add(paginate);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(rowsLimit);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(total);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(gotoStart);
			hPanel.add(previous);
			hPanel.add(pageInfo);
			hPanel.add(next);
			hPanel.add(gotoEnd);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(orderByText);
			hPanel.add(orderBy);
			hPanel.add(Util.hSpace("5"));
			hPanel.add(reverseText);
			hPanel.add(reverse);
			hPanel.setCellVerticalAlignment(paginated, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(paginate, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(rowsLimit, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(total, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(gotoStart, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(previous, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(pageInfo, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(next, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(gotoEnd, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(orderByText, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(orderBy, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(reverseText, HasAlignment.ALIGN_MIDDLE);
			hPanel.setCellVerticalAlignment(reverse, HasAlignment.ALIGN_MIDDLE);
		}
		
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.setCellWidth(space, "100%");
	}
	
	/**
	 * getFilter
	 * 
	 * @return
	 */
	public Map<String, GWTFilter> getFilter() {
		Map<String, GWTFilter> map = new HashMap<String, GWTFilter>();
		// Only values with filter not empty
		for (String key : fPopup.getMapFilter().keySet()) {
			if (key.equals(String.valueOf(GWTPaginated.COL_NAME)) || key.equals(String.valueOf(GWTPaginated.COL_AUTHOR)) ||
				key.equals(String.valueOf(GWTPaginated.COL_VERSION))) {
				if (!fPopup.getMapFilter().get(key).getFilterValue1().equals("")) {
					GWTFilter filter = fPopup.getMapFilter().get(key);
					filter.setFilterValue1(filter.getFilterValue1().toLowerCase()); // Filter values always in lowercase
					map.put(key, filter);
				}
			} else if (key.equals(String.valueOf(GWTPaginated.COL_SIZE))) {
				GWTFilter filter = fPopup.getMapFilter().get(key);
				boolean add = false;
				if (!fPopup.getMapFilter().get(key).getFilterValue1().equals("")) {
					add = true;
					switch (filter.getSizeType1()) {
						case GWTFilter.SIZE_BYTES:
							filter.setSizeValue1(Integer.parseInt(filter.getFilterValue1()));
							break;
						case GWTFilter.SIZE_KB:
							filter.setSizeValue1(Integer.parseInt(filter.getFilterValue1())*1024);
							break;
						case GWTFilter.SIZE_MB:
							filter.setSizeValue1(Integer.parseInt(filter.getFilterValue1())*1048576);
							break;
						case GWTFilter.SIZE_GB:
							filter.setSizeValue1(Integer.parseInt(filter.getFilterValue1())*1073741824);
							break;
					}
				} else {
					fPopup.getMapFilter().get(key).setSizeValue1(-1);
				}
				if (!fPopup.getMapFilter().get(key).getFilterValue2().equals("")) {
					add = true;
					switch (filter.getSizeType2()) {
						case GWTFilter.SIZE_BYTES:
							filter.setSizeValue2(Integer.parseInt(filter.getFilterValue2()));
							break;
						case GWTFilter.SIZE_KB:
							filter.setSizeValue2(Integer.parseInt(filter.getFilterValue2())*1024);
							break;
						case GWTFilter.SIZE_MB:
							filter.setSizeValue2(Integer.parseInt(filter.getFilterValue2())*1048576);
							break;
						case GWTFilter.SIZE_GB:
							filter.setSizeValue2(Integer.parseInt(filter.getFilterValue2())*1073741824);
							break;
					}
				} else {
					fPopup.getMapFilter().get(key).setSizeValue2(-1);
				}
				if (add) {
					map.put(key, filter);
				}
			} else if (key.equals(String.valueOf(GWTPaginated.COL_DATE))) {
				GWTFilter filter = fPopup.getMapFilter().get(key);
				if (filter.getFrom()!=null || filter.getTo()!=null) {
					map.put(key, filter);
				}
			} 
		}		
		return map;
	}
	
	/**
	 * FilterPopup
	 * 
	 * @author jllort
	 *
	 */
	private class FilterPopup extends DialogBox {
		private VerticalPanel vPanel;
		private Map<String, GWTFilter> map;
		private FlexTable table;
		private Button accept;
		
		/**
		 * FilterPopup
		 */
		public FilterPopup() {
			// Establishes auto-close when click outside
			super(false, true);
			setText(Main.i18n("filebrowser.controller.filter"));
			
			map = new LinkedHashMap<String, GWTFilter>();
			vPanel = new VerticalPanel();
			
			// filter table 
			table = new FlexTable();
			table.setCellSpacing(0);
			table.setCellPadding(2);
			table.setStyleName("okm-NoWrap");
			
			// accept
			accept = new Button(Main.i18n("button.accept"));
			accept.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (validationProcessor.validate()) {
						updateMapFilter();
						hide();
					}
				}
			});
			accept.setStyleName("okm-YesButton");
			
			vPanel.add(table);
			vPanel.add(Util.vSpace("5"));
			vPanel.add(accept);
			vPanel.add(Util.vSpace("5"));
			vPanel.setCellHorizontalAlignment(table, HasAlignment.ALIGN_LEFT);
			vPanel.setCellHorizontalAlignment(accept, HasAlignment.ALIGN_CENTER);
			
			setWidget(vPanel);
		}
		
		/**
		 * reset
		 */
		public void reset() {
			validationProcessor = new DefaultValidationProcessor();
			FocusAction focusAction = new FocusAction();
			final Image cleanAll = new Image(OKMBundleResources.INSTANCE.deleteDisabled());
			cleanAll.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!isAllCleaned()) {
						cleanAll();
						cleanAll.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
					}
				}
			});
			cleanAll.setStyleName("okm-Hyperlink");
			table.removeAllRows();
			// Case not only order by none is enabled first row show clean all 
			if (map.keySet().size()>1) {
				int row = table.getRowCount();
				table.setHTML(row, 0, "");
				table.setHTML(row, 1, Main.i18n("filebrowser.controller.filter.clean"));
				table.setWidget(row, 2, cleanAll);
				table.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasAlignment.ALIGN_RIGHT);
			}
			for (String key : map.keySet()) {
				if (key.equals(String.valueOf(GWTPaginated.COL_NAME)) || key.equals(String.valueOf(GWTPaginated.COL_AUTHOR)) || 
					key.equals(String.valueOf(GWTPaginated.COL_VERSION))) {
					int row = table.getRowCount();
					GWTFilter filter = map.get(key);
					final TextBox input = new TextBox();
					final Image clean = new Image(OKMBundleResources.INSTANCE.delete());
					if (filter.getFilterValue1().equals("")) {
						clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
					}
					clean.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (input.getValue().length()>0) {
								input.setValue("");
								clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
							}
							evaluateCleanAll(cleanAll);
						}
					});
					clean.setStyleName("okm-Hyperlink");
					input.setValue(filter.getFilterValue1());
					input.addKeyUpHandler(new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							if (input.getValue().length()>0) {
								clean.setResource(OKMBundleResources.INSTANCE.delete());
							} else {
								clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
							}
							evaluateCleanAll(cleanAll);
						}
					});
					input.setStyleName("okm-Input");
					if(key.equals(String.valueOf(GWTPaginated.COL_VERSION))) {
						input.setWidth("70px");
					} else {
						input.setWidth("150px");
					}
					
					table.setHTML(row, 0, filter.getItem() + "&nbsp;");
					table.setWidget(row, 1, input);
					table.setWidget(row, 2, clean);
					table.setHTML(row, 3, key);
					table.getFlexCellFormatter().setVisible(row, 3, false);
					
				} else if (key.equals(String.valueOf(GWTPaginated.COL_SIZE))) {
					int row = table.getRowCount();
					GWTFilter filter = map.get(key);
					final Image clean = new Image(OKMBundleResources.INSTANCE.delete());
					if (filter.getFilterValue1().equals("") && filter.getFilterValue2().equals("")) {
						clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
					}
					HorizontalPanel hPanel = new HorizontalPanel();
					hPanel.setStyleName("okm-NoWrap");
					final TextBox input1 = new TextBox();
					input1.setWidth("50");
					input1.setStyleName("okm-Input");
					final ListBox sizeType1 = new ListBox();
					sizeType1.addItem("Bytes", String.valueOf(GWTFilter.SIZE_BYTES));
					sizeType1.addItem("KB", String.valueOf(GWTFilter.SIZE_KB));
					sizeType1.addItem("MB", String.valueOf(GWTFilter.SIZE_MB));
					sizeType1.addItem("GB", String.valueOf(GWTFilter.SIZE_GB));
					sizeType1.setSelectedIndex(filter.getSizeType1());
					sizeType1.setStyleName("okm-Input");
					HTML between = new HTML("&nbsp;&harr;&nbsp;");
					final TextBox input2 = new TextBox();
					input2.setWidth("50");
					input2.setStyleName("okm-Input");
					final ListBox sizeType2 = new ListBox();
					sizeType2.addItem("Bytes", String.valueOf(GWTFilter.SIZE_BYTES));
					sizeType2.addItem("KB", String.valueOf(GWTFilter.SIZE_KB));
					sizeType2.addItem("MB", String.valueOf(GWTFilter.SIZE_MB));
					sizeType2.addItem("GB", String.valueOf(GWTFilter.SIZE_GB));
					sizeType2.setSelectedIndex(filter.getSizeType2());
					sizeType2.setStyleName("okm-Input");
					hPanel.add(input1);
					hPanel.add(sizeType1);
					hPanel.add(between);
					hPanel.add(input2);
					hPanel.add(sizeType2);
					hPanel.setCellVerticalAlignment(input1, HasAlignment.ALIGN_MIDDLE);
					hPanel.setCellVerticalAlignment(sizeType1, HasAlignment.ALIGN_MIDDLE);
					hPanel.setCellVerticalAlignment(between, HasAlignment.ALIGN_MIDDLE);
					hPanel.setCellVerticalAlignment(input2, HasAlignment.ALIGN_MIDDLE);
					hPanel.setCellVerticalAlignment(sizeType2, HasAlignment.ALIGN_MIDDLE);
					clean.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (input1.getValue().length()>0 || input2.getValue().length()>0) {
								input1.setValue("");
								input2.setValue("");
								sizeType1.setSelectedIndex(0);
								sizeType2.setSelectedIndex(0);
								clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
							}
							evaluateCleanAll(cleanAll);
						}
					});
					clean.setStyleName("okm-Hyperlink");
					input1.addKeyUpHandler(new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							if (input1.getValue().length()>0 || input2.getValue().length()>0) {
								clean.setResource(OKMBundleResources.INSTANCE.delete());
							} else {
								clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
							}
							evaluateCleanAll(cleanAll);
						}
					});
					input2.addKeyUpHandler(new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							if (input1.getValue().length()>0 || input2.getValue().length()>0) {
								clean.setResource(OKMBundleResources.INSTANCE.delete());
							} else {
								clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
							}
							evaluateCleanAll(cleanAll);
						}
					});
					input1.setValue(filter.getFilterValue1());
					input2.setValue(filter.getFilterValue2());
					table.setHTML(row, 0, filter.getItem() + "&nbsp;");
					table.setWidget(row, 1, hPanel);
					table.setWidget(row, 2, clean);
					table.setHTML(row, 3, key);
					table.getFlexCellFormatter().setVisible(row, 3, false);
					
					GWTValidator validator = new GWTValidator();
					validator.setType("num");
					ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "input_1", validator, input1);
					ValidatorBuilder.addValidator(validationProcessor, focusAction, hPanel, "input_2", validator, input2);
					
				} else if (key.equals(String.valueOf(GWTPaginated.COL_DATE))) {
					int row = table.getRowCount();
					final GWTFilter filter = map.get(key);
					final Image clean = new Image(OKMBundleResources.INSTANCE.deleteDisabled());
					if (filter.getFrom()!=null || filter.getTo()!=null) {
						clean.setResource(OKMBundleResources.INSTANCE.delete());
					}
					HorizontalPanel hPanel = new HorizontalPanel();
					calendarFired = CALENDAR_FIRED_NONE;
					final TextBox startDate = new TextBox();
					final TextBox endDate = new TextBox();
					startDate.setWidth("100px");
					endDate.setWidth("100px");
					startDate.setEnabled(false);
					endDate.setEnabled(false);
					
					if (filter.getFrom() != null) {
						DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
						startDate.setText(dtf.format(filter.getFrom()));
					}
					
					if (filter.getTo() != null) {
						DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
						endDate.setText(dtf.format(filter.getTo()));
					}
					
					startDate.setStyleName("okm-Input");
					endDate.setStyleName("okm-Input");
					final CalendarWidget calendar = new CalendarWidget();
					final PopupPanel calendarPopup = new PopupPanel(true);
					calendarPopup.setWidget(calendar);
					final Image startCalendarIcon = new Image(OKMBundleResources.INSTANCE.calendar());
					final Image endCalendarIcon =  new Image(OKMBundleResources.INSTANCE.calendar());
					HTML dateBetween = new HTML("&nbsp;&harr;&nbsp;");
					
					calendar.addChangeHandler(new ChangeHandler(){
						@Override
						public void onChange(ChangeEvent event) {
							calendarPopup.hide();
							DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
							
							switch (calendarFired) {
								case CALENDAR_FIRED_START:
									startDate.setText(dtf.format(calendar.getDate()));
									break;
								
								case CALENDAR_FIRED_END:
									endDate.setText(dtf.format(calendar.getDate()));
									break;
							}
							
							clean.setResource(OKMBundleResources.INSTANCE.delete());
							evaluateCleanAll(cleanAll);
							calendarFired = CALENDAR_FIRED_NONE;
						}
					});
					
					startCalendarIcon.addClickHandler(new ClickHandler() { 
						@Override
						public void onClick(ClickEvent event) {
							calendarFired = CALENDAR_FIRED_START;
							if (filter.getFrom()!=null) {
								calendar.setNow((Date)filter.getFrom().clone());
							} else {
								calendar.setNow(null);
							}
							calendarPopup.setPopupPosition(startCalendarIcon.getAbsoluteLeft(), startCalendarIcon.getAbsoluteTop()-2);
							calendarPopup.show();
						}
					});
					startCalendarIcon.setStyleName("okm-Hyperlink");
					
					endCalendarIcon.addClickHandler(new ClickHandler() { 
						@Override
						public void onClick(ClickEvent event) {
							calendarFired = CALENDAR_FIRED_END;
							if (filter.getTo()!=null) {
								calendar.setNow((Date)filter.getTo().clone());
							} else {
								calendar.setNow(null);
							}
							calendarPopup.setPopupPosition(endCalendarIcon.getAbsoluteLeft(), endCalendarIcon.getAbsoluteTop()-2);
							calendarPopup.show();
						}
					});
					endCalendarIcon.setStyleName("okm-Hyperlink");
					
					clean.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							startDate.setText("");
							filter.setFrom(null);
							endDate.setText("");
							filter.setTo(null);
							clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
							evaluateCleanAll(cleanAll);
						}
					});
					clean.setStyleName("okm-Hyperlink");
					
					// Date range panel
					hPanel.add(startDate);
					hPanel.add(Util.hSpace("5"));
					hPanel.add(startCalendarIcon);
					hPanel.add(dateBetween);
					hPanel.add(endDate);
					hPanel.add(Util.hSpace("5"));
					hPanel.add(endCalendarIcon);
					startDate.setMaxLength(10);
					endDate.setMaxLength(10);
					startDate.setReadOnly(true);
					endDate.setReadOnly(true);
					hPanel.setCellVerticalAlignment(startCalendarIcon,HasAlignment.ALIGN_MIDDLE);
					hPanel.setCellVerticalAlignment(endCalendarIcon,HasAlignment.ALIGN_MIDDLE);
					dateBetween.addStyleName("okm-NoWrap");
					table.setHTML(row, 0, filter.getItem() + "&nbsp;");
					table.setWidget(row, 1, hPanel);
					table.setWidget(row, 2, clean);
					table.setHTML(row, 3, key);
					table.getFlexCellFormatter().setVisible(row, 3, false);
				}
			}
			evaluateCleanAll(cleanAll);
		}
		
		/**
		 * updateMapFilter
		 */
		private void updateMapFilter() {
			for (int row=1; row<table.getRowCount(); row++) {
				String key = table.getHTML(row, 3);
				if (key.equals(String.valueOf(GWTPaginated.COL_NAME)) || key.equals(String.valueOf(GWTPaginated.COL_AUTHOR)) ||
					key.equals(String.valueOf(GWTPaginated.COL_VERSION))) {
					TextBox input = (TextBox) table.getWidget(row, 1);
					map.get(key).setFilterValue1(input.getValue());

				} else if (key.equals(String.valueOf(GWTPaginated.COL_SIZE))) {
					HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
					TextBox input1 = (TextBox) hPanel.getWidget(0);
					TextBox input2 = (TextBox) hPanel.getWidget(3);
					ListBox sizeType1 = (ListBox) hPanel.getWidget(1);
					ListBox sizeType2 = (ListBox) hPanel.getWidget(4);
					map.get(key).setFilterValue1(input1.getValue());
					map.get(key).setFilterValue2(input2.getValue());
					map.get(key).setSizeType1(Integer.parseInt(sizeType1.getValue(sizeType1.getSelectedIndex())));
					map.get(key).setSizeType2(Integer.parseInt(sizeType2.getValue(sizeType2.getSelectedIndex())));						
					
				} else if (key.equals(String.valueOf(GWTPaginated.COL_DATE))) {
					HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
					TextBox input1 = (TextBox) hPanel.getWidget(0);
					TextBox input2 = (TextBox) hPanel.getWidget(4);
					if (!input1.getText().equals("")) {
						DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
						map.get(key).setFrom(dtf.parse(input1.getText()));
					} else {
						map.get(key).setFrom(null);
					}
					if (!input2.getText().equals("")) {
						DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.day.pattern"));
						map.get(key).setTo(dtf.parse(input2.getText()));
					} else {
						map.get(key).setTo(null);
					}
				} 
			}
			controller.setMapFilter(map); // Updating actual controller values
			controller.setOffset(0); // restarting offset to 0 when filter has been changed
			refreshFileBrowser();
		}
		
		/**
		 * isAllCleaned
		 * 
		 * @return
		 */
		private boolean isAllCleaned() {
			boolean cleanAll = true;
			for (int row=1; row<table.getRowCount(); row++) {
				String key = table.getHTML(row, 3);
				if (key.equals(String.valueOf(GWTPaginated.COL_NAME)) || key.equals(String.valueOf(GWTPaginated.COL_AUTHOR)) ||
					key.equals(String.valueOf(GWTPaginated.COL_VERSION))) {
					TextBox input = (TextBox) table.getWidget(row, 1);
					if (!input.getValue().equals("")) { 
						cleanAll = false;
						break;
					}

				} else if (key.equals(String.valueOf(GWTPaginated.COL_SIZE))) {
					HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
					TextBox input1 = (TextBox) hPanel.getWidget(0);
					TextBox input2 = (TextBox) hPanel.getWidget(3);
					if (!input1.getValue().equals("") || !input2.getValue().equals("")) { 
						cleanAll = false;
						break;
					}
					
				} else if (key.equals(String.valueOf(GWTPaginated.COL_DATE))) {
					HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
					TextBox input1 = (TextBox) hPanel.getWidget(0);
					TextBox input2 = (TextBox) hPanel.getWidget(4);
					if (!input1.getText().equals("") || !input2.getText().equals("")) {
						cleanAll = false;
						break;
					}
				}
			}
			return cleanAll;
		}
		
		/**
		 * cleanAll
		 */
		private void cleanAll() {
			for (int row=1; row<table.getRowCount(); row++) {
				String key = table.getHTML(row, 3);
				if (key.equals(String.valueOf(GWTPaginated.COL_NAME)) || key.equals(String.valueOf(GWTPaginated.COL_AUTHOR)) ||
					key.equals(String.valueOf(GWTPaginated.COL_VERSION))) {
					TextBox input = (TextBox) table.getWidget(row, 1);
					input.setValue("");
					
				} else if (key.equals(String.valueOf(GWTPaginated.COL_SIZE))) {
					HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
					TextBox input1 = (TextBox) hPanel.getWidget(0);
					TextBox input2 = (TextBox) hPanel.getWidget(3);
					ListBox sizeType1 = (ListBox) hPanel.getWidget(1);
					ListBox sizeType2 = (ListBox) hPanel.getWidget(4);
					input1.setValue("");
					input2.setValue("");
					sizeType1.setSelectedIndex(0);
					sizeType2.setSelectedIndex(0);
					
				} else if (key.equals(String.valueOf(GWTPaginated.COL_DATE))) {
					HorizontalPanel hPanel = (HorizontalPanel) table.getWidget(row, 1);
					TextBox input1 = (TextBox) hPanel.getWidget(0);
					TextBox input2 = (TextBox) hPanel.getWidget(4);
					map.get(key).setFrom(null);
					map.get(key).setTo(null);
					input1.setValue("");
					input2.setValue("");
				}
				Image clean = (Image) table.getWidget(row, 2);
				clean.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
			}
		}
		
		/**
		 * evaluateCleanAll
		 * 
		 * @param cleanAll
		 */
		private void evaluateCleanAll(Image cleanAll) {
			if (!isAllCleaned()) {
				cleanAll.setResource(OKMBundleResources.INSTANCE.delete());
			} else {
				cleanAll.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
			}
		}
		
		/**
		 * setMapFilter
		 * 
		 * @param map
		 */
		public void setMapFilter(Map<String, GWTFilter> map) {
			this.map = map;
		}
		
		/**
		 * getMapFilter
		 * 
		 * @return
		 */
		public Map<String, GWTFilter> getMapFilter() {
			return map;
		}
		
		/**
		 * addItem
		 * 
		 * @param item
		 * @param value
		 */
		public void addItem(String item, String value) {
			if (map.containsKey(value)) {
				map.get(value).setItem(item);
			} else {
				GWTFilter filter = new GWTFilter();
				filter.setItem(item);
				filter.setValue(value);
				filter.setFilterValue1("");
				filter.setFilterValue2("");
				filter.setSizeType1(0);
				filter.setSizeType2(0);
				filter.setFrom(null);
				filter.setTo(null);
				map.put(value, filter);
			}
		}
		
		/**
		 * langRefresh
		 */
		public void langRefresh() {
			accept.setHTML(Main.i18n("button.accept"));
			setText(Main.i18n("filebrowser.controller.filter"));
		}
	}
}