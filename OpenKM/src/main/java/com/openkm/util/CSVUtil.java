/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2014  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.util.Calendar;
import com.openkm.api.OKMSearch;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.QueryResult;
import com.openkm.bean.json.FindSimpleQueryValues;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.dao.bean.Translation;
import com.openkm.dao.bean.UserConfig;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.util.ISO8601;
import com.openkm.principal.PrincipalAdapterException;

/**
 * CSVUtil
 * 
 * @author jllort
 */
public class CSVUtil {
	
	/**
	 * createFind
	 */
	public static String createFind(String lang, String user, List<String[]> csvValues, String json, boolean compact)
			throws DatabaseException, PathNotFoundException, IOException, ParseException, RepositoryException,
			PrincipalAdapterException, NoSuchGroupException {
		Gson gson = new GsonBuilder().setDateFormat(ISO8601.BASIC_PATTER).create();
		String fileName = "";
		
		// Getting translations
		Map<String, String> translations = LanguageUtils.getTranslations(lang,
				new String[] { Translation.MODULE_FRONTEND });
		Profile up = new Profile();
		UserConfig uc = UserConfigDAO.findByPk(user);
		up = uc.getProfile();
		
		int cols = 0;
		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			cols++;
		}
		cols++; // Path column
		String[] columns = new String[cols];
		
		int index = 0;
		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type");
		}
		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.name");
		}
		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.size");
		}
		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.date.update");
		}
		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.author");
		}
		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.version");
		}
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.path");

		csvValues.add(columns);
		DateFormat sdf = new SimpleDateFormat(translations.get(Translation.MODULE_FRONTEND + "."+ "general.date.pattern"));
		fileName = sdf.format(Calendar.getInstance().getTime()) + "-find-export.csv";
		
		// Json conversion
		GWTQueryParams params = gson.fromJson(json, GWTQueryParams.class);
		for (QueryResult qr : OKMSearch.getInstance().find(null, GWTUtil.copy(params))) {
			csvValues.add(CSVUtil.toArray(qr, sdf, translations, cols, up, compact));
		}
		
		return fileName;
	}
	
	/**
	 * createFindSimpleQuery
	 */
	public static String createFindSimpleQuery(String lang, String user, List<String[]> csvValues, String json)
			throws DatabaseException, PathNotFoundException, IOException, ParseException, RepositoryException,
			PrincipalAdapterException, NoSuchGroupException {
		Gson gson = new GsonBuilder().setDateFormat(ISO8601.BASIC_PATTER).create();
		String fileName = "";
		
		// Getting translations
		Map<String, String> translations = LanguageUtils.getTranslations(lang, new String[] { Translation.MODULE_FRONTEND });
		
		Profile up = new Profile();
		UserConfig uc = UserConfigDAO.findByPk(user);
		up = uc.getProfile();
		
		int cols = 7;
		String[] columns = new String[cols];
		
		int index = 0;
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.name");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.size");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.date.update");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.author");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.version");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.path");

		csvValues.add(columns);
		DateFormat sdf = new SimpleDateFormat(translations.get(Translation.MODULE_FRONTEND + "."+ "general.date.pattern"));
		fileName = sdf.format(Calendar.getInstance().getTime()) + "-find-export.csv";
		
		// Json conversion
		FindSimpleQueryValues fqs = gson.fromJson(json, FindSimpleQueryValues.class);
		for (QueryResult qr : OKMSearch.getInstance().findSimpleQuery(null, fqs.getStatement())) {
			csvValues.add(CSVUtil.toArray(qr, sdf, translations, cols, up, false));
		}
		
		return fileName;
	}
	
	/**
	 * toArray
	 */
	private static String[] toArray(QueryResult qr, DateFormat dtf, Map<String, String> translations, int cols,
			Profile up, boolean compact) throws IOException, ParseException,
			NoSuchGroupException, PathNotFoundException, RepositoryException, DatabaseException,
			PrincipalAdapterException {
		String[] columns = new String[cols];
		if (qr.getDocument() != null) {
			Document doc = qr.getDocument();
			int col = 0;
			if (up.getPrfFileBrowser().isIconVisible() || !compact) {
				columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.document");
			}
			if (up.getPrfFileBrowser().isNameVisible() || !compact) {
				columns[col++] = PathUtils.getName(doc.getPath());
			}
			if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
				columns[col++] = FormatUtil.formatSize(doc.getActualVersion().getSize());
			}
			if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
				columns[col++] = dtf.format(doc.getActualVersion().getCreated().getTime());
			}
			if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
				columns[col++] = doc.getAuthor();
			}
			if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
				columns[col++] = doc.getActualVersion().getName();
			}
			columns[col++] = Config.APPLICATION_URL + "?uuid=" +doc.getUuid();
		} else if (qr.getAttachment() != null) {
			Document doc = qr.getAttachment();
			int col =0;
			if (up.getPrfFileBrowser().isIconVisible() || !compact) {
				columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.document");
			}
			if (up.getPrfFileBrowser().isNameVisible() || !compact) {
				columns[col++] = PathUtils.getName(doc.getPath());
			}
			if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
				columns[col++] = FormatUtil.formatSize(doc.getActualVersion().getSize());
			}
			if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
				columns[col++] = dtf.format(doc.getActualVersion().getCreated().getTime());
			}
			if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
				columns[col++] = doc.getAuthor();
			}
			if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
				columns[col++] = doc.getActualVersion().getName();
			}
			columns[col++] = Config.APPLICATION_URL + "?uuid="+ doc.getUuid();
		} else if (qr.getFolder() != null) {
			Folder fld = qr.getFolder();
			int col = 0;
			if (up.getPrfFileBrowser().isIconVisible() || !compact) {
				columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.folder");
			}
			if (up.getPrfFileBrowser().isNameVisible() || !compact) {
				columns[col++] = PathUtils.getName(fld.getPath());
			}
			if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
				columns[col++] = "";
			}
			if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
				columns[col++] = dtf.format(fld.getCreated().getTime());
			}
			if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
				columns[col++] = fld.getAuthor();
			}
			if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
				columns[col++] = "";
			}
			columns[col++] = Config.APPLICATION_URL + "?uuid=" + fld.getUuid();
		} else if (qr.getMail() != null) {
			Mail mail = qr.getMail();
			int col = 0;
			if (up.getPrfFileBrowser().isIconVisible() || !compact) {
				columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.mail");
			}
			if (up.getPrfFileBrowser().isNameVisible() || !compact) {
				columns[col++] = mail.getSubject();
			}
			if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
				columns[col++] = FormatUtil.formatSize(mail.getSize());
			}
			if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
				columns[col++] = dtf.format(mail.getCreated().getTime());
			}
			if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
				columns[col++] = mail.getAuthor();
			}
			if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
				columns[col++] = "";
			}
			columns[col++] = Config.APPLICATION_URL + "?uuid=" + mail.getUuid();

		}
		return columns;
	}
}