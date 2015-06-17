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
		<div id="container">
			<div id="top">
				<spring:message code="gcm-send-message.title" />
			</div>
	
			<div id="leftSide">
				<fieldset>
					<form id="gcm-send-message" class="form" method='POST' action="send" >
						<label for="date">
							<spring:message code="gcm-send-message.label-date" />
						</label>
						<div class="div_texbox">
							<input path="date" value="${notification.date}" disabled="true" />
						</div>
	
						<label for="type">
							<spring:message code="gcm-send-message.label-type" />
						</label>
						<div class="div_texbox">
							<select path="type" id="gcmType">
								<spring:message code="gcm-send-message.label-select-type" var="selectType"/>
								<option value="NONE" label="${selectType}" />
				   				<options items="${notificationTypes}" />
							</select>
						</div>
						
						<label for="data">
							<spring:message code="gcm-send-message.label-data" />
						</label>
						<div class="div_texbox">
							<input path="data" id="gcmData" />
						</div>
	
						<div class="button_div">
							<input name="submit" type="submit" class="buttons" value="<spring:message code="gcm-send-message.label-reset"/>" />
							<span></span>
							<input name="reset" type="reset" class="buttons" value="<spring:message code="gcm-send-message.label-send"/>" />
						</div>
					</form>
				</fieldset>
			</div>
		</div>
	</jsp:body>
	
</t:mainpage-template>