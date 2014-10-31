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

package com.openkm.servlet.frontend.util;

import com.openkm.frontend.client.bean.GWTPropertyGroup;

/**
 * PropertyGroupComparator
 * 
 * @author jllort
 *
 */
public class PropertyGroupComparator extends CultureComparator<GWTPropertyGroup> {
	
	protected PropertyGroupComparator(String locale) {
		super(locale);
	}
	
	public static PropertyGroupComparator getInstance(String locale) {
		try {
			PropertyGroupComparator comparator = (PropertyGroupComparator) CultureComparator.getInstance(PropertyGroupComparator.class, locale);
			return comparator;
		}
		catch (Exception e) {
			return new PropertyGroupComparator(locale);
		}
	}
	
	public static PropertyGroupComparator getInstance() {
		PropertyGroupComparator instance = getInstance(CultureComparator.DEFAULT_LOCALE);
		return instance;
	}

	public int compare(GWTPropertyGroup arg0, GWTPropertyGroup arg1) {
		GWTPropertyGroup first = arg0;
		GWTPropertyGroup second = arg1;
		
		return collator.compare(first.getLabel(), second.getLabel());		
	}
}