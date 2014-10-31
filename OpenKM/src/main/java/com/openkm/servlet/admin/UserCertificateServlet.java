package com.openkm.servlet.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.UserCertificateDAO;
import com.openkm.dao.bean.UserCertificate;
import com.openkm.util.CertificateUtil;
import com.openkm.module.jcr.stuff.JCRUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

public class UserCertificateServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(UserCertificateServlet.class);
	private Map<String, String> postParam;
	private List<FileItem> postFiles;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		
		// Disable browser cache
		response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
		response.setHeader("Cache-Control", "max-age=0, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		
		postParam = new HashMap<String, String>();
		postFiles = new ArrayList<FileItem>();
		if (isMultipartRequest(request)) {
			initPostParameters(request);
		}
		String action = getStringParameter(request, "action");
		Session session = null;
		updateSessionManager(request);
		try {
			session = JCRUtils.getSession();
			if (action.equals("create")) {
				create(session, request, response);
			} else if (action.equals("delete")) {
				delete(session, request, response);
			}
			if (action.equals("") || getBooleanParameter(request, "persist")) {
				list(session, request, response);
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (CertificateException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		}  finally {
			JCRUtils.logout(session);
		}

	}
	
	private void initPostParameters(HttpServletRequest request) {
		try {
			postParam.clear();
			postFiles.clear();
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item : items) {
				if (item.isFormField()) {
					postParam.put(item.getFieldName(), item.getString("UTF-8"));
				} else {
					postFiles.add(item);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void create(Session session, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException, CertificateException{
		log.info("create({}, {}, {})", new Object[] { session, request, response });
		if (getBooleanParameter(request, "persist")) {
			// read certificate content from file
			InputStream is = postFiles.get(0).getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();

			// initialize the certificate and validate
			X509Certificate x509Certificate = CertificateUtil.getX509Certificate(sb.toString());
			x509Certificate.checkValidity();
			
			// make sure certificate not exists
			String usrId = getStringParameter(request, "uc_user");
			String sha1 = CertificateUtil.getCertificateSHA1(x509Certificate);
			UserCertificate savedCertificate = UserCertificateDAO.findByUser(usrId, sha1);
			if(savedCertificate != null){
				throw new java.security.cert.CertificateException("Certificate already exists!");
			}
			
			// save the new certificate
			UserCertificate uc = new UserCertificate();
			uc.setUser(usrId);
			uc.setCertHash(sha1);
			uc.setCertData(sb.toString());
			UserCertificateDAO.create(uc);
			
			// log user activity
            //UserActivity.log(session.getUserID(), "ADMIN_USER_CERTICICATE_CREATE", uc.getUser(), uc.toString());
		} else {
			ServletContext sc = getServletContext();
			sc.setAttribute("action", getStringParameter(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("uc_user", getStringParameter(request, "uc_user"));
			sc.getRequestDispatcher("/admin/user_certificate_add.jsp").forward(request, response);
		}
		log.debug("create: void");
	}

	private void delete(Session session, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("delete({}, {}, {})", new Object[] { session, request, response });
		int ucId = getIntParameter(request, "uc_id");
		UserCertificateDAO.delete(ucId);
		//UserActivity.log(session.getUserID(), "ADMIN_USER_CERTICICATE_DELETE", Integer.toString(ucId), null);
		list(session, request, response);
		log.debug("delete: void");
	}

	private void list(Session session, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		String usrId = getStringParameter(request, "uc_user");
		List<UserCertificate> userCertificates = new ArrayList<UserCertificate>();
		try {
			List<UserCertificate> savedCertificates = UserCertificateDAO.findByUser(usrId);
			if (savedCertificates!=null && savedCertificates.size()>0){
				for(UserCertificate certificate : savedCertificates){
					X509Certificate x509Certificate = CertificateUtil.getX509Certificate(certificate.getCertData());
					userCertificates.add(getUserCertificate(certificate.getId(), certificate.getUser(), x509Certificate));
				}
			}
		} catch (Exception e) {
		}
		sc.setAttribute("uc_user", usrId);
		sc.setAttribute("userCertificates", userCertificates);
		sc.getRequestDispatcher("/admin/user_certificate_list.jsp").forward(request, response);
		log.debug("list: void");
	}
	
	private boolean isMultipartRequest(HttpServletRequest request) {
		try {
			return request.getContentType() != null && request.getContentType().contains("multipart");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	private String getStringParameter(HttpServletRequest request, String name) {
		if (postParam.containsKey(name)) {
			String val = postParam.get(name);
			return val != null ? val : WebUtils.EMPTY_STRING;
		}
		return WebUtils.getString(request, name);
	}

	private int getIntParameter(HttpServletRequest request, String name){
		try {
			return new Integer(getStringParameter(request, name));
		} catch (Exception e) {
			return 0;
		}
	}
	
	private boolean getBooleanParameter(HttpServletRequest request, String name) {
		String strValue = getStringParameter(request, name);
		return (strValue != null && !strValue.equals(WebUtils.EMPTY_STRING) && !strValue.equals("false"));
	}
	
	private UserCertificate getUserCertificate(int id, String user, X509Certificate x509cert){
		UserCertificate cert = new UserCertificate();
		cert.setId(id);
		cert.setUser(user);
		cert.setValid(false);
		try {
			if (x509cert.getSerialNumber() != null ){
				cert.setSerialNr(x509cert.getSerialNumber().toString());
			}
			if (x509cert.getNotBefore()!=null){
				Calendar cal=Calendar.getInstance();
				cal.setTime(x509cert.getNotBefore());
				cert.setStartDate(cal);
			}
			if(x509cert.getNotAfter()!=null){
				Calendar cal=Calendar.getInstance();
				cal.setTime(x509cert.getNotAfter());
				cert.setEndDate(cal);
			}
			if (x509cert.getSubjectDN()!=null){
				cert.setSubjectDn(x509cert.getSubjectDN().getName());
			}
			if (x509cert.getIssuerDN()!=null){
				cert.setIssuerDn(x509cert.getIssuerDN().getName());	
			}
			x509cert.checkValidity();
			cert.setValid(true);
		} catch (Exception e) {
		}
		return cert;
	}
	
}
