<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<head>

	<title>
		<spring:message code="about.title" />
	</title>

	<style type="text/css">
		<%@ include file="/resources/css/about.css" %>
	</style>

</head>

<t:mainpage-template>

	<jsp:body>
		<div id="about-content">
			<fieldset>
				<legend id="about-title">
					<spring:message code="about.title"/>
				</legend>
				
				<textarea readonly="readonly"><spring:message code="about.content"/></textarea>
			</fieldset>
		</div>
	</jsp:body>
	
</t:mainpage-template>