package com.openkm.module.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.openkm.bean.AppVersion;
import com.openkm.bean.Document;
import com.openkm.bean.Signature;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.SignatureException;
import com.openkm.dao.UserCertificateDAO;
import com.openkm.dao.bean.UserCertificate;
import com.openkm.module.ModuleManager;
import com.openkm.module.RepositoryModule;
import com.openkm.module.SignatureModule;
import com.openkm.module.jcr.base.BaseNotificationModule;
import com.openkm.module.jcr.stuff.JCRUtils;
import com.openkm.module.jcr.stuff.JcrSessionManager;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.CertificateUtil;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WarUtils;


public class JcrSignatureModule implements SignatureModule {

	private static Logger log = LoggerFactory.getLogger(JcrSignatureModule.class);

	@Override
	public void add(String token, String nodePath, byte[] signContent)
			throws IOException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException,
			SignatureException {
		log.debug("add({}, {})", new Object[] { token, nodePath });
		
		Session session = null;
		Node jcrNode = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				// we changed all "Direct" into "Jcr"
				session = JcrSessionManager.getInstance().get(token);
			}
			
			// read signature content
			ByteArrayInputStream bais = new ByteArrayInputStream(signContent);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        org.w3c.dom.Document xmlSignatureDoc = dbf.newDocumentBuilder().parse(bais);
			bais.close();

			// Find and validate certificate from signature file
			NodeList certXMLNodeList = xmlSignatureDoc.getElementsByTagName("ds:X509Certificate");
			if (certXMLNodeList.getLength() == 0) {
				throw new SignatureException("Cannot find certificate element");
			}
			X509Certificate x509Certificate = CertificateUtil.getX509Certificate(certXMLNodeList.item(0).getFirstChild().getNodeValue());
			x509Certificate.checkValidity();
			
			// Load user certificates and validate signature certificate
			String sha1 = CertificateUtil.getCertificateSHA1(x509Certificate);
			UserCertificate userAttachedCertificates = UserCertificateDAO.findByUser(session.getUserID(), sha1);
			if (userAttachedCertificates == null) {
				throw new SignatureException("Signature is not belong to logged user");
			}
			
			// Find Signature element
			NodeList signatureXMLNodeList = xmlSignatureDoc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (signatureXMLNodeList.getLength() == 0) {
				throw new SignatureException("Cannot find Signature element");
			}
			
			// Create a DOM XMLSignatureFactory that will be used to unmarshal
			// the document containing the XMLSignature
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			// Create a DOMValidateContext and specify a KeyValue KeySelector
			// and document context
			DOMValidateContext valContext = new DOMValidateContext(x509Certificate.getPublicKey(), signatureXMLNodeList.item(0));
			
			// unmarshal the XMLSignature
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);
			if (!signature.validate(valContext)) {
				throw new SignatureException("Invalid signature !");
			}

			// check the validation status of each Reference
			List<String> digestValues = new ArrayList<String>();
			boolean sv = signature.getSignatureValue().validate(valContext);
			Iterator iterator = signature.getSignedInfo().getReferences().iterator();
			for (int j = 0; iterator.hasNext(); j++) {
				javax.xml.crypto.dsig.Reference reference = (javax.xml.crypto.dsig.Reference) iterator.next();
				if (!reference.validate(valContext)) {
					throw new SignatureException("Invalid signature !");
				}
				digestValues.add(SecureStore.b64Encode(reference.getDigestValue()));
			}
			
			// load jcr node content and init digest message
			jcrNode = session.getRootNode().getNode(nodePath.substring(1));
			Node contentNode = jcrNode.getNode(Document.CONTENT);
			InputStream docInputStream = contentNode.getProperty(JcrConstants.JCR_DATA).getStream();
			byte[] docInBytes = IOUtils.toByteArray(docInputStream);
			MessageDigest md1 = MessageDigest.getInstance("SHA1");
			md1.update(docInBytes);
			final String digestValue = SecureStore.b64Encode(md1.digest());

			// verify digest value
			boolean digestValueMatch = false;
			for (String dv : digestValues) {
				if (digestValue.equals(dv)) {
					digestValueMatch = true;
					break;
				}
			}
			if (!digestValueMatch) {
				throw new SignatureException("Invalid signature !");
			}

			// save signature
			if (!jcrNode.isNodeType(Signature.MIX_TYPE)) {
				log.debug("Adding mixing '{}' to {}", Signature.MIX_TYPE, jcrNode.getPath());
				jcrNode.addMixin(Signature.MIX_TYPE);
				jcrNode.save();
			}
			Node signaturesJCRNode = jcrNode.getNode(Signature.LIST);

			// prepare signature values 
			Calendar cal = Calendar.getInstance();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			OutputFormat outputFormat = new OutputFormat(xmlSignatureDoc);
			XMLSerializer serializer = new XMLSerializer(outputStream, outputFormat);
			serializer.serialize(xmlSignatureDoc);
			InputStream inputStream = (InputStream) new ByteArrayInputStream(outputStream.toByteArray());
			
			// find and update signature if exists
			boolean jcrSignatureExists = false;
			for (NodeIterator nit = signaturesJCRNode.getNodes(); nit.hasNext();) {
				Node signatureJRCNode = nit.nextNode();
				String nodeSHA1 = signatureJRCNode.getProperty(Signature.SIGN_SHA1).getString();
				if (sha1.equals(nodeSHA1)) {
					signatureJRCNode.setProperty(Signature.DATE, cal);
					signatureJRCNode.setProperty(Signature.SIGN_DIGEST, digestValue);
					signatureJRCNode.setProperty(Signature.SIGN_SIZE, inputStream.available());
					signatureJRCNode.setProperty(Signature.SIGN_CONTENT, inputStream);
					signatureJRCNode.save();
					jcrSignatureExists = true;
					break;
				}
			}
			// save as new node
			if (!jcrSignatureExists) {
				Node signatureJRCNode = signaturesJCRNode.addNode(cal.getTimeInMillis() + "", Signature.TYPE);
				signatureJRCNode.setProperty(Signature.DATE, cal);
				signatureJRCNode.setProperty(Signature.USER, session.getUserID());
				signatureJRCNode.setProperty(Signature.SIGN_SHA1, sha1);
				signatureJRCNode.setProperty(Signature.SIGN_DIGEST, digestValue);
				signatureJRCNode.setProperty(Signature.SIGN_SIZE, inputStream.available());
				signatureJRCNode.setProperty(Signature.SIGN_CONTENT, inputStream);
				signaturesJCRNode.save();
			}

			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(jcrNode, session.getUserID(), "SAVE_SIGNATURE", nodePath);

			// Activity log
			UserActivity.log(session.getUserID(), "SAVE_SIGNATURE", jcrNode.getUUID(), nodePath, sha1);
			
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new IOException(e.getMessage(), e);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new RepositoryException(e.getMessage(), e);
		} catch (CertificateException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new SignatureException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new SignatureException(e.getMessage(), e);
		} catch (XMLSignatureException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new SignatureException(e.getMessage(), e);
		} catch (MarshalException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new SignatureException(e.getMessage(), e);
		} catch (SAXException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new SignatureException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(jcrNode);
			throw new SignatureException(e.getMessage(), e);
		} finally {
			if (token == null)
				JCRUtils.logout(session);
		}		
	}

	@Override
	public boolean canSign(String token, String certSHA1)
			throws AccessDeniedException, DatabaseException,
			RepositoryException, LoginException, SignatureException {
		log.debug("canSign({}, {})", new Object[] { token, certSHA1});
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			String userId = null;
			if (session == null) {
				Authentication auth = null, oldAuth = null;
				try {
					oldAuth = PrincipalUtils.getAuthentication();
					auth = PrincipalUtils.getAuthenticationByToken(token);
					userId = auth.getName();
				} finally {
					PrincipalUtils.setAuthentication(oldAuth);
				}
			} else {
				userId = session.getUserID();
			}
			
			UserCertificate userAttachedCertificates = UserCertificateDAO.findByUser(userId, certSHA1);
			if (userAttachedCertificates == null) {
				throw new SignatureException("Signature is not belong to logged user");
			}
			
			// Activity log
			UserActivity.log(userId, "HAS_SIGNATURE", session == null ? "Authentication" : "JcrSession", "", certSHA1);
			return true;
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
			throw new DatabaseException(e);
		} catch (LoginException e) {
			log.warn(e.getMessage(), e);
			throw new LoginException(e);
		} catch (javax.jcr.RepositoryException e) {
			log.warn(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} catch (SignatureException e) {
			log.warn(e.getMessage(), e);
			throw new SignatureException(e);
		} finally {
			if (token == null)
				JCRUtils.logout(session);
		}
	}

	@Override
	public InputStream getContent(String token, String certPath)
			throws PathNotFoundException, RepositoryException, IOException,
			DatabaseException, SignatureException {
		log.debug("getContent({}, {})", new Object[] { token, certPath });
		InputStream is = null;
		Session session = null;

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}

			Node signatureNode = session.getRootNode().getNode(certPath.substring(1));
			is = signatureNode.getProperty(Signature.SIGN_CONTENT).getStream();
			
			if (is==null){
				throw new SignatureException("Could not find signature !");
			}

			// Activity log
			UserActivity.log(session.getUserID(), "GET_DOCUMENT_SIGNATURE_CONTENT", token,  certPath, "" + is.available());
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (token == null)
				JCRUtils.logout(session);
		}

		log.debug("getContent: {}", is);
		return is;
	}

	
	
}
