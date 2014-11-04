package com.openkm.util;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertificateUtil {

	public static X509Certificate getX509Certificate(String content) throws CertificateException {
		content = content.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
		
		Pattern patternBegin = Pattern.compile("-+BEGIN*CERTIFICATE-+\\r?\\n?");
		Matcher matcherBegin = patternBegin.matcher(content);
		if (matcherBegin.find()) {
			content = content.substring(matcherBegin.end());
		}
		Pattern patternEnd = Pattern.compile("-+END*CERTIFICATE-+\\r?\\n?");
		Matcher matcherEnd = patternEnd.matcher(content);
		if (matcherEnd.find()) {
			content = content.substring(0, matcherEnd.start());
		}
		StringBuilder certContent = new StringBuilder();
		certContent.append("-----BEGIN CERTIFICATE-----");
		certContent.append("\n").append(content).append("\n");
		certContent.append("-----END CERTIFICATE-----");
		
		ByteArrayInputStream ksbufin = new ByteArrayInputStream(certContent.toString().getBytes());
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		return (X509Certificate) certFactory.generateCertificate(ksbufin);
	}

	public static String getCertificateSHA1(X509Certificate x509Certificate) throws NoSuchAlgorithmException {
		 MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		 messageDigest.update(x509Certificate.getPublicKey().toString().getBytes());
		 return SecureStore.b64Encode(messageDigest.digest());
	}
	
}
