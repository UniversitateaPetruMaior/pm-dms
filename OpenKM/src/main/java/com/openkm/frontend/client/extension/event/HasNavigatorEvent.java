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

package com.openkm.frontend.client.extension.event;



/**
 * HasNavigatorEvent
 * 
 * 
 * @author jllort
 *
 */

public interface HasNavigatorEvent {
	
	/**
	 * NavigatorEventConstant
	 * 
	 * @author jllort
	 *
	 */
	public static class NavigatorEventConstant {
		
		static final int EVENT_STACK_CHANGED = 1;
		
		private int type = 0;
		
		/**
		 * ToolBarEventConstant
		 * 
		 * @param type
		 */
		private NavigatorEventConstant(int type) {
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
	}
	
	NavigatorEventConstant STACK_CHANGED = new NavigatorEventConstant(NavigatorEventConstant.EVENT_STACK_CHANGED);
	
	/**
	 * @param event
	 */
	void fireEvent(NavigatorEventConstant event);
	
}