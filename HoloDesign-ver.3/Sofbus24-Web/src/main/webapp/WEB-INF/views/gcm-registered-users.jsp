<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	</style>

</head>

<t:mainpage-template>

	<jsp:body>
		<div id="top">
			<h1>
				<spring:message code="gcm-registered-users.title"/>
			</h1>
		</div>
			
		<table>
		    <tr>
		    	<th>
		    		<spring:message code="gcm-registered-users.reg-id"/>
		    	</th>
		    	<th>
		    		<spring:message code="gcm-registered-users.device-model"/>
		    	</th>
		    	<th>
		    		<spring:message code="gcm-registered-users.device-os-version"/>
		    	</th>
		    	<th>
		    		<spring:message code="gcm-registered-users.registration-date"/>
		    	</th>
		    	<th>
		    		<spring:message code="gcm-registered-users.last-push-notification-date"/>
		    	</th>
		    </tr>
		    
		    <c:if test="${empty phoneUsersList}">
			    <tr>
			    	<td colspan="5">
			    		<spring:message code="gcm-registered-users.empty-list"/>
			    	</td>
			    </tr>
		    </c:if>
		    
		    <c:if test="${not empty phoneUsersList}">
			    <c:forEach items="${phoneUsersList}" var="phoneUser">
				    <tr>
				    	<td>${phoneUser.regId}</td>
				    	<td>${phoneUser.deviceModel}</td>
				    	<td>${phoneUser.deviceOsVersion}</td>
				    	<td>${phoneUser.registrationDate}</td>
				    	<td>${phoneUser.lastPushNotificationDate}</td>
				    </tr>
		    	</c:forEach>
		    </c:if>
    	</table>
	</jsp:body>
	
</t:mainpage-template>