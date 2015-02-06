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

package com.openkm.cmis;

import java.util.HashMap;

import javax.servlet.ServletContext;

import org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

public class CmisLifecycleBean implements ServletContextAware, InitializingBean, DisposableBean {
	private ServletContext servletContext;
	private CmisServiceFactory factory;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setCmisServiceFactory(CmisServiceFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (factory != null) {
			factory.init(new HashMap<String, String>());
			servletContext.setAttribute(CmisRepositoryContextListener.SERVICES_FACTORY, factory);
		}
	}
	
	@Override
	public void destroy() throws Exception {
		if (factory != null) {
			factory.destroy();
		}
	}
}
