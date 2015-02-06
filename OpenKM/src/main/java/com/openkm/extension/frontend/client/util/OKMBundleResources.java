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

package com.openkm.extension.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * OKMBundleResources
 * 
 * @author jllort
 *
 */
public interface OKMBundleResources extends ClientBundle {
	
	public static final OKMBundleResources INSTANCE =  GWT.create(OKMBundleResources.class);
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/download.gif")
	public ImageResource download();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_disabled.gif")
	public ImageResource downloadDisabled();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_pdf.gif")
	public ImageResource downloadPdf();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/download_pdf_disabled.gif")
	public ImageResource downloadPdfDisabled();
	
	@Source("com/openkm/extension/frontend/public/img/indicator.gif")
	public ImageResource indicator();	

	@Source("com/openkm/extension/frontend/public/img/icon/actions/delete.png")
	public ImageResource delete();
		
	@Source("com/openkm/extension/frontend/public/img/icon/actions/search.png")
	public ImageResource search();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/chart_organisation.png")
	public ImageResource chartOrganisation();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/fast_action.png")
	public ImageResource fastAction();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/fast_action_disabled.png")
	public ImageResource fastActionDisabled();
	
	@Source("com/openkm/extension/frontend/public/img/icon/toolbar/meeting.png")
	public ImageResource meeting();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/room.png")
	public ImageResource room();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_in.png")
	public ImageResource roomIn();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_close.png")
	public ImageResource roomClose();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/room_add_file.png")
	public ImageResource roomAddFile();
	
	@Source("com/openkm/extension/frontend/public/img/zoom_out.gif")
	public ImageResource zoomOut();
	
	@Source("com/openkm/extension/frontend/public/img/zoom_in.gif")
	public ImageResource zoomIn();
	
	@Source("com/openkm/extension/frontend/public/img/icon/actions/user.png")
	public ImageResource user();
	
}
