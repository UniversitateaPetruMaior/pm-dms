package com.openkm.ws.endpoint;

import java.io.IOException;

import javax.jcr.LoginException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

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

@WebService(name = "OKMSignature", serviceName = "OKMSignature", targetNamespace = "http://ws.openkm.com")
public class OKMSignature {

	private static Logger log = LoggerFactory.getLogger(OKMSignature.class);

	@WebMethod
	public void signDocument(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String nodePath,
			@WebParam(name = "signBytes") byte[] signBytes) throws IOException, LockException, PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException, SignatureException   {
		log.debug("signDocument({})", nodePath);
		SignatureModule sm = ModuleManager.getSignatureModule();
		sm.add(token, nodePath, signBytes);
		log.debug("signDocument: {}", nodePath);
	}
	
	@WebMethod
	public void validateCertificate(@WebParam(name = "token") String token,
			@WebParam(name = "certSHA1") String certSHA1) throws LoginException, AccessDeniedException, DatabaseException, RepositoryException, SignatureException    {
		log.debug("validateCertificate({})", certSHA1);
		SignatureModule sm = ModuleManager.getSignatureModule();
		sm.canSign(token, certSHA1);
		log.debug("validateCertificate: {}", certSHA1);
	}
	
}
