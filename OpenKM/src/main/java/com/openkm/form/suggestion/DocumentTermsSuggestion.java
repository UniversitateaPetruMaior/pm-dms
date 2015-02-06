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

package com.openkm.form.suggestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeDocumentDAO;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.SearchDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import com.openkm.extractor.TextExtractorWork;

public class DocumentTermsSuggestion implements Suggestion {
	private static Logger log = LoggerFactory.getLogger(DocumentTermsSuggestion.class);
	
	@Override
	public List<String> getSuggestions(String nodeUuid, String nodePath, Select sel) throws PathNotFoundException, SuggestionException,
			DatabaseException {
		log.debug("getSuggestions({}, {}, {})", new Object[] { nodeUuid, nodePath, sel });
		List<String> list = new ArrayList<String>();
		
		try {
			if (NodeDocumentDAO.getInstance().isValid(nodeUuid)) {
				// Get text extraction
				if (!NodeDocumentDAO.getInstance().isTextExtracted(nodeUuid)) {
					// Force text extraction
					NodeDocumentVersion nDocVer = NodeDocumentVersionDAO.getInstance().findCurrentVersion(nodeUuid);
					TextExtractorWork tew = new TextExtractorWork();
					tew.setDocUuid(nodeUuid);
					tew.setDocPath(nodePath);
					tew.setDocVerUuid(nDocVer.getUuid());
					
					// Execute extractor
					NodeDocumentDAO.getInstance().textExtractorHelper(tew);
				}
				
				for (String term : SearchDAO.getInstance().getTerms(NodeDocument.class, nodeUuid)) {
					for (Option opt : sel.getOptions()) {
						if (term.equals(opt.getLabel().toLowerCase())) {
							if (!list.contains(opt.getValue())) {
								list.add(opt.getValue());
								break;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new SuggestionException("IOException: " + e.getMessage(), e);
		}
		
		return list;
	}
}
