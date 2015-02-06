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

package com.openkm.extension.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;
import com.openkm.extension.core.ExtensionException;
import com.openkm.extension.dao.ZohoTokenDAO;
import com.openkm.extension.dao.bean.ZohoToken;
import com.openkm.module.db.DbDocumentModule;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.module.jcr.JcrDocumentModule;
import com.openkm.module.jcr.stuff.JcrSessionManager;
import com.openkm.servlet.frontend.OKMHttpServlet;

/**
 * ZohoFileUploadServlet
 * 
 * @author jllort
 */
public class ZohoFileUploadServlet extends OKMHttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ZohoFileUploadServlet.class);
	
	@SuppressWarnings("unchecked")
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		log.info("service({}, {})", request, response);
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		InputStream is = null;
		String id = "";
		
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			
			// Parse the request and get all parameters and the uploaded file
			List<FileItem> items;
			
			try {
				items = upload.parseRequest(request);
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
					if (item.isFormField()) {
						// if (item.getFieldName().equals("format")) { format =
						// item.getString("UTF-8"); }
						if (item.getFieldName().equals("id")) {
							id = item.getString("UTF-8");
						}
						// if (item.getFieldName().equals("filename")) {
						// filename = item.getString("UTF-8"); }
					} else {
						is = item.getInputStream();
					}
				}
				
				// Retrieve token
				ZohoToken zot = ZohoTokenDAO.findByPk(id);
				
				// Save document
				if (Config.REPOSITORY_NATIVE) {
					String sysToken = DbSessionManager.getInstance().getSystemToken();
					String path = OKMRepository.getInstance().getNodePath(sysToken, zot.getNode());
					new DbDocumentModule().checkout(sysToken, path, zot.getUser());
					new DbDocumentModule().checkin(sysToken, path, is, "Modified from Zoho", zot.getUser());
				} else {
					String sysToken = JcrSessionManager.getInstance().getSystemToken();
					String path = OKMRepository.getInstance().getNodePath(sysToken, zot.getNode());
					new JcrDocumentModule().checkout(sysToken, path, zot.getUser());
					new JcrDocumentModule().checkin(sysToken, path, is, "Modified from Zoho", zot.getUser());
				}
			} catch (FileUploadException e) {
				log.error(e.getMessage(), e);
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
			} catch (FileSizeExceededException e) {
				log.error(e.getMessage(), e);
			} catch (UserQuotaExceededException e) {
				log.error(e.getMessage(), e);
			} catch (VirusDetectedException e) {
				log.error(e.getMessage(), e);
			} catch (VersionException e) {
				log.error(e.getMessage(), e);
			} catch (LockException e) {
				log.error(e.getMessage(), e);
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
			} catch (AutomationException e) {
				log.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
