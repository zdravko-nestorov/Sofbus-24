<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>

	<head>
		<title>
			<spring:message code="gcm-send-message.access-denied-title" />
		</title>
		<style type="text/css">
			<%@ include file="/resources/css/access-denied.css" %>
		</style>
	</head>
	
	<body>
		<img id="access-denied" src="/resources/images/access-denied.png" alt="" />
	</body>
	
</html>