package com.openkm.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see http://tech.top21.de/techblog/20110125-maintaining-sessions-with-ajax-polling-and-servlets.html
 * @author pavila
 */
public class SessionKeepAliveServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(SessionKeepAliveServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("Session keep alive poll from {}", request.getHeader("Referer"));
		
		// Access the session without creating it - this maintains the session
		request.getSession(false);
		
		// Send a 204
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}
