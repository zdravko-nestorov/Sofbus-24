<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<head>

	<title>
		<spring:message code="gcm-registered-users.title" />
	</title>

	<script type="text/javascript">
		<%@ include file="/resources/js/jquery-1.10.1.min.js" %>
		<%@ include file="/resources/js/gcm-registered-users.js" %>
	</script>

	<style type="text/css">
		<%@ include file="/resources/css/gcm-registered-users.css" %>
		<%@ include file="/resources/css/display-table.css" %>
	</style>

</head>

<t:mainpage-template>

	<jsp:body>
		<div id="top">
			<h1>
				<spring:message code="gcm-registered-users.title"/>
			</h1>
		</div>
	    
	    <display:table name="phoneUsersList" requestURI="/gcm/users" pagesize="8">
	   	 	<display:setProperty name="basic.msg.empty_list" value="gcm-registered-users.empty-list" />
		    <display:column property="regId" title="regId" titleKey="gcm-registered-users.reg-id" sortable="true" />
		    <display:column property="deviceModel" titleKey="gcm-registered-users.device-model" sortable="true" />
		    <display:column property="deviceOsVersion" titleKey="gcm-registered-users.device-os-version" sortable="true" />
		    <display:column property="registrationDate" titleKey="gcm-registered-users.registration-date" sortable="true" />
		    <display:column property="lastPushNotificationDate" titleKey="gcm-registered-users.last-push-notification-date" sortable="true" />
		</display:table>
	</jsp:body>
	
</t:mainpage-template>