<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
	<title>Spring Oauth2实例</title>
	<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/default.css"/>" />
</head>

<body>

	<h1>用户登录</h1>

	<div id="content">
		<c:if test="${not empty param.authentication_error}">
			<h1>Woops!</h1>

			<p class="error">登录失败</p>
		</c:if>
		<c:if test="${not empty param.authorization_error}">
			<h1>Woops!</h1>

			<p class="error">你没有权限访问资源</p>
		</c:if>

		<form id="loginForm" name="loginForm" action="<c:url value="/login.do"/>" method="post">
			<p>
				<label>Username: <input type='text' name='j_username' value="huali" /></label>
			</p>
			<p>
				<label>Password: <input type="password" name='j_password' value="111111" /></label>
			</p>
			<p>
				<input name="login" value="Login" type="submit" />
			</p>
		</form>
	</div>

</body>
</html>
