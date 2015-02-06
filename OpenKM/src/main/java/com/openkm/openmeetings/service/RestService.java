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
package com.openkm.openmeetings.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * RestService
 * 
 * @author jllort
 * 
 */
public class RestService {
	/**
	 * call
	 * 
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Element> callMap(String request, Object param) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = null;
		try {
			method = new GetMethod(getEncodetURI(request).toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		int statusCode = 0;
		try {
			statusCode = client.executeMethod(method);
		} catch (HttpException e) {
			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration.");
		} catch (IOException e) {
			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration.");
		}
		
		switch (statusCode) {
			case 200: // OK
				break;
			case 400:
				throw new Exception(
						"Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect.");
			case 403:
				throw new Exception(
						"Forbidden. You do not have permission to access this resource, or are over your rate limit.");
			case 503:
				throw new Exception("Service unavailable. An internal problem prevented us from returning data to you.");
			default:
				throw new Exception("Your call to OpenMeetings! Web Services returned an unexpected  HTTP status of: "
						+ statusCode);
		}
		
		InputStream rstream = null;
		try {
			rstream = method.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("No Response Body");
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
		SAXReader reader = new SAXReader();
		Document document = null;
		String line;
		try {
			while ((line = br.readLine()) != null) {
				document = reader.read(new ByteArrayInputStream(line.getBytes("UTF-8")));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception("UnsupportedEncodingException by SAXReader");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("IOException by SAXReader in REST Service");
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new Exception("DocumentException by SAXReader in REST Service");
		} finally {
			br.close();
		}
		
		Element root = document.getRootElement();
		Map<String, Element> elementMap = new LinkedHashMap<String, Element>();
		
		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element item = it.next();
			if (item.getNamespacePrefix() == "soapenv") {
				throw new Exception(item.getData().toString());
			}
			String nodeVal = item.getName();
			elementMap.put(nodeVal, item);
		}
		
		return elementMap;
	}
	
	/**
	 * call
	 * 
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static List<Element> callList(String request, Object param) throws Exception {
		HttpClient client = new HttpClient();
		GetMethod method = null;
		try {
			method = new GetMethod(getEncodetURI(request).toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		int statusCode = 0;
		try {
			statusCode = client.executeMethod(method);
		} catch (HttpException e) {
			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration.");
		} catch (IOException e) {
			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration.");
		}
		
		switch (statusCode) {
			case 200: // OK
				break;
			case 400:
				throw new Exception(
						"Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect.");
			case 403:
				throw new Exception(
						"Forbidden. You do not have permission to access this resource, or are over your rate limit.");
			case 503:
				throw new Exception("Service unavailable. An internal problem prevented us from returning data to you.");
			default:
				throw new Exception("Your call to OpenMeetings! Web Services returned an unexpected  HTTP status of: "
						+ statusCode);
		}
		
		InputStream rstream = null;
		try {
			rstream = method.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("No Response Body");
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
		SAXReader reader = new SAXReader();
		Document document = null;
		String line;
		try {
			while ((line = br.readLine()) != null) {
				document = reader.read(new ByteArrayInputStream(line.getBytes("UTF-8")));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new Exception("UnsupportedEncodingException by SAXReader");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("IOException by SAXReader in REST Service");
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new Exception("DocumentException by SAXReader in REST Service");
		} finally {
			br.close();
		}
		
		Element root = document.getRootElement();
		List<Element> elementList = new ArrayList<Element>();
		
		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element item = it.next();
			if (item.getNamespacePrefix() == "soapenv") {
				throw new Exception(item.getData().toString());
			}
			elementList.add(item);
		}
		
		return elementList;
	}
	
	/**
	 * getEncodetURI
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	private static String getEncodetURI(String url) throws MalformedURLException, UnsupportedEncodingException {
		return new URL(url).toString().replaceAll(" ", "%20");
	}
}