<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>

	<head>
		<title>
			<spring:message code="gcm-send-message.title" />
		</title>
		<style type="text/css">
			<%@ include file="/resources/css/error404.css" %>
		</style>
	</head>
	
	<body>
		<img id="error404" src="<c:url value="/resources/images/error404.png" />" alt="" />
	</body>
	
</html>