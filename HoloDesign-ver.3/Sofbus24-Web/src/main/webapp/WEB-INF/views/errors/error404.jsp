<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>

	<head>
		<title>
			<spring:message code="gcm-send-message.error404-title" />
		</title>
		<style type="text/css">
			<%@ include file="/resources/css/error404.css" %>
		</style>
	</head>
	
	<body>
		<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
		
		<a href="${contextPath}/">
			<img id="error404" src="/resources/images/error404.png" alt="" />
		</a>
	</body>
	
</html>