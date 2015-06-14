<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>
        	<spring:message code="gcm-send-message.title" />
        </title>

		<script type="text/javascript">
			<%@ include file="/resources/js/jquery-1.10.1.min.js" %>
			<%@ include file="/resources/js/gcm-send-message.js" %>
		</script>
	
		<style>
			<%@ include file="/resources/css/gcm-send-message.css" %>
		</style>
    </head>
    
	<body>
		<div id="gcmdiv">
			<h1>
				<spring:message code="gcm-send-message.title" />
			</h1>
			
			<form:form method="POST" modelAttribute="notification" action="send">
				<form:label path="date">
					<spring:message code="gcm-send-message.label-date" />
				</form:label>
				<form:input path="date" value="${notification.date}" disabled="true" /><br/>
				
				<form:label path="type">
					<spring:message code="gcm-send-message.label-type" />
				</form:label>
				<form:select path="type" id="gcmType">
					<spring:message code="gcm-send-message.label-select-type" var="selectTtype"/>
					<form:option value="NONE" label="${selectTtype}" />
    				<form:options items="${notificationTypes}" />
				</form:select><br/>
				
				<form:label path="data">
					<spring:message code="gcm-send-message.label-data" />
				</form:label>
				<form:input path="data" id="gcmData" /><br/>
				
				<spring:message code="gcm-send-message.label-reset" var="resetGcm" />
				<spring:message code="gcm-send-message.label-send" var="sendGcm" />
				<input id="reset" type="reset" align="right" value="${resetGcm}" />
				<input id="submit" type="submit" align="right" value="${sendGcm}" />
			</form:form>
			
			<span id="notif-msg-success">
				<spring:message code="gcm-send-message.notif-success"/>
			</span>
			<span id="notif-msg-failed">
				<spring:message code="gcm-send-message.notif-failed"/>
			</span>
		</div>
    </body>
    
</html>