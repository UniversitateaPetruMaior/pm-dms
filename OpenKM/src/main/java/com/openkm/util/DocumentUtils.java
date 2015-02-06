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

package com.openkm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.core.MimeTypeConfig;
import com.openkm.dao.NodeDocumentVersionDAO;
import com.openkm.dao.bean.NodeDocument;
import com.openkm.util.metadata.MetadataExtractor;
import com.openkm.util.metadata.OfficeMetadata;
import com.openkm.util.metadata.OpenOfficeMetadata;
import com.openkm.util.metadata.PdfMetadata;

public class DocumentUtils {
	private static Logger log = LoggerFactory.getLogger(DocumentUtils.class);
	
	public void staticExtractMetadata(NodeDocument nDoc) {
		InputStream is = null;
		
		try {
			if (MimeTypeConfig.MIME_PDF.equals(nDoc.getMimeType())) {
				is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(nDoc.getUuid(), true);
				PdfMetadata md = MetadataExtractor.pdfExtractor(is);
				log.info("{}", md);
			} else if (MimeTypeConfig.MIME_MS_WORD.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_MS_EXCEL.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_MS_POWERPOINT.equals(nDoc.getMimeType())) {
				is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(nDoc.getUuid(), true);
				OfficeMetadata md = MetadataExtractor.officeExtractor(is, nDoc.getMimeType());
				log.info("{}", md);
			} else if (MimeTypeConfig.MIME_OO_TEXT.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_OO_SPREADSHEET.equals(nDoc.getMimeType())
					|| MimeTypeConfig.MIME_OO_PRESENTATION.equals(nDoc.getMimeType())) {
				is = NodeDocumentVersionDAO.getInstance().getCurrentContentByParent(nDoc.getUuid(), true);
				OpenOfficeMetadata md = new OpenOfficeMetadata();
				log.info("{}", md);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	/**
	 * Text spell checker
	 */
	public static String spellChecker(String text) throws IOException {
		log.debug("spellChecker({})", text);
		StringBuilder sb = new StringBuilder();
		
		if (Config.SYSTEM_OPENOFFICE_DICTIONARY.equals("")) {
			log.warn("OpenOffice dictionary not configured");
			sb.append(text);
		} else {
			log.info("Using OpenOffice dictionary: {}", Config.SYSTEM_OPENOFFICE_DICTIONARY);
			ZipFile zf = new ZipFile(Config.SYSTEM_OPENOFFICE_DICTIONARY);
			OpenOfficeSpellDictionary oosd = new OpenOfficeSpellDictionary(zf);
			SpellChecker sc = new SpellChecker(oosd);
			sc.setCaseSensitive(false);
			StringTokenizer st = new StringTokenizer(text);
			
			while (st.hasMoreTokens()) {
				String w = st.nextToken();
				List<String> s = sc.getDictionary().getSuggestions(w);
				
				if (s.isEmpty()) {
					sb.append(w).append(" ");
				} else {
					sb.append(s.get(0)).append(" ");
				}
			}
			
			zf.close();
		}
		
		log.debug("spellChecker: {}", sb.toString());
		return sb.toString();
	}
}
