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

package com.openkm.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMMail;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Mail;
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.servlet.admin.BaseServlet;
import com.openkm.util.WebUtils;

/**
 * Mail Preview Servlet
 * 
 */
public class MailPreviewServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(MailPreviewServlet.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String uuid = WebUtils.getString(request, "uuid");
		
		try {
			String mailPath = OKMRepository.getInstance().getNodePath(null, uuid);
			Mail mail = OKMMail.getInstance().getProperties(null, mailPath);
			String content = mail.getContent();
			
			if (MimeTypeConfig.MIME_TEXT.equals(mail.getMimeType())) {
				content = content.replace("\n", "<br/>");
			}
			
			ServletContext sc = getServletContext();
			sc.setAttribute("content", content);
			sc.getRequestDispatcher("/mail_preview.jsp").forward(request, response);
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		}
	}
}
