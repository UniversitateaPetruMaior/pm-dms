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

package com.openkm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.automation.AutomationException;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;
import com.openkm.extension.core.ExtensionException;

import de.svenjacobs.loremipsum.LoremIpsum;

/**
 * http://www.docx4java.org/trac/docx4j
 * 
 * @author pavila
 */
public class MSOUtils {
	private static Logger log = LoggerFactory.getLogger(MSOUtils.class);
	
	/**
	 * Fill DOCX template
	 */
	public static void fillTemplate(InputStream input, HashMap<String, String> model, OutputStream output) throws FileNotFoundException,
			Docx4JException, JAXBException, IOException {
		log.info("fillTemplate({}, {}, {})", new Object[] { input, model, output });
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(input);
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		
		// unmarshallFromTemplate requires string input
		String xml = XmlUtils.marshaltoString(documentPart.getJaxbElement(), true);
		
		// Do it...
		Object obj = XmlUtils.unmarshallFromTemplate(xml, model);
		
		// Inject result into docx
		documentPart.setJaxbElement((Document) obj);
		
		// Save it
		SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
		saver.save(output);
		log.info("fillTemplate: void");
	}
	
	/**
	 * Fill document template.
	 * 
	 * @param token Authentication info.
	 * @param docId The path that identifies an unique document or its UUID.
	 * @param model A map with the template keys and values.
	 * @param dstPath The path of the resulting PDF document (with the name).
	 */
	public static void fillTemplate(String token, String docId, HashMap<String, String> model, String dstPath) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, IOException, Docx4JException,
			JAXBException, FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, VersionException,
			ExtensionException, UnsupportedMimeTypeException, ItemExistsException, AutomationException {
		File docOut = null;
		InputStream docIs = null;
		OutputStream docOs = null;
		
		try {
			// Get document content
			com.openkm.bean.Document doc = OKMDocument.getInstance().getProperties(token, docId);
			docIs = OKMDocument.getInstance().getContent(token, docId, false);
			String mimeType = doc.getMimeType();
			
			// Convert to PDF
			docOut = FileUtils.createTempFileFromMime(mimeType);
			docOs = new FileOutputStream(docOut);
			MSOUtils.fillTemplate(docIs, model, docOs);
			
			// Upload to OpenKM
			try {
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().createSimple(token, dstPath, docIs);
			} catch (ItemExistsException e) {
				IOUtils.closeQuietly(docIs);
				docIs = new FileInputStream(docOut);
				OKMDocument.getInstance().checkout(token, dstPath);
				OKMDocument.getInstance().checkin(token, dstPath, docIs, "Fill template");
			}
		} finally {
			IOUtils.closeQuietly(docIs);
			IOUtils.closeQuietly(docOs);
			FileUtils.deleteQuietly(docOut);
		}
	}
	
	/**
	 * Generate sample docx
	 */
	public static void generateSample(int paragraphs, OutputStream os) throws Exception {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
		LoremIpsum li = new LoremIpsum();
		
		for (int i = 0; i < paragraphs; i++) {
			mdp.addParagraphOfText(li.getParagraphs());
		}
		
		SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
		saver.save(os);
	}
}
