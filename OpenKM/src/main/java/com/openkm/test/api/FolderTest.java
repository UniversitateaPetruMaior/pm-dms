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

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.extension.core.ExtensionException;

public class FolderTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(FolderTest.class);
	private OKMRepository okmRepo = OKMRepository.getInstance();
	private OKMFolder okmFolder = OKMFolder.getInstance();
	private OKMAuth okmAuth = OKMAuth.getInstance();
	private String BASE = Config.UNIT_TESTING_FOLDER;
	private String token;
	
	public FolderTest(String name) {
		super(name);
	}
	
	@Before
	public void setUp() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException,
			ExtensionException, AutomationException {
		log.debug("setUp()");
		token = okmAuth.login(Config.UNIT_TESTING_USER, Config.UNIT_TESTING_PASSWORD);
		
		// Create base folder
		catchException(okmFolder).createSimple(token, BASE);
	}
	
	@After
	public void tearDown() throws LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("tearDown()");
		
		// Clean folders and ignore if do no exists
		catchException(okmFolder).delete(token, BASE);
		catchException(okmRepo).purgeTrash(token);
		
		okmAuth.logout(token);
	}
	
	@Test
	public void testCreate() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Folder betaFld = new Folder();
		betaFld.setPath(BASE + "/beta");
		Folder betaNew = okmFolder.create(token, betaFld);
		assertNotNull(betaNew);
		assertEquals(betaFld.getPath(), betaNew.getPath());
		assertEquals(Config.UNIT_TESTING_USER, betaNew.getAuthor());
		
		catchException(okmFolder).create(token, betaFld);
		assertTrue(caughtException() instanceof ItemExistsException);
	}
	
	@Test
	public void testCreateSimple() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Folder alphaNew = okmFolder.createSimple(token, BASE + "/alpha");
		assertNotNull(alphaNew);
		assertEquals(BASE + "/alpha", alphaNew.getPath());
		assertEquals(Config.UNIT_TESTING_USER, alphaNew.getAuthor());
		
		catchException(okmFolder).createSimple(token, BASE + "/alpha");
		assertTrue(caughtException() instanceof ItemExistsException);
	}
	
	/**
	 * Path with dangerous characters are encoded as entities.
	 */
	@Test
	public void testCharactersAmp() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Folder alphaNew = okmFolder.createSimple(token, BASE + "/alpha & beta");
		assertNotNull(alphaNew);
		assertEquals(BASE + "/alpha &amp; beta", alphaNew.getPath());
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha &amp; beta"));
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha & beta"));
		catchException(okmFolder).createSimple(token, BASE + "/alpha & beta");
		assertTrue(caughtException() instanceof ItemExistsException);
		
		okmFolder.createSimple(token, BASE + "/alpha &amp; beta/ño");
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha &amp; beta/ño"));
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha & beta/ño"));
		List<Folder> children = okmFolder.getChildren(token, BASE + "/alpha & beta");
		assertFalse(children.isEmpty());
		assertEquals(1, children.size());
		assertNotNull(children.get(0));
		assertEquals(BASE + "/alpha &amp; beta/ño", children.get(0).getPath());
	}
	
	/**
	 * Path with dangerous characters are encoded as entities.
	 */
	@Test
	public void testCharactersLtGt() throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException, AutomationException, LockException {
		Folder alphaNew = okmFolder.createSimple(token, BASE + "/alpha <> beta");
		assertNotNull(alphaNew);
		assertEquals(BASE + "/alpha &lt;&gt; beta", alphaNew.getPath());
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha &lt;&gt; beta"));
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha <> beta"));
		catchException(okmFolder).createSimple(token, BASE + "/alpha <> beta");
		assertTrue(caughtException() instanceof ItemExistsException);
		
		okmFolder.createSimple(token, BASE + "/alpha &lt;&gt; beta/ño");
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha &lt;&gt; beta/ño"));
		assertTrue(okmRepo.hasNode(token, BASE + "/alpha <> beta/ño"));
		List<Folder> children = okmFolder.getChildren(token, BASE + "/alpha <> beta");
		assertFalse(children.isEmpty());
		assertEquals(1, children.size());
		assertNotNull(children.get(0));
		assertEquals(BASE + "/alpha &lt;&gt; beta/ño", children.get(0).getPath());
	}
}