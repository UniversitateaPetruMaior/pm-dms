package com.openkm.module;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.LoginException;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.SignatureException;

public interface SignatureModule {	

	/**
	 * Add a signature to a document
	 * 
	 * @param nodePath The path that identifies an unique document.
	 * @param publicKey The public key text
	 * @param signature The signature text
	 * @throws LockException A locked document can't be modified.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this folder because of lack of permissions.
	 * @throws RepositoryException If there is any problem.
	 */
	public void add(String token, String nodePath, byte[] signContent) throws  IOException, LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, SignatureException ;
	
	public boolean canSign(String token, String certSHA1) throws  AccessDeniedException, DatabaseException, RepositoryException, LoginException, SignatureException;
	
	public InputStream getContent(String token, String certPath) throws PathNotFoundException, RepositoryException, IOException, DatabaseException, SignatureException, AccessDeniedException;
}
