<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u"%>
<fieldset>
	<legend>File browser</legend>
	<table>
		<tr>
			<td>Width</td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_status_width" size="3" maxlength="3"
				type="text" value="${prf.prfFileBrowser.statusWidth}" />
			</td>
			<td>Status</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.statusVisible}">
						<input name="prf_filebrowser_status_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_status_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_massive_width" size="3" maxlength="3"
				type="text" value="${prf.prfFileBrowser.massiveWidth}" />
			</td>
			<td>Massive</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.massiveVisible}">
						<input name="prf_filebrowser_massive_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_massive_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_icon_width" size="3" maxlength="3" type="text"
				value="${prf.prfFileBrowser.iconWidth}" />
			</td>
			<td>Icon</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.iconVisible}">
						<input name="prf_filebrowser_icon_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_icon_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_name_width" size="3" maxlength="3" type="text"
				value="${prf.prfFileBrowser.nameWidth}" />
			</td>
			<td>Name</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.nameVisible}">
						<input name="prf_filebrowser_name_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_name_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_size_width" size="3" maxlength="3" type="text"
				value="${prf.prfFileBrowser.sizeWidth}" />
			</td>
			<td>Size</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.sizeVisible}">
						<input name="prf_filebrowser_size_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_size_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_lastmod_width" size="3" maxlength="3"
				type="text" value="${prf.prfFileBrowser.lastModifiedWidth}" />
			</td>
			<td>Last modified</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.lastModifiedVisible}">
						<input name="prf_filebrowser_lastmod_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_lastmod_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_author_width" size="3" maxlength="3"
				type="text" value="${prf.prfFileBrowser.authorWidth}" />
			</td>
			<td>Author</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.authorVisible}">
						<input name="prf_filebrowser_author_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_author_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<td>
				<div id="error_integer" style="display: none; color: red;">Expected
					integer.</div> <input
				class=":required;; :integer;;error_integer :only_on_blur"
				name="prf_filebrowser_version_width" size="3" maxlength="3"
				type="text" value="${prf.prfFileBrowser.versionWidth}" />
			</td>
			<td>Version</td>
			<td><c:choose>
					<c:when test="${prf.prfFileBrowser.versionVisible}">
						<input name="prf_filebrowser_version_visible" type="checkbox"
							checked="checked" />
					</c:when>
					<c:otherwise>
						<input name="prf_filebrowser_version_visible" type="checkbox" />
					</c:otherwise>
				</c:choose></td>
		</tr>
	</table>
</fieldset>