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

package com.openkm.frontend.client.widget.properties;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.service.RPCService;
import com.openkm.frontend.client.extension.widget.preview.PreviewExtension;
import com.openkm.frontend.client.util.Util;

/**
 * Notes
 * 
 * @author jllort
 */
public class Preview extends Composite {
	private static final int TURN_BACK_HEIGHT = 25;
	private VerticalPanel vPanel;
	private HTML pdf;
	private HTML swf;
	private HTML video;
	public HTMLPreview htmlPreview;
	private int width = 0;
	private int height = 0;
	private boolean previewAvailable = false;
	private boolean previewConversion = true;
	String mediaUrl = "";
	private String mediaProvider = "";
	private List<PreviewExtension> widgetPreviewExtensionList;
	private HasPreviewEvent previewEvent;
	private HorizontalPanel hReturnPanel;
	private Button backButton;
	private String pdfID = "jsPdfViewer";
	
	/**
	 * Preview
	 */
	public Preview(final HasPreviewEvent previewEvent) {
		this.previewEvent = previewEvent;
		widgetPreviewExtensionList = new ArrayList<PreviewExtension>();
		vPanel = new VerticalPanel();
		htmlPreview = new HTMLPreview();
		pdf = new HTML("<div id=\"pdfembededcontainer\"></div>\n");
		swf = new HTML("<div id=\"pdfviewercontainer\"></div>\n");
		video = new HTML("<div id=\"mediaplayercontainer\"></div>\n");
		hReturnPanel = new HorizontalPanel();
		hReturnPanel.setWidth("100%");
		backButton = new Button(Main.i18n("search.button.preview.back"));
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				previewEvent.returnBack();
			}
		});
		backButton.setStylePrimaryName("okm-Button");
		HTML space2 = Util.hSpace("5");
		hReturnPanel.add(space2);
		hReturnPanel.add(backButton);
		hReturnPanel.setCellWidth(space2, "5");
		hReturnPanel.setCellHorizontalAlignment(backButton, HasAlignment.ALIGN_LEFT);
		hReturnPanel.setCellVerticalAlignment(backButton, HasAlignment.ALIGN_MIDDLE);
		hReturnPanel.setHeight(String.valueOf(TURN_BACK_HEIGHT));
		hReturnPanel.setStyleName("okm-TopPanel");
		hReturnPanel.addStyleName("okm-Border-Top");
		hReturnPanel.addStyleName("okm-Border-Left");
		hReturnPanel.addStyleName("okm-Border-Right");
		initWidget(vPanel);
	}
	
	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		this.width = (previewEvent==null)?width:width;
		this.height = (previewEvent==null)?height:height-TURN_BACK_HEIGHT;
		htmlPreview.setPixelSize(this.width, this.height);
	}
	
	/**
	 * showHTML
	 */
	public void showHTML(GWTDocument doc) {
		hideWidgetExtension();
		vPanel.clear();
		
		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT));
		}
		
		vPanel.add(htmlPreview);
		vPanel.setCellHorizontalAlignment(htmlPreview, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(htmlPreview, HasAlignment.ALIGN_MIDDLE);
		
		if (previewAvailable) {
			htmlPreview.showHTML(doc);
		}
	}
	
	/**
	 * showEmbedSWF
	 * 
	 * @param uuid Unique document ID to be previewed.
	 */
	public void showEmbedSWF(String uuid) {
		hideWidgetExtension();
		vPanel.clear();
		
		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT));
		}
		
		vPanel.add(swf);
		vPanel.setCellHorizontalAlignment(swf, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(swf, HasAlignment.ALIGN_MIDDLE);
		
		if (previewAvailable) {
			if (previewConversion) {
				String url = RPCService.ConverterServlet + "?inline=true&toSwf=true&uuid=" + URL.encodeQueryString(uuid);
				swf.setHTML("<div id=\"pdfviewercontainer\"></div>\n"); // needed for rewriting purpose
				
				if (Main.get().workspaceUserProperties.getWorkspace().getPreviewer().equals("flexpaper")) {
					Util.createPDFViewerFlexPaper(url, "" + width, "" + height);
				} else {
					Util.createPDFViewerZviewer(url, "" + width, "" + height);
				}
				
				Main.get().conversionStatus.getStatus();
			} else {
				String url = RPCService.DownloadServlet + "?inline=true&uuid=" + URL.encodeQueryString(uuid);
				swf.setHTML("<div id=\"swfviewercontainer\"></div>\n"); // needed for rewriting purpose
				Util.createSwfViewer(url, "" + width, "" + height);
			}
		} else {
			swf.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable")
					+ "</div>\n"); // needed for rewriting purpose
		}
	}
	
	/**
	 * resizeEmbedSWF
	 */
	public void resizeEmbedSWF(int width, int height){
		if (previewConversion) {
			if (Main.get().workspaceUserProperties.getWorkspace().getPreviewer().equals("flexpaper")) {
				Util.resizePDFViewerFlexPaper(""+width, ""+height);
			} else {
				Util.resizePDFViewerZviewer(""+width, ""+height);
			}
		} else {
			Util.resizeSwfViewer(""+width, ""+height);
		}
	}
	
	/**
	 * showEmbedPDF
	 * 
	 * @param uuid Unique document ID to be previewed.
	 */
	public void showEmbedPDF(String uuid) {
		hideWidgetExtension();
		vPanel.clear();
		
		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT));
		}
		
		vPanel.add(pdf);
		vPanel.setCellHorizontalAlignment(pdf, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(pdf, HasAlignment.ALIGN_MIDDLE);
		
		if (previewAvailable) {
			String url = RPCService.DownloadServlet + "?inline=true&uuid=" + URL.encodeQueryString(uuid);
			pdf.setHTML("<div id=\"pdfembededcontainer\">"+
					"<object id=\""+pdfID+"\" name=\""+pdfID+"\" width=\""+width+"\" height=\""+height+"\" type=\"application/pdf\" data=\""+url+"\"&#zoom=85&scrollbar=1&toolbar=1&navpanes=1&view=FitH\">"+
					"<p>Browser plugin suppport error, PDF can not be displayed</p>"+
					"</object>"+
					"</div>\n"); // needed for rewriting  purpose
		} else {
			swf.setHTML("<div id=\"pdfembededcontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable") + "</div>\n");
		}
	}
	
	/**
	 * cleanPreview
	 */
	public void cleanPreview() {
		swf.setHTML("<div id=\"pdfviewercontainer\" ></div>\n");
	}
	
	/**
	 * Set the media file to reproduce
	 * 
	 * @param mediaUrl The media file url
	 */
	public void showMediaFile(String mediaUrl, String mimeType) {
		hideWidgetExtension();
		vPanel.clear();
		
		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT));
		}
		
		vPanel.add(video);
		vPanel.setCellHorizontalAlignment(video, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(video, HasAlignment.ALIGN_MIDDLE);

		this.mediaUrl = mediaUrl;
		Util.removeMediaPlayer();
		video.setHTML("<div id=\"mediaplayercontainer\"></div>\n");
		
		if (mimeType.equals("audio/mpeg")) {
			mediaProvider = "sound";
		} else if (mimeType.equals("video/x-flv") || mimeType.equals("video/mp4")) {
			mediaProvider = "video";
		} else if (mimeType.equals("application/x-shockwave-flash")) {
			mediaProvider = "";
		}
		
		Util.createMediaPlayer(mediaUrl, mediaProvider, ""+width, ""+height);
	}
	
	/**
	 * resizeMediaPlayer
	 */
	public void resizeMediaPlayer(int width, int height){
		Util.resizeMediaPlayer(""+width, ""+height);
	}
	
	/**
	 * setPreviewExtension
	 */
	public void showPreviewExtension(PreviewExtension preview, String url) {
		hideWidgetExtension();
		vPanel.clear();
		
		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT));
		}
		
		if (previewAvailable) {
			vPanel.add(preview.getWidget());
			preview.createViewer(url, width, height);
		}
	}
	
	/**
	 * hideWidgetExtension
	 */
	private void hideWidgetExtension() {
		if (vPanel.getWidgetCount() > 4) {
			for (int i = 3; i < vPanel.getWidgetCount(); i++) {
				vPanel.getWidget(i).setVisible(false);
			}
		}
	}
	
	/**
	 * Sets the boolean value if previewing document is available
	 * 
	 * @param previewAvailable Set preview availability status.
	 */
	public void setPreviewAvailable(boolean previewAvailable) {
		this.previewAvailable = previewAvailable;
	}
	
	/**
	 * Sets the boolean value if document preview does not need conversion
	 */
	public void setPreviewConversion(boolean previewConversion) {
		this.previewConversion = previewConversion;
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		if (!previewAvailable) {
			swf.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable")
					+ "</div>\n"); // needed for rewriting purpose
		}
		backButton.setHTML(Main.i18n("search.button.preview.back"));
	}
	
	/**
	 * previewDocument
	 */
	public void previewDocument(boolean refreshing, GWTDocument doc) {
		if (doc.getMimeType().equals("video/x-flv") || doc.getMimeType().equals("video/mp4") || doc.getMimeType().equals("audio/mpeg")) {
			if (!refreshing) {
				showMediaFile(RPCService.DownloadServlet + "?uuid=" + URL.encodeQueryString(doc.getUuid()), doc.getMimeType());
			} else {
				resizeMediaPlayer(width, height);
			}
		} else if (HTMLPreview.isPreviewAvailable(doc.getMimeType())) {
			if (!refreshing) {
				showHTML(doc);
			}
		} else if (doc.isConvertibleToDxf()) {
			boolean found = false;
			// There's no preview
			if (!found) {
				setPreviewAvailable(false); // Special case autocad when converting from pdf
			}
			
			if (!refreshing) {
				showEmbedSWF(doc.getUuid());
			} else {
				resizeEmbedSWF(width, height);
			}
		} else if (doc.getMimeType().equals("application/dicom")) {
			boolean found = false;
			// There's no preview
			if (!found) {
				setPreviewAvailable(false);
			}
			if (!refreshing) {
				showEmbedSWF(doc.getUuid());
			} else {
				resizeEmbedSWF(width, height);
			}
		} else if (doc.getMimeType().equals("application/x-shockwave-flash")) {
			setPreviewConversion(false);
			
			if (!refreshing) {
				showEmbedSWF(doc.getUuid());
			} else {
				resizeEmbedSWF(width, height);
			}
		} else {
			if (Main.get().workspaceUserProperties.getWorkspace().isAcrobatPluginPreview() && doc.getMimeType().equals("application/pdf") ) {
				if (!refreshing) {
					showEmbedPDF(doc.getUuid());
				} else {
					Util.resizeEmbededPDF(""+width, ""+height, pdfID);
				}
			} else {
				setPreviewConversion(true);
				
				if (!refreshing) {
					showEmbedSWF(doc.getUuid());
				} else {
					resizeEmbedSWF(width, height);
				}
			}
		}
	}
	
	/**
	 * addPreviewExtension
	 */
	public void addPreviewExtension(PreviewExtension extension) {
		widgetPreviewExtensionList.add(extension);
	}
}
