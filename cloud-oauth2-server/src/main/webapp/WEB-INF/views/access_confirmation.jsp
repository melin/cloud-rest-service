<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="org.springframework.security.core.AuthenticationException"%>
<%@ page
	import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter"%>
<%@ page
	import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException"%>
<%@ taglib prefix="authz"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
	<title>cloud-oauth2-server</title>
	<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/default.css"/>" />
</head>

<body>
	<h1>cloud-oauth2-server</h1>

	<div id="content">

		<%
			if (session
					.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null
					&& !(session
							.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) instanceof UnapprovedClientAuthenticationException)) {
		%>
		<div class="error">
			<h2>Woops!</h2>

			<p>
				Access could not be granted. (<%=((AuthenticationException) session
						.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY))
						.getMessage()%>)
			</p>
		</div>
		<%
			}
		%>
		<c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION" />

		<p>
			clientId：<c:out value="${client.clientId}" />
		</p>
		<ul>
			<c:forEach var="scope" items="${auth_request.scope}">
				<li>${scope}</li>
			</c:forEach>
		</ul>



		<form id="confirmationForm" name="confirmationForm"
			action="<%=request.getContextPath()%>/oauth/authorize" method="post">
			<input name="user_oauth_approval" value="true" type="hidden" /> <label><input
				name="authorize" value="授权" type="submit" /></label>
		</form>
		<form id="denialForm" name="denialForm"
			action="<%=request.getContextPath()%>/oauth/authorize" method="post">
			<input name="user_oauth_approval" value="false" type="hidden" /> <label><input
				name="deny" value="拒绝" type="submit" /></label>
		</form>
	</div>
</body>
</html>
