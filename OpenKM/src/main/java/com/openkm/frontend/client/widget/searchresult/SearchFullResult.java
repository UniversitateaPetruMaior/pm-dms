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

package com.openkm.frontend.client.widget.searchresult;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.WidgetUtil;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;
import com.openkm.frontend.client.widget.form.FormManager;
import com.openkm.frontend.client.widget.searchin.SearchControl;

/**
 * SearchFullResult
 * 
 * @author jllort
 *
 */
public class SearchFullResult extends Composite {
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);
	
	private ScrollPanel scrollPanel;
	private FlexTable table;
	
	/**
	 * SearchFullResult
	 */
	public SearchFullResult() {
		table = new FlexTable();
		scrollPanel = new ScrollPanel(table);
		
		scrollPanel.setStyleName("okm-Input");
		
		initWidget(scrollPanel);
	}
	
	 /* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		table.setWidth("100%");
		scrollPanel.setPixelSize(width, height);
	}
	
	/**
	 * Adds a document to the panel
	 * 
	 * @param doc The doc to add
	 */
	public void addRow(GWTQueryResult gwtQueryResult) {
		if (gwtQueryResult.getDocument()!=null || gwtQueryResult.getAttachment()!=null) {
			addDocumentRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getFolder()!=null) {
			addFolderRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getMail()!=null) {
			addMailRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		}
	}
	
	/**
	 * Adding document row
	 * 
	 * @param gwtQueryResult Query result
	 * @param score Document score
	 */
	private void addDocumentRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = table.getRowCount();
		final GWTDocument doc;
		
		if (gwtQueryResult.getDocument() != null) {
			doc = gwtQueryResult.getDocument();
		} else if (gwtQueryResult.getAttachment() != null) {
			doc = gwtQueryResult.getAttachment();
		} else {
			doc = new GWTDocument();
		}
		
		// Document row
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("okm-NoWrap");
		hPanel.add(new HTML(score.getHTML()));
		hPanel.add(Util.hSpace("5"));
		
		hPanel.add(new HTML(Util.mimeImageHTML(doc.getMimeType())));
		hPanel.add(Util.hSpace("5"));
		Anchor anchor = new Anchor();
		anchor.setHTML(doc.getName());
		anchor.setStyleName("okm-Hyperlink");
		String path = "";
		
		// On attachment case must remove last folder path, because it's internal usage not for visualization
		if (doc.isAttachment()) {
			anchor.setTitle(Util.getParent(doc.getParentPath()));
			path = doc.getParentPath(); // path will contains mail path
		} else {
			anchor.setTitle(doc.getParentPath());
			path = doc.getPath();
		}
		
		final String docPath = path;
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(Util.getParent(docPath), docPath);
			}
		});
		
		hPanel.add(anchor);
		hPanel.add(Util.hSpace("5"));
		hPanel.add(new HTML(doc.getActualVersion().getName()));
		hPanel.add(Util.hSpace("5"));
		
		// Search similar documents
		if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isSimilarDocumentVisible()) {
			final String uuid = doc.getUuid();
			Image findSimilarDocument = new Image(OKMBundleResources.INSTANCE.findSimilarDocument());
			findSimilarDocument.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Main.get().findSimilarDocumentSelectPopup.show();
					Main.get().findSimilarDocumentSelectPopup.find(uuid);
				}
			});
			
			findSimilarDocument.setTitle(Main.i18n("general.menu.file.find.similar.document"));
			findSimilarDocument.setStyleName("okm-KeyMap-ImageHover");
			hPanel.add(findSimilarDocument);
			hPanel.add(Util.hSpace("5"));
		}
		
		// Download
		if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
			Image downloadDocument = new Image(OKMBundleResources.INSTANCE.download());
			downloadDocument.addClickHandler(new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) {
					Util.downloadFileByUUID(doc.getUuid(), "");
				}
			});
			
			downloadDocument.setTitle(Main.i18n("general.menu.file.download.document"));
			downloadDocument.setStyleName("okm-KeyMap-ImageHover");
			hPanel.add(downloadDocument);
		}
		
		table.setWidget(rows++, 0, hPanel);		
		
		// Excerpt row
		if ((Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.getSearchMode() == SearchControl.SEARCH_MODE_SIMPLE ||
				!Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal.content.getText().equals("")) &&
				gwtQueryResult.getExcerpt() != null) {
			table.setHTML(rows++, 0, ""+gwtQueryResult.getExcerpt()+(gwtQueryResult.getExcerpt().length()>256?" ...":""));
			HTML space = new HTML();
			table.setWidget(rows, 0, space);
			table.getFlexCellFormatter().setHeight(rows++, 0, "5");
		}
		
		// Folder row
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("okm-NoWrap");
		hPanel2.add(new HTML("<b>"+Main.i18n("document.folder")+":</b>&nbsp;"));
		if (doc.isAttachment()) {
			String convertedPath = doc.getParentPath();
			convertedPath = Util.getParent(convertedPath) + "/"+ Util.getName(convertedPath).substring(37);
			hPanel2.add(drawMailWithAttachment(convertedPath, path));
		} else {
			hPanel2.add(drawFolder(doc.getParentPath()));
		}
		table.setWidget(rows++, 0, hPanel2);
		
		// Document detail
		HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setStyleName("okm-NoWrap");
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.author")+":</b>&nbsp;"));
		hPanel4.add(new HTML(doc.getActualVersion().getUser().getUsername()));
		hPanel4.add(Util.hSpace("33"));
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.size")+":</b>&nbsp;"));
		hPanel4.add(new HTML(Util.formatSize(doc.getActualVersion().getSize())));
		hPanel4.add(Util.hSpace("33"));
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.version")+":</b>&nbsp;"));
		hPanel4.add(new HTML(doc.getActualVersion().getName()));
		hPanel4.add(Util.hSpace("33"));
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.date.update")+":&nbsp;</b>"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		hPanel4.add(new HTML(dtf.format(doc.getLastModified())));
		table.setWidget(rows++, 0, hPanel4);
		
		// Categories and tagcloud
		rows = addCategoriesKeywords(doc.getCategories(),doc.getKeywords(), table);
		
		// PropertyGroups
		rows = addPropertyGroups(doc.getPath(), table);
		
		// Separator end line
		Image horizontalLine = new Image("img/transparent_pixel.gif");
		horizontalLine.setStyleName("okm-TopPanel-Line-Border");
		horizontalLine.setSize("100%", "2px");
		table.setWidget(rows, 0, horizontalLine);
		table.getFlexCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_BOTTOM);
		table.getFlexCellFormatter().setHeight(rows, 0, "30");		
	}
	
	/**
	 * addPropertyGroups
	 */
	private int addPropertyGroups(final String path, FlexTable table) {
		int rows = table.getRowCount();
		if (Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.showPropertyGroups.getValue()) {
			final HorizontalPanel propertyGroupsPanel = new HorizontalPanel();
			table.setWidget(rows++, 0, propertyGroupsPanel);
			propertyGroupService.getGroups(path, new AsyncCallback<List<GWTPropertyGroup>>() {
				@Override
				public void onSuccess(List<GWTPropertyGroup> result) {
					drawPropertyGroups(path, result, propertyGroupsPanel);
				}
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("getGroups", caught);
				}
			});
		}
		return rows;
	}
	
	/**
	 * drawCategoriesKeywords
	 */
	private int addCategoriesKeywords(Set<GWTFolder> categories, Set<String> keywords, FlexTable table) {
		int rows = table.getRowCount();
		
		// Categories and tagcloud
		if (categories.size() > 0 || keywords.size() > 0) { 
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.setStyleName("okm-NoWrap");
			
			if (categories.size() > 0) {
				FlexTable tableSubscribedCategories = new FlexTable();
				tableSubscribedCategories.setStyleName("okm-DisableSelect");
				
				// Sets the document categories
				for (Iterator<GWTFolder> it = categories.iterator(); it.hasNext();) {
					drawCategory(tableSubscribedCategories, it.next());
				}
				
				hPanel.add(new HTML("<b>"+Main.i18n("document.categories")+"</b>"));
				hPanel.add(Util.hSpace("5"));
				hPanel.add(tableSubscribedCategories);
				hPanel.add(Util.hSpace("33"));
			}
			
			if (keywords.size() > 0) {
				// Tag cloud
				TagCloud keywordsCloud = new TagCloud();
				keywordsCloud.setWidth("350");
				WidgetUtil.drawTagCloud(keywordsCloud, keywords);
				hPanel.add(new HTML("<b>"+Main.i18n("document.keywords.cloud")+"</b>"));
				hPanel.add(Util.hSpace("5"));
				hPanel.add(keywordsCloud);
			}
			
			table.setWidget(rows++, 0, hPanel);
		}
		
		return rows;
	}
	
	/**
	 * drawPropertyGroups
	 */
	private void drawPropertyGroups(final String docPath, final List<GWTPropertyGroup> propertyGroups, 
			final HorizontalPanel propertyGroupsPanel) {
		if (propertyGroups.size() > 0) {
			Status status = Main.get().mainPanel.search.searchBrowser.searchResult.status;
			status.setFlag_refreshPropertyGroups();
			final GWTPropertyGroup propertyGroup = propertyGroups.remove(0);
			propertyGroupService.getProperties(docPath, propertyGroup.getName(), false, new AsyncCallback<List<GWTFormElement>>() {
				@Override
				public void onSuccess(List<GWTFormElement> result) {
					if (propertyGroupsPanel.getWidgetCount()==0) {
						HTML label = new HTML("");
						label.setStyleName("okm-Security-Title");
						label.setHeight("15");
						Image verticalLine = new Image("img/transparent_pixel.gif");
						verticalLine.setStyleName("okm-Vertical-Line-Border");
						verticalLine.setSize("2","100%");
						VerticalPanel vlPanel = new VerticalPanel();						
						vlPanel.add(label);
						vlPanel.add(verticalLine);
						vlPanel.setCellWidth(verticalLine, "7");
						vlPanel.setCellHeight(verticalLine, "100%");
						vlPanel.setHeight("100%");
						propertyGroupsPanel.add(vlPanel);
						propertyGroupsPanel.setCellHorizontalAlignment(vlPanel, HasAlignment.ALIGN_LEFT);
						propertyGroupsPanel.setCellWidth(vlPanel, "7");
						propertyGroupsPanel.setCellHeight(vlPanel, "100%");
					}
					
					Image verticalLine = new Image("img/transparent_pixel.gif");
					verticalLine.setStyleName("okm-Vertical-Line-Border");
					verticalLine.setSize("2","100%");
					FormManager manager = new FormManager();
					manager.setFormElements(result);
					manager.draw(true); // read only !
					VerticalPanel vPanel = new VerticalPanel();
					HTML label = new HTML(propertyGroup.getLabel());
					label.setStyleName("okm-Security-Title");
					label.setHeight("15");
					vPanel.add(label);
					vPanel.add(manager.getTable());
					propertyGroupsPanel.add(vPanel);
					propertyGroupsPanel.add(verticalLine);
					propertyGroupsPanel.setCellVerticalAlignment(vPanel, HasAlignment.ALIGN_TOP);
					propertyGroupsPanel.setCellHorizontalAlignment(verticalLine, HasAlignment.ALIGN_CENTER);
					propertyGroupsPanel.setCellWidth(verticalLine, "12");
					propertyGroupsPanel.setCellHeight(verticalLine, "100%");
					drawPropertyGroups(docPath, propertyGroups, propertyGroupsPanel);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("drawPropertyGroups", caught);
				}
			});
		} else {
			Status status = Main.get().mainPanel.search.searchBrowser.searchResult.status;
			status.unsetFlag_refreshPropertyGroups();
		}
	}
	
	/**
	 * Adding folder
	 * 
	 * @param gwtQueryResult Query result
	 * @param score The folder score
	 */
	private void addFolderRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = table.getRowCount();
		final GWTFolder folder = gwtQueryResult.getFolder();
		
		// Folder row
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("okm-NoWrap");
		hPanel.add(new HTML(score.getHTML()));
		hPanel.add(Util.hSpace("5"));
		
		// Looks if must change icon on parent if now has no childs and properties with user security atention
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (folder.isHasChildren()) {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_childs.gif")));
			} else {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_empty.gif")));
			}
		} else {
			if (folder.isHasChildren()) {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_childs_ro.gif")));
			} else {
				hPanel.add(new HTML(Util.imageItemHTML("img/menuitem_empty_ro.gif")));
			}
		}
		
		Anchor anchor = new Anchor();
		anchor.setHTML(folder.getName());
		anchor.setTitle(folder.getParentPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openPath(folder.getPath(), "");
			}
		});
		anchor.setStyleName("okm-Hyperlink");
		hPanel.add(anchor);
		table.setWidget(rows++, 0, hPanel);	
		
		// Folder row
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("okm-NoWrap");
		hPanel2.add(new HTML("<b>"+Main.i18n("folder.parent")+":</b>&nbsp;"));
		hPanel2.add(drawFolder(folder.getParentPath()));
		table.setWidget(rows++, 0, hPanel2);
		
		// Folder detail
		HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setStyleName("okm-NoWrap");
		hPanel3.add(new HTML("<b>"+Main.i18n("search.result.author")+":</b>&nbsp;"));
		hPanel3.add(new HTML(folder.getUser().getUsername()));
		hPanel3.add(Util.hSpace("33"));
		hPanel3.add(new HTML("<b>"+Main.i18n("folder.created")+":&nbsp;</b>"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		hPanel3.add(new HTML(dtf.format(folder.getCreated())));
		table.setWidget(rows++, 0, hPanel3);
		
		// Categories and tagcloud
		rows = addCategoriesKeywords(folder.getCategories(),folder.getKeywords(), table);
		
		// PropertyGroups
		rows = addPropertyGroups(folder.getPath(), table);
		
		// Separator end line
		Image horizontalLine = new Image("img/transparent_pixel.gif");
		horizontalLine.setStyleName("okm-TopPanel-Line-Border");
		horizontalLine.setSize("100%", "2px");
		table.setWidget(rows, 0, horizontalLine);
		table.getFlexCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_BOTTOM);
		table.getFlexCellFormatter().setHeight(rows, 0, "30");		
	}
	
	/**
	 * Adding mail
	 * 
	 * @param gwtQueryResult Query result
	 * @param score The mail score
	 */
	private void addMailRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = table.getRowCount();
		final GWTMail mail = gwtQueryResult.getMail();
		
		// Mail row
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("okm-NoWrap");
		hPanel.add(new HTML(score.getHTML()));
		hPanel.add(Util.hSpace("5"));
		
		if (mail.getAttachments().size() > 0) {
			hPanel.add(new HTML(Util.imageItemHTML("img/email_attach.gif")));
		} else {
			hPanel.add(new HTML(Util.imageItemHTML("img/email.gif")));
		}
		
		Anchor anchor = new Anchor();
		anchor.setHTML(mail.getSubject());
		anchor.setTitle(mail.getParentPath());
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String docPath = mail.getPath();
				CommonUI.openPath(Util.getParent(docPath), docPath);
			}
		});
		anchor.setStyleName("okm-Hyperlink");
		hPanel.add(anchor);
		table.setWidget(rows++, 0, hPanel);	
		
		// Mail Subject
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("okm-NoWrap");
		hPanel2.add(new HTML("<b>"+Main.i18n("mail.subject")+":</b>&nbsp;"));
		hPanel2.add(new HTML(mail.getSubject()));
		
		// Excerpt row
		if ((Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.getSearchMode() == SearchControl.SEARCH_MODE_SIMPLE ||
				!Main.get().mainPanel.search.searchBrowser.searchIn.searchNormal.content.getText().equals("")) &&
				gwtQueryResult.getExcerpt() != null) {
			table.setHTML(rows++, 0, ""+gwtQueryResult.getExcerpt()+(gwtQueryResult.getExcerpt().length()>256?" ...":""));
			HTML space = new HTML();
			table.setWidget(rows, 0, space);
			table.getFlexCellFormatter().setHeight(rows++, 0, "5");
		}
		
		// Folder row
		HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setStyleName("okm-NoWrap");
		hPanel3.add(new HTML("<b>"+Main.i18n("document.folder")+":</b>&nbsp;"));
		hPanel3.add(drawFolder(mail.getParentPath()));
		table.setWidget(rows++, 0, hPanel3);
		
		// mail details
		HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setStyleName("okm-NoWrap");
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.author")+":</b>&nbsp;"));
		hPanel4.add(new HTML(mail.getAuthor()));
		hPanel4.add(Util.hSpace("33"));
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.size")+":</b>&nbsp;"));
		hPanel4.add(new HTML(Util.formatSize(mail.getSize())));
		hPanel4.add(Util.hSpace("33"));
		hPanel4.add(new HTML("<b>"+Main.i18n("search.result.date.create")+":&nbsp;</b>"));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		hPanel4.add(new HTML(dtf.format(mail.getCreated())));
		table.setWidget(rows++, 0, hPanel4);
		
		// Categories and tagcloud
		rows = addCategoriesKeywords(mail.getCategories(),mail.getKeywords(), table);
		
		// PropertyGroups
		rows = addPropertyGroups(mail.getPath(), table);
		
		// From, To and Reply panel
		HorizontalPanel hPanel5 = new HorizontalPanel();
		hPanel5.setStyleName("okm-NoWrap");
		hPanel5.add(new HTML("<b>"+Main.i18n("mail.from")+":</b>&nbsp;"));
		hPanel5.add(new HTML(mail.getFrom()));
		
		if (mail.getTo().length > 0) {
			VerticalPanel toPanel = new VerticalPanel();
			
			for (int i=0; i < mail.getTo().length; i++) {
				Anchor hTo = new Anchor();
				final String mailTo = mail.getTo()[i].contains("<") ?
						mail.getTo()[i].substring(mail.getTo()[i].indexOf("<") + 1,
								mail.getTo()[i].indexOf(">")):mail.getTo()[i];
				hTo.setHTML(mail.getTo()[i].replace("<", "&lt;").replace(">", "&gt;"));
				hTo.setTitle("mailto:" + mailTo);
				hTo.setStyleName("okm-Mail-Link");
				hTo.addStyleName("okm-NoWrap");
				hTo.addClickHandler(new ClickHandler() { 
					@Override
					public void onClick(ClickEvent event) {
						Window.open("mailto:" + mailTo, "_self", "");
					}
				});
				
				toPanel.add(hTo);
			}
			
			hPanel5.add(Util.hSpace("33"));
			hPanel5.add((new HTML("<b>" + Main.i18n("mail.to") + ":</b>&nbsp;")));
			hPanel5.add(toPanel);
		}
		
		if (mail.getReply().length > 0) {
			VerticalPanel replyPanel = new VerticalPanel();
			
			for (int i=0; i < mail.getReply().length; i++) {
				Anchor hReply = new Anchor();
				final String mailReply = mail.getReply()[i].contains("<") ?
						mail.getReply()[i].substring(mail.getReply()[i].indexOf("<") + 1,
								mail.getReply()[i].indexOf(">")):mail.getReply()[i];
				hReply.setHTML(mail.getReply()[i].replace("<", "&lt;").replace(">", "&gt;"));
				hReply.setTitle("mailto:" + mailReply);
				hReply.setStyleName("okm-Mail-Link");
				hReply.addStyleName("okm-NoWrap");
				hReply.addClickHandler(new ClickHandler() { 
					@Override
					public void onClick(ClickEvent event) {
						Window.open("mailto:" + mailReply, "_self", "");
					}
				});
				
				replyPanel.add(hReply);
			}
			
			hPanel5.add(Util.hSpace("33"));
			hPanel5.add(new HTML("<b>" + Main.i18n("mail.reply") + ":</b>&nbsp;"));
			hPanel5.add(replyPanel);
		}
		
		table.setWidget(rows++, 0, hPanel5);
		
		// Separator end line
		Image horizontalLine = new Image("img/transparent_pixel.gif");
		horizontalLine.setStyleName("okm-TopPanel-Line-Border");
		horizontalLine.setSize("100%", "2px");
		table.setWidget(rows, 0, horizontalLine);
		table.getFlexCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_BOTTOM);
		table.getFlexCellFormatter().setHeight(rows, 0, "30");	
	}
	
	/**
	 * drawCategory
	 */
	private void drawCategory(final FlexTable tableSubscribedCategories, final GWTFolder category) {
		int row = tableSubscribedCategories.getRowCount();
		Anchor anchor = new Anchor();
		
		// Looks if must change icon on parent if now has no childs and properties with user security atention
		String path = category.getPath().substring(16); // Removes /okm:categories
		
		if (category.isHasChildren()) {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));
		} else {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_empty.gif", path, "top"));
		}
		
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(category.getPath(), null);
			}
		});
		
		anchor.setStyleName("okm-KeyMap-ImageHover");
		tableSubscribedCategories.setWidget(row, 0, anchor);
	}
	
	/**
	 * drawFolder
	 */
	private Anchor drawFolder(final String path) {
		Anchor anchor = new Anchor();
		anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));	
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(path, null);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		return anchor;
	}
	
	/**
	 * drawMailWithAttachment
	 */
	private Anchor drawMailWithAttachment(String convertedPath, final String path) {
		Anchor anchor = new Anchor();
		anchor.setHTML(Util.imageItemHTML("img/email_attach.gif", convertedPath, "top"));	
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(Util.getParent(path), path);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		return anchor;
	}
	
	/**
	 * removeAllRows
	 */
	public void removeAllRows() {
		table.removeAllRows();
	}
}