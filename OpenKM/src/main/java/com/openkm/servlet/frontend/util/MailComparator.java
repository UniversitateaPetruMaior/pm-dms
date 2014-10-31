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

import com.openkm.frontend.client.bean.GWTMail;

/**
 * MailComparator
 * 
 * @author jllort
 *
 */
public class MailComparator extends CultureComparator<GWTMail> {
	
	protected MailComparator(String locale) {
		super(locale);
	}
	
	public static MailComparator getInstance(String locale) {
		try {
			MailComparator comparator = (MailComparator) CultureComparator.getInstance(MailComparator.class, locale);
			return comparator;
		}
		catch (Exception e) {
			return new MailComparator(locale);
		}
	}
	
	public static MailComparator getInstance() {
		MailComparator instance = getInstance(CultureComparator.DEFAULT_LOCALE);
		return instance;
	}

	public int compare(GWTMail arg0, GWTMail arg1) {
		GWTMail first = arg0;
		GWTMail second = arg1;
		
		return collator.compare(first.getSubject(), second.getSubject());
	}
}