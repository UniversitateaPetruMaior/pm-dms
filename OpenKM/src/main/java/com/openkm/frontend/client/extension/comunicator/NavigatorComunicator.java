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

package com.openkm.frontend.client.extension.comunicator;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;



/**
 * NavigatorComunicator
 * 
 * @author jllort
 *
 */
public class NavigatorComunicator {
	
	/**
	 * Gets the stack index value
	 * 
	 * @return The stack index value
	 */
	public static int getStackIndex() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex();
	}
	
	/**
	 * isTaxonomyShown
	 * 
	 * @return
	 */
	public static boolean isTaxonomyShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_TAXONOMY;
	}
	
	/**
	 * isCategoriesShown
	 * 
	 * @return
	 */
	public static boolean isCategoriesShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_CATEGORIES;
	}
	
	/**
	 * isThesaurusShown
	 * 
	 * @return
	 */
	public static boolean isThesaurusShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_THESAURUS;
	}
	
	/**
	 * isTemplatesShown
	 * 
	 * @return
	 */
	public static boolean isTemplatesShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_TEMPLATES;
	}
	
	/**
	 * isPersonalShown
	 * 
	 * @return
	 */
	public static boolean isPersonalShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_PERSONAL;
	}
	
	/**
	 * isMailShown
	 * 
	 * @return
	 */
	public static boolean isMailShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_MAIL;
	}
	
	/**
	 * isTrashShown
	 * 
	 * @return
	 */
	public static boolean isTrashShown() {
		return Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_TRASH;
	}
	
	/**
	 * getFolder
	 * 
	 * @return
	 */
	public static GWTFolder getFolder() {
		return Main.get().activeFolderTree.getFolder();
	}
	
	/**
	 * getActualPath
	 * 
	 * @return
	 */
	public static String getActualPath() {
		return Main.get().activeFolderTree.getActualPath();
	}
	
	/**
	 * getRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getRootFolder() {
		return Main.get().taxonomyRootFolder;
	}
	
	/**
	 * getCategoriesRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getCategoriesRootFolder() {
		return Main.get().categoriesRootFolder;
	}
	
	/**
	 * getThesaurusRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getThesaurusRootFolder() {
		return Main.get().thesaurusRootFolder;
	}
	
	/**
	 * getTemplatesRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getTemplatesRootFolder() {
		return Main.get().templatesRootFolder;
	}
	
	/**
	 * getPersonalRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getPersonalRootFolder() {
		return Main.get().personalRootFolder;
	}
	
	/**
	 * getMailRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getMailRootFolder() {
		return Main.get().mailRootFolder;
	}
	
	/**
	 * getTrashRootFolder
	 * 
	 * @return
	 */
	public static GWTFolder getTrashRootFolder() {
		return Main.get().trashRootFolder;
	}
	
	/**
	 * initJavaScriptApi
	 */
	public native void initJavaScriptApi() /*-{
		$wnd.jsGetActualPath = function() {
			return @com.openkm.frontend.client.extension.comunicator.NavigatorComunicator::getActualPath()();
		};
	}-*/;
}