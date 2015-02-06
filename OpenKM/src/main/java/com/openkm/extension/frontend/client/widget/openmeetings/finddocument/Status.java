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

package com.openkm.extension.frontend.client.widget.openmeetings.finddocument;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.OKMBundleResources;

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
	private boolean flag_getChilds = false;
	private Widget widget;
	
	/**
	 * Status
	 */
	public Status(Widget widget) {
		super(false, true);
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
	 * Refreshing satus
	 */
	public void refresh() {
		if (flag_getChilds) {
			int left = ((widget.getOffsetWidth() - 200) / 2) + widget.getAbsoluteLeft();
			int top = ((widget.getOffsetHeight() - 40) / 2) + widget.getAbsoluteTop();
			setPopupPosition(left, top);
			((FindDocumentSelectPopup) widget).scrollDocumentPanel.addStyleName("okm-PanelRefreshing");
			super.show();
		} else {
			super.hide();
			((FindDocumentSelectPopup) widget).scrollDocumentPanel.removeStyleName("okm-PanelRefreshing");
		}
	}
	
	/**
	 * Set childs flag
	 */
	public void setFlagChilds() {
		msg.setHTML(Main.i18n("filebrowser.status.refresh.document"));
		flag_getChilds = true;
		refresh();
	}
	
	/**
	 * Unset childs flag
	 */
	public void unsetFlagChilds() {
		flag_getChilds = false;
		refresh();
	}
}