<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="css/style.css" />
<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
      $dm = $('.ds').openDOMWindow({
        height:300, width:400,
        eventType:'click',
        overlayOpacity: '57',
        windowSource:'iframe', windowPadding:0
      });
	});
    
    function dialogClose() {
    	$dm.closeDOMWindow();
    }
  </script>
<title>Repository Backup</title>
</head>
<body>
	<c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
	<c:choose>
		<c:when test="${isAdmin}">
			<ul id="breadcrumb">
				<li class="path"><a href="experimental.jsp">Experimental</a></li>
				<li class="path">Repository backup</li>
			</ul>
			<br />
			<form action="RepositoryBackup">
				<table class="form" align="center">
					<tr>
						<td>Filesystem path</td>
						<td><input type="text" size="50" name="fsPath" id="fsPath"
							value="" /></td>
						<td><a class="ds"
							href="../extension/DataBrowser?action=fs&sel=fld&dst=fsPath"><img
								src="img/action/browse_fs.png" /></a></td>
					</tr>
					<tr>
						<td colspan="3" align="right"><input type="submit"
							value="Send" /></td>
					</tr>
				</table>
			</form>
		</c:when>
		<c:otherwise>
			<div class="error">
				<h3>Only admin users allowed</h3>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>