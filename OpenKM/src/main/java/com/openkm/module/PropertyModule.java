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

package com.openkm.module;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.VersionException;

public interface PropertyModule {

	/**
	 * Add a category to a node.
	 * 
	 * @param token The session authorization token.
	 * @param nodePath The complete path to the node.
	 * @param catId Category id (the UUID of the category node).
	 * @throws VersionException A document checked in can't be modified.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no node in this
	 * repository path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void addCategory(String token, String nodePath, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Remove a category from a node.
	 * 
	 * @param token The session authorization token.
	 * @param nodePath The complete path to the node.
	 * @param catId Category id (the UUID of the category node).
	 * @throws VersionException A document checked in can't be modified.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no node in this
	 * repository path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void removeCategory(String token, String nodePath, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException;
	
	/**
	 * Add a keyword to a node.
	 * 
	 * @param token The session authorization token.
	 * @param nodePath The complete path to the node.
	 * @param keyword The keyword to be added.
	 * @throws VersionException A document checked in can't be modified.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no node in this
	 * repository path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String addKeyword(String token, String nodePath, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException;

	/**
	 * Remove a keyword from a node.
	 * 
	 * @param token The session authorization token.
	 * @param nodePath The complete path to the node.
	 * @param keyword The keyword to be removed.
	 * @throws VersionException A document checked in can't be modified.
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no node in this
	 * repository path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the node because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void removeKeyword(String token, String nodePath, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException;
}
