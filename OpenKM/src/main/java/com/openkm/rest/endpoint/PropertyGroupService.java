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

package com.openkm.rest.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.TextArea;
import com.openkm.core.Config;
import com.openkm.module.ModuleManager;
import com.openkm.module.PropertyGroupModule;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.FormElementComplexList;
import com.openkm.rest.util.PropertyGroupList;
import com.openkm.rest.util.SimplePropertyGroup;
import com.openkm.rest.util.SimplePropertyGroupList;
import com.openkm.ws.util.FormElementComplex;

@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class PropertyGroupService {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupService.class);
	
	@PUT
	@Path("/addGroup")
	public void addGroup(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName) throws GenericException {
		try {
			log.debug("addGroup({}, {})", new Object[] { nodeId, grpName });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			cm.addGroup(null, nodeId, grpName);
			log.debug("addGroup: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@DELETE
	@Path("/removeGroup")
	public void removeGroup(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName) throws GenericException {
		try {
			log.debug("removeGroup({}, {})", new Object[] { nodeId, grpName });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			cm.removeGroup(null, nodeId, grpName);
			log.debug("removeGroup: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getGroups")
	public PropertyGroupList getGroups(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("getGroups({})", nodeId);
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			PropertyGroupList pgl = new PropertyGroupList();
			pgl.getList().addAll(cm.getGroups(null, nodeId));
			log.debug("getGroups: {}", pgl);
			return pgl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getAllGroups")
	public PropertyGroupList getAllGroups() throws GenericException {
		try {
			log.debug("getAllGroups()");
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			PropertyGroupList pgl = new PropertyGroupList();
			pgl.getList().addAll(cm.getAllGroups(null));
			log.debug("getAllGroups: {} ", pgl);
			return pgl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getProperties")
	public FormElementComplexList getProperties(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName)
			throws GenericException {
		try {
			log.debug("getProperties({}, {})", new Object[] { nodeId, grpName });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			FormElementComplexList fecl = new FormElementComplexList();
			
			for (FormElement fe : cm.getProperties(null, nodeId, grpName)) {
				FormElementComplex fec = FormElementComplex.toFormElementComplex(fe);
				fecl.getList().add(fec);
			}
			
			log.debug("getProperties: {}", fecl);
			return fecl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getPropertyGroupForm")
	public FormElementComplexList getPropertyGroupForm(@QueryParam("grpName") String grpName)
			throws GenericException {
		try {
			log.debug("getPropertyGroupForm({})", new Object[] { grpName });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			FormElementComplexList fecl = new FormElementComplexList();
			
			for (FormElement fe : cm.getPropertyGroupForm(null, grpName)) {
				FormElementComplex fec = FormElementComplex.toFormElementComplex(fe);
				fecl.getList().add(fec);
			}
			
			log.debug("getPropertyGroupForm: {}", fecl);
			return fecl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@PUT
	@Path("/setProperties")
	// The "properties" parameter comes in the POST request body (encoded as XML or JSON).
	public void setProperties(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName, FormElementComplexList properties)
			throws GenericException {
		try {
			log.debug("setProperties({}, {}, {})", new Object[] { nodeId, grpName, properties });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			List<FormElement> al = new ArrayList<FormElement>();
			
			for (FormElementComplex fec : properties.getList()) {
				al.add(FormElementComplex.toFormElement(fec));
			}
			
			cm.setProperties(null, nodeId, grpName, al);
			log.debug("setProperties: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@PUT
	@Path("/setPropertiesSimple")
	// The "properties" parameter comes in the POST request body (encoded as XML or JSON).
	public void setPropertiesSimple(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName,
			SimplePropertyGroupList properties) throws GenericException {
		try {
			log.debug("setPropertiesSimple({}, {}, {})", new Object[] { nodeId, grpName, properties });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			List<FormElement> al = new ArrayList<FormElement>();
			HashMap<String, String> mapProps = new HashMap<String, String>();
			
			// Unmarshall
			for (SimplePropertyGroup spg : properties.getList()) {
				mapProps.put(spg.getName(), spg.getValue());
			}
			
			for (FormElement fe : cm.getProperties(null, nodeId, grpName)) {
				String value = mapProps.get(fe.getName());
				
				if (value != null) {
					if (fe instanceof Input) {
						((Input) fe).setValue(value);
					} else if (fe instanceof SuggestBox) {
						((SuggestBox) fe).setValue(value);
					} else if (fe instanceof TextArea) {
						((TextArea) fe).setValue(value);
					} else if (fe instanceof CheckBox) {
						((CheckBox) fe).setValue(Boolean.valueOf(value));
					} else if (fe instanceof Select) {
						Select sel = (Select) fe;

						for (Option opt : sel.getOptions()) {
							StringTokenizer st = new StringTokenizer(value, Config.LIST_SEPARATOR);
							
							while (st.hasMoreTokens()) {
								String optVal = st.nextToken().trim();
							
								if (opt.getValue().equals(optVal)) {
									opt.setSelected(true);
									break;
								} else {
									opt.setSelected(false);
								}
							}
						}
					}
					
					al.add(fe);
				}
			}
			
			cm.setProperties(null, nodeId, grpName, al);
			log.debug("setPropertiesSimple: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/hasGroup")
	public boolean hasGroup(@QueryParam("nodeId") String nodeId, @QueryParam("grpName") String grpName) throws GenericException {
		try {
			log.debug("hasGroup({}, {})", new Object[] { nodeId, grpName });
			PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
			boolean ret = cm.hasGroup(null, nodeId, grpName);
			log.debug("hasGroup: {}", ret);
			return ret;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
