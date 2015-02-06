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

package com.openkm.util.pagination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.bean.pagination.FilterResult;
import com.openkm.bean.pagination.ObjectToOrder;
import com.openkm.core.DatabaseException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.dao.bean.NodeFolder;
import com.openkm.dao.bean.NodeMail;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPaginated;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;
import com.openkm.principal.PrincipalAdapterException;

/**
 * FilterUtils
 * 
 * @author jllort
 * 
 */
public class FilterUtils {
	private static Logger log = LoggerFactory.getLogger(FilterUtils.class);
	
	/**
	 * filter
	 */
	public static FilterResult filter(GWTWorkspace workspace, List<Object> col, int order, String selectedRowId,
			Map<String, GWTFilter> mapFilter) throws PrincipalAdapterException, IOException, ParseException,
			NoSuchGroupException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("filter({}, {}, {}, {}, {})", new Object[] { workspace, col, order, selectedRowId, mapFilter });
		long begin = System.currentTimeMillis();
		List<ObjectToOrder> convertedCol = new ArrayList<ObjectToOrder>();
		int selectedRow = -1;
		int actualRow = 0;
		boolean foundRow = false;
		
		for (Object obj : col) {
			ObjectToOrder oto = new ObjectToOrder();
			oto.setObj(obj);
			
			// Filtering enabled
			boolean add = true;
			
			for (String key : mapFilter.keySet()) {
				switch (Integer.parseInt(key)) {
					case GWTPaginated.COL_NONE:
						// No ordering
						break;
						
					case GWTPaginated.COL_TYPE:
						// No ordering here ( use type icons for it )
						break;
						
					case GWTPaginated.COL_NAME:
						if (obj instanceof NodeFolder) {
							if (!((NodeFolder) obj).getName().toLowerCase().contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof NodeDocument) {
							if (!((NodeDocument) obj).getName().toLowerCase().contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof NodeMail) {
							if (!((NodeMail) obj).getSubject().toLowerCase().contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						}
						break;
						
					case GWTPaginated.COL_SIZE:
						if (obj instanceof NodeFolder) {
							if (mapFilter.get(key).getSizeValue1() > 0) {
								add = false; // Folder has size 0
							}
						} else if (obj instanceof NodeDocument) {
							if (mapFilter.get(key).getSizeValue1() >= 0) {
								String docUuid = ((NodeDocument) obj).getUuid();
								NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
								
								if (nDocVer.getSize() < mapFilter.get(key).getSizeValue1()) {
									add = false; // Should not be added to final list
								}
							}
							
							if (mapFilter.get(key).getSizeValue2() >= 0) {
								String docUuid = ((NodeDocument) obj).getUuid();
								NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
								
								if (nDocVer.getSize() > mapFilter.get(key).getSizeValue2()) {
									add = false; // Should not be added to final list
								}
							}
						} else if (obj instanceof NodeMail) {
							if (mapFilter.get(key).getSizeValue1() >= 0
									&& ((NodeMail) obj).getSize() < mapFilter.get(key).getSizeValue1()) {
								add = false; // Should not be added to final list
							}
							
							if (mapFilter.get(key).getSizeValue2() >= 0
									&& ((NodeMail) obj).getSize() > mapFilter.get(key).getSizeValue2()) {
								add = false; // Should not be added to final list
							}
						}
						break;
						
					case GWTPaginated.COL_DATE:
						if (obj instanceof NodeFolder) {
							if (mapFilter.get(key).getFrom() != null
									&& ((NodeFolder) obj).getCreated().getTime().compareTo(mapFilter.get(key).getFrom()) <= 0) {
								add = false;
							}
							
							if (mapFilter.get(key).getTo() != null
									&& ((NodeFolder) obj).getCreated().getTime().compareTo(mapFilter.get(key).getTo()) >= 0) {
								add = false;
							}
						} else if (obj instanceof NodeDocument) {
							if (mapFilter.get(key).getFrom() != null
									&& ((NodeDocument) obj).getLastModified().getTime()
											.compareTo(mapFilter.get(key).getFrom()) <= 0) {
								add = false;
							}
							
							if (mapFilter.get(key).getTo() != null
									&& ((NodeDocument) obj).getLastModified().getTime()
											.compareTo(mapFilter.get(key).getTo()) >= 0) {
								add = false;
							}
						} else if (obj instanceof NodeMail) {
							if (mapFilter.get(key).getFrom() != null
									&& ((NodeMail) obj).getCreated().getTime().compareTo(mapFilter.get(key).getFrom()) <= 0) {
								add = false;
							}
							if (mapFilter.get(key).getTo() != null
									&& ((NodeMail) obj).getCreated().getTime().compareTo(mapFilter.get(key).getTo()) >= 0) {
								add = false;
							}
						}
						break;
						
					case GWTPaginated.COL_AUTHOR:
						if (obj instanceof NodeFolder) {
							if (!OKMAuth.getInstance().getName(null, ((NodeFolder) obj).getAuthor()).toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof NodeDocument) {
							String docUuid = ((NodeDocument) obj).getUuid();
							NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
							
							if (!OKMAuth.getInstance().getName(null, nDocVer.getAuthor())
									.toLowerCase().contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof NodeMail) {
							if (!OKMAuth.getInstance().getName(null, ((NodeMail) obj).getAuthor()).toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						}
						break;
						
					case GWTPaginated.COL_VERSION:
						if (obj instanceof NodeFolder) {
							add = false;
						} else if (obj instanceof NodeDocument) {
							String docUuid = ((NodeDocument) obj).getUuid();
							NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(docUuid);
							
							if (!nDocVer.getName().equals(mapFilter.get(key).getFilterValue1())) {
								add = false;
							}
						} else if (obj instanceof NodeMail) {
							add = false;
						}
						break;
				}
				
				if (!add) {
					break;
				}
			}
			
			if (add) {							
				// Get exact Pagination selectedRowId location in rows
				// When selectedRowId any filtering, ordering, reverse etc.. value is empty
				if (selectedRowId != null && !selectedRowId.equals("") && !foundRow) {
					if (obj instanceof NodeFolder) {
						String path = NodeBaseDAO.getInstance().getPathFromUuid(((NodeFolder) obj).getUuid());
						
						if (path.equals(selectedRowId)) {
							selectedRow = actualRow;
							foundRow = true;
						}
					} else if (obj instanceof NodeDocument) {
						String path = NodeBaseDAO.getInstance().getPathFromUuid(((NodeDocument) obj).getUuid());
						
						if (path.equals(selectedRowId)) {
							selectedRow = actualRow;
							foundRow = true;
						}
					} else if (obj instanceof NodeMail) {
						String path = NodeBaseDAO.getInstance().getPathFromUuid(((NodeMail) obj).getUuid());
						
						if (path.equals(selectedRowId)) {
							selectedRow = actualRow;
							foundRow = true;
						}
					}
					
					actualRow++;
				}
				
				convertedCol.add(oto);
			}
		}
		
		FilterResult fr = new FilterResult();
		fr.setConvertedCol(convertedCol);
		fr.setSelectedRow(selectedRow);
		
		log.trace("filter.Time: {}", System.currentTimeMillis() - begin);
		log.debug("filter: {}", fr);
		return fr;
	}
	
	/**
	 * filter
	 */
	public static void filter(GWTWorkspace workspace, List<?> col, Map<String, GWTFilter> mapFilter)
			throws PrincipalAdapterException, IOException, ParseException, NoSuchGroupException, PathNotFoundException,
			RepositoryException, DatabaseException {
		List<Object> toRemove = new ArrayList<Object>();
		
		for (Object obj : col) {
			// Filtering enabled
			boolean add = true;
			
			for (String key : mapFilter.keySet()) {
				switch (Integer.parseInt(key)) {
					case GWTPaginated.COL_NONE:
						// No ordering
						break;
						
					case GWTPaginated.COL_TYPE:
						// No ordering here ( use type icons for it )
						break;
						
					case GWTPaginated.COL_NAME:
						if (obj instanceof GWTFolder) {
							if (!((GWTFolder) obj).getName().toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof GWTDocument) {
							if (!((GWTDocument) obj).getName().toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof GWTMail) {
							if (!((GWTMail) obj).getSubject().toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						}
						break;
						
					case GWTPaginated.COL_SIZE:
						if (obj instanceof GWTFolder) {
							if (mapFilter.get(key).getSizeValue1() > 0) {
								add = false; // Folder has size 0
							}
						} else if (obj instanceof GWTDocument) {
							if (mapFilter.get(key).getSizeValue1() >= 0
									&& ((GWTDocument) obj).getActualVersion().getSize() < mapFilter.get(key)
											.getSizeValue1()) {
								add = false; // Should not be added to final list
							}
							if (mapFilter.get(key).getSizeValue2() >= 0
									&& ((GWTDocument) obj).getActualVersion().getSize() > mapFilter.get(key)
											.getSizeValue2()) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof GWTMail) {
							if (mapFilter.get(key).getSizeValue1() >= 0
									&& ((GWTMail) obj).getSize() < mapFilter.get(key).getSizeValue1()) {
								add = false; // Should not be added to final list
							}
							if (mapFilter.get(key).getSizeValue2() >= 0
									&& ((GWTMail) obj).getSize() > mapFilter.get(key).getSizeValue2()) {
								add = false; // Should not be added to final list
							}
						}
						break;
						
					case GWTPaginated.COL_DATE:
						if (obj instanceof GWTFolder) {
							if (mapFilter.get(key).getFrom() != null
									&& ((GWTFolder) obj).getCreated().compareTo(mapFilter.get(key).getFrom()) <= 0) {
								add = false;
							}
							if (mapFilter.get(key).getTo() != null
									&& ((GWTFolder) obj).getCreated().compareTo(mapFilter.get(key).getTo()) >= 0) {
								add = false;
							}
						} else if (obj instanceof GWTDocument) {
							if (mapFilter.get(key).getFrom() != null
									&& ((GWTDocument) obj).getActualVersion().getCreated()
											.compareTo(mapFilter.get(key).getFrom()) <= 0) {
								add = false;
							}
							if (mapFilter.get(key).getTo() != null
									&& ((GWTDocument) obj).getActualVersion().getCreated()
											.compareTo(mapFilter.get(key).getTo()) >= 0) {
								add = false;
							}
						} else if (obj instanceof GWTMail) {
							if (mapFilter.get(key).getFrom() != null
									&& ((GWTMail) obj).getCreated().compareTo(mapFilter.get(key).getFrom()) <= 0) {
								add = false;
							}
							if (mapFilter.get(key).getTo() != null
									&& ((GWTMail) obj).getCreated().compareTo(mapFilter.get(key).getTo()) >= 0) {
								add = false;
							}
						}
						break;
						
					case GWTPaginated.COL_AUTHOR:
						if (obj instanceof GWTFolder) {
							if (!OKMAuth.getInstance().getName(null, ((GWTFolder) obj).getAuthor()).toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof GWTDocument) {
							if (!OKMAuth.getInstance()
									.getName(null, ((GWTDocument) obj).getActualVersion().getAuthor()).toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						} else if (obj instanceof GWTMail) {
							if (!OKMAuth.getInstance().getName(null, ((GWTMail) obj).getAuthor()).toLowerCase()
									.contains(mapFilter.get(key).getFilterValue1())) {
								add = false; // Should not be added to final list
							}
						}
						break;
						
					case GWTPaginated.COL_VERSION:
						if (obj instanceof GWTFolder) {
							add = false;
						} else if (obj instanceof GWTDocument) {
							if (!((GWTDocument) obj).getActualVersion().getName()
									.equals(mapFilter.get(key).getFilterValue1())) {
								add = false;
							}
						} else if (obj instanceof GWTMail) {
							add = false;
						}
						break;
				}
				
				if (!add) {
					break;
				}
			}
			
			if (!add) {
				toRemove.add(obj);
			}
		}
		
		// Clean final collection
		col.removeAll(toRemove);
	}
}