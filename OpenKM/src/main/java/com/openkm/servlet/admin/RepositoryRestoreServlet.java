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

package com.openkm.servlet.admin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.RepositoryCopier;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.core.MimeTypeConfig;
import com.openkm.module.jcr.JcrRepositoryModule;
import com.openkm.servlet.RepositoryStartupServlet;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Repository restore servlet
 */
public class RepositoryRestoreServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryRestoreServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String[][] breadcrumb = new String[][] {
		new String[] { "experimental.jsp", "Experimental" },
		new String[] { "repository_restore.jsp", "Repository restore" }
	};
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();
		
		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		updateSessionManager(request);
		String fsPath = WebUtils.getString(request, "fsPath");
		PrintWriter out = response.getWriter();
		response.setContentType(MimeTypeConfig.MIME_HTML);
		header(out, "Restore", breadcrumb);
		out.flush();
		Timer timer = null;
		UpdateProgress up = null;
		
		try {
			if (fsPath != null && !fsPath.equals("")) {
				File dirSource = new File(fsPath);
				
				if (dirSource.exists() && dirSource.canRead() && dirSource.isDirectory()) {
					Config.SYSTEM_MAINTENANCE = true;
					log.info("System into maintenance mode");
					out.println("<li>System into maintenance mode</li>");
					
					// Stop repository
					log.info("Stop repository");
					out.println("<li>Stop repository</li>");
					out.flush();
					RepositoryStartupServlet.stop(null);
					
					// Restore backup
					log.info("Restoring repository");
					out.println("<li>Restoring repository</li>");
					out.flush();
					
					timer = new Timer("Repository Restore Update Progress");
					up = new UpdateProgress(out);
					timer.schedule(up, 1000, 5*1000);
					RepositoryConfig source = RepositoryConfig.create(dirSource);
					RepositoryConfig target = JcrRepositoryModule.getRepositoryConfig();
					RepositoryCopier.copy(source, target);
					up.cancel();
					timer.cancel();
					
					// Start again
					log.info("Start repository");
					out.println("<li>Start repository</li>");
					out.flush();
					
					timer = new Timer("Repository Restore Update Progress");
					up = new UpdateProgress(out);
					timer.schedule(up, 1000, 5*1000);
					RepositoryStartupServlet.start();
					up.cancel();
					timer.cancel();
					
					Config.SYSTEM_MAINTENANCE = false;
					log.info("System out of maintenance mode");
					out.println("<li>System out of maintenance mode</li>");
					out.flush();
					
					// Finalized
					log.info("Repository restore completed!");
					out.println("<li>Repository restore completed!</li>");
					out.println("</ul>");
					out.flush();
					
					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_REPOSITORY_RESTORE", null, null, null);
				} else {
					throw new IOException("Source path does not exists or not is a directory");
				}
			} else {
				throw new IOException("Missing source path");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendError(out, e.getMessage());
		} finally {
			if (up != null) up.cancel();
			if (timer != null) timer.cancel();
			out.close();
		}
	}
	
	/**
	 * Keep alive the HTTP session
	 */
	class UpdateProgress extends TimerTask {
		PrintWriter pw;
		int cont = 0;
		
		public UpdateProgress(PrintWriter pw) {
			this.pw = pw;
		}
		
		public void run() {
			if (cont++ % 2 == 0) {
				pw.print(".");
			} else {
				pw.print("o");
			}
			
			if (cont % 200 == 0) {
				pw.println("<br/>");
			}
			
			pw.flush();
		}
	}
}
