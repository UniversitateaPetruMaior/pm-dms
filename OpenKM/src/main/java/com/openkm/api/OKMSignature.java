package com.openkm.api;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.SignatureException;
import com.openkm.module.ModuleManager;
import com.openkm.module.SignatureModule;

public class OKMSignature implements SignatureModule {
	private static Logger log = LoggerFactory.getLogger(OKMSignature.class);
	private static OKMSignature instance = new OKMSignature();

	private OKMSignature() {}
	
	public static OKMSignature getInstance() {
		return instance;
	}

	@Override
	public void add(String token, String nodePath, byte[] signContent) throws IOException, LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, SignatureException {
		log.debug("add({}, {})", new Object[] { token, nodePath });
		ModuleManager.getSignatureModule().add(token, nodePath, signContent);
	}

	@Override
	public boolean canSign(String token, String certSHA1) throws AccessDeniedException, DatabaseException, RepositoryException, LoginException, SignatureException {
		log.debug("canSign({}, {})", new Object[] { token, certSHA1 });
		boolean canSign = ModuleManager.getSignatureModule().canSign(token, certSHA1);
		log.debug("canSign: {}", canSign);
		return canSign;
	}

	@Override
	public InputStream getContent(String token, String certPath) throws PathNotFoundException, RepositoryException, IOException, DatabaseException, SignatureException, AccessDeniedException {
		log.debug("canSign({}, {})", new Object[] { token, certPath });
		return ModuleManager.getSignatureModule().getContent(token, certPath);
	}	
}
