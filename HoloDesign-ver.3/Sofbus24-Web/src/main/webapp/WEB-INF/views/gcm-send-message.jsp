<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<head>

    <title>
    	<spring:message code="gcm-send-message.title" />
    </title>

	<script type="text/javascript">
		<%@ include file="/resources/js/jquery-1.10.1.min.js" %>
		<%@ include file="/resources/js/gcm-send-message.js" %>
	</script>

	<style type="text/css">
		<%@ include file="/resources/css/gcm-send-message.css" %>
	</style>
	
</head>
    
<t:mainpage-template>

	<jsp:body>
		<div id="top">
			<h1>
				<spring:message code="gcm-send-message.title" />
			</h1>
		</div>
		
		<fieldset>
			<form:form id="gcm-send-message" class="form" method="POST" modelAttribute="notification" action="send" >
				<form:label path="date">
					<spring:message code="gcm-send-message.label-date" />
					<span class="star"> *</span>
				</form:label>
				<div class="div_texbox">
					<form:input class="textbox" path="date" value="${notification.date}" disabled="true" />
					<span class="info">
						<spring:message code="gcm-send-message.date-info" />
					</span>
				</div>

				<form:label path="type">
					<spring:message code="gcm-send-message.label-type" />
					<span class="star"> *</span>
				</form:label>
				<div class="div_texbox">
					<form:select id="gcmType" path="type">
						<spring:message code="gcm-send-message.label-select-type" var="selectType"/>
						<form:option value="NONE" label="${selectType}" />
		   				<form:options items="${notificationTypes}" />
					</form:select>
					<span class="info">
						<spring:message code="gcm-send-message.type-info" />
					</span>
				</div>
				
				<form:label path="data">
					<spring:message code="gcm-send-message.label-data" />
					<span class="star"> *</span>
				</form:label>
				<div class="div_texbox">
					<form:input id="gcmData" class="textbox" path="data" />
					<span class="info">
						<spring:message code="gcm-send-message.data-info" />
					</span>
				</div>

				<div class="button_div">
					<spring:message code="gcm-send-message.label-reset" var="resetGcm" />
					<spring:message code="gcm-send-message.label-send" var="sendGcm" />
					<input id="submit" class="buttons" name="submit" type="submit" value="${sendGcm}" />
					<span></span>
					<input id="reset" class="buttons" name="reset" type="reset" value="${resetGcm}" />
				</div>
			</form:form>
				
			<div id="notif-msg-status">
				<span id="notif-msg-success">
					<spring:message code="gcm-send-message.notif-success"/>
				</span>
				<span id="notif-msg-failed">
					<spring:message code="gcm-send-message.notif-failed"/>
				</span>
			</div>
		</fieldset>
	</jsp:body>
	
</t:mainpage-template>