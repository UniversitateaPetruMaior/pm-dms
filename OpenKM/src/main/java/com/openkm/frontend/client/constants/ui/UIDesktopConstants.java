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

package com.openkm.frontend.client.constants.ui;

/**
 * Defines contants values for the desktop panels
 * 
 * @author jllort
 */
public class UIDesktopConstants {
	
	// Navigator stack panel definition
	public static final int NAVIGATOR_TAXONOMY = 0; // Number indicates ordering
	public static final int NAVIGATOR_CATEGORIES = 1;
	public static final int NAVIGATOR_METADATA = 2;
	public static final int NAVIGATOR_THESAURUS = 3;
	public static final int NAVIGATOR_TEMPLATES = 4;
	public static final int NAVIGATOR_PERSONAL = 5;
	public static final int NAVIGATOR_MAIL = 6;
	public static final int NAVIGATOR_TRASH = 7;
	public static final int NAVIGATOR_ALL_CONTEXT = 8; // Used to indicate
														// operation to all
														// context ( search )
														// really it's not a
														// panel
	public static final int NUMBER_OF_STACKS = 8;
	public static final int STACK_HEIGHT = 22;
	
	// File Browser columns
	public static final int FILEBROWSER_COLUMN_STATUS = 0;
	public static final int FILEBROWSER_COLUMN_MASSIVE = 1;
	public static final int FILEBROWSER_COLUMN_ICON = 2;
	public static final int FILEBROWSER_COLUMN_NAME = 3;
	public static final int FILEBROWSER_COLUMN_SIZE = 4;
	public static final int FILEBROWSER_COLUMN_LASTMODIFIED = 5;
	public static final int FILEBROWSER_COLUMN_AUTHOR = 6;
	public static final int FILEBROWSER_COLUMN_VERSION = 7;
	
	// Search Browser columns
	public static final int SEARCHBROWSER_COLUMN_RELEVANCE = 0;
	public static final int SEARCHBROWSER_COLUMN_ICON = 1;
	public static final int SEARCHBROWSER_COLUMN_NAME = 2;
	public static final int SEARCHBROWSER_COLUMN_SIZE = 3;
	public static final int SEARCHBROWSER_COLUMN_LASTMODIFIED = 4;
	public static final int SEARCHBROWSER_COLUMN_AUTHOR = 5;
	public static final int SEARCHBROWSER_COLUMN_VERSION = 6;
	
	// Document history
	public static final int DOCUMENT_HISTORY_COLUMN_VERSION = 0;
	public static final int DOCUMENT_HISTORY_COLUMN_DATE = 1;
	public static final int DOCUMENT_HISTORY_COLUMN_AUTHOR = 2;
	public static final int DOCUMENT_HISTORY_COLUMN_SIZE = 3;
	public static final int DOCUMENT_HISTORY_COLUMN_BUTTON_SHOW = 4;
	public static final int DOCUMENT_HISTORY_COLUMN_BUTTON_RESTORE = 5;
	public static final int DOCUMENT_HISTORY_COLUMN_COMMENT = 6;
}