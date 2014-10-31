<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.core.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <title>User Certificates</title>
</head>
<body>
  <c:set var="isAdmin"><%=request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="Auth" var="urlUserList">
      </c:url>
      <h1>User Certificates <span style="font-size: 10px;">(<a href="${urlUserList}">Users</a>)</span></h1>
      <table class="results">
        <tr>
          <th width="100px">SerialNr</th>
          <th>Issuer</th>
          <th>Subject</th>
          <th width="100px">ValidFrom</th>
          <th width="100px">ValidTo</th>
          <th width="100px">Validity</th>
          <th width="50px">
          	<c:url value="UserCertificates" var="urlCreate">
		   	  <c:param name="action" value="create"/>
		      <c:param name="uc_user" value="${uc_user}"/>
		    </c:url>
	        <a href="${urlCreate}"><img src="img/action/new.png" alt="Add Certificate" title="Add Certificate"/></a>
		  </th>
        </tr>
        <c:forEach var="cert" items="${userCertificates}" varStatus="row">
          <c:url value="UserCertificates" var="urlDelete">
            <c:param name="action" value="delete"/>
            <c:param name="uc_id" value="${cert.id}"/>
            <c:param name="uc_user" value="${uc_user}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td align="center">${cert.serialNr}</td>
            <td>${cert.issuerDn}</td>
            <td>${cert.subjectDn}</td>
            <td align="center"><fmt:formatDate value="${cert.startDate.time}" type="both"/></td>
            <td align="center"><fmt:formatDate value="${cert.endDate.time}" type="both"/></td>
            <td align="center">
              <c:choose>
                <c:when test="${cert.valid}">
                  <img src="img/true.png" alt="Active" title="Active"/>
                </c:when>
                <c:otherwise>
                  <img src="img/false.png" alt="Inactive" title="Inactive"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td align="center">
              <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
            </td>
          </tr>
        </c:forEach>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>