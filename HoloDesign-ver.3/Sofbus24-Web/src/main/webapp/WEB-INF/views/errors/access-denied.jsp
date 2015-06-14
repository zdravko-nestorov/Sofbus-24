<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>

	<head>
		<title>
			<spring:message code="gcm-send-message.title" />
		</title>
		<style type="text/css">
			<%@ include file="/resources/css/access-denied.css" %>
		</style>
	</head>
	
	<body>
		<img id="access-denied" src="<c:url value="/resources/images/access-denied.png" />" alt="" />
	</body>
	
</html>