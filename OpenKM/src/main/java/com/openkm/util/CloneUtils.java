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

package com.openkm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class CloneUtils {
	/**
	 * Clone collection
	 */
	public static <E> List<E> clone(List<E> input) {
		List<E> ret = new ArrayList<E>();
		
		for (E tmp : input) {
			ret.add(tmp);
		}
		
		return ret;
	}
	
	/**
	 * Clone set
	 */
	public static <E> Set<E> clone(Set<E> input) {
		Set<E> ret = new HashSet<E>();
		
		for (E tmp : input) {
			ret.add(tmp);
		}
		
		return ret;
	}
	
	/**
	 * Clone map
	 */
	public static <K, V> Map<K, V> clone(Map<K, V> input) {
		Map<K, V> ret = new HashMap<K, V>();
		
		for (Entry<K, V> tmp : input.entrySet()) {
			ret.put(tmp.getKey(), tmp.getValue());
		}
		
		return ret;
	}
	
	/**
	 * DeepClone
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap deepClone(HashMap map) {
		HashMap newone = (HashMap) map.clone();
		
		for (Iterator it = newone.keySet().iterator(); it.hasNext(); ) {
			Object newkey = it.next();
			Object deepobj = null, newobj = newone.get(newkey);
			
			if (newobj instanceof HashMap) {
				deepobj = deepClone((HashMap) newobj);
			} else if (newobj instanceof String) {
				deepobj = (Object)new String((String)newobj);
			} else if (newobj instanceof Vector) {
				deepobj = ((Vector) newobj).clone();
			}
			
			newone.put(newkey, deepobj);
		}
		
		return newone;
	}
}
