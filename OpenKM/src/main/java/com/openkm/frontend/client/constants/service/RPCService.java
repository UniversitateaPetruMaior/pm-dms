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

package com.openkm.frontend.client.constants.service;

import com.openkm.frontend.client.Main;

/**
 * RPC Service General configuration
 * 
 * @author jllort
 */
public class RPCService {
	// Service entry point 
	public static String DownloadServlet = Main.CONTEXT + "/frontend/Download";
	public static String ConverterServlet = Main.CONTEXT + "/frontend/Converter";
	public static String FileUploadService = Main.CONTEXT + "/frontend/FileUpload";
	public static String ReportServlet = Main.CONTEXT + "/frontend/ExecuteReport";
	public static String CSVExporterServlet = Main.CONTEXT + "/frontend/CSVExporter";
	public static String FeedService = Main.CONTEXT + "/feed/";
}
