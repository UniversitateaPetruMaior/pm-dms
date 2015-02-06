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

package com.openkm.test.api;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;

public class RepositoryTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(RepositoryTest.class);
	private OKMRepository okmRepo = OKMRepository.getInstance();
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String token;
	
	public RepositoryTest(String name) {
		super(name);
	}
	
	@Before
	public void setUp() throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setUp()");
		token = okmAuth.login(Config.UNIT_TESTING_USER, Config.UNIT_TESTING_PASSWORD);
	}
	
	@After
	public void tearDown() throws RepositoryException, DatabaseException {
		log.debug("tearDown()");
		okmAuth.logout(token);
	}
	
	@Test
	public void testBasic() throws PathNotFoundException, RepositoryException, DatabaseException {
		String repoUuid = okmRepo.getRepositoryUuid(token);
		assertNotNull(repoUuid);
		
		Folder rootFld = okmRepo.getRootFolder(token);
		assertNotNull(rootFld);
		assertEquals("/okm:root", rootFld.getPath());
		
		String rootUuid = okmRepo.getNodeUuid(token, rootFld.getPath());
		assertNotNull(rootUuid);
		assertEquals(rootFld.getPath(), okmRepo.getNodePath(token, rootUuid));
	}
}
