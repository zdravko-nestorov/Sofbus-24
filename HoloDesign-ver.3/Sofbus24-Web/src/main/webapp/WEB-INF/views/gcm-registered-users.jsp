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
		    
	    <c:if test="${empty phoneUsersList}">
	    	<div class="pagebanner">
	    		<span class="pagebanner well">No items found.</span>
	    	</div>
	    	<div class="pagination">
	    		<ul>
	    			<li class="prev disabled">
	    				<a href="#">← First</a>
	    			</li>
	    			<li class="prev disabled">
	    				<a href="#">← Previous</a>
	    			</li>
	    			<li class="active">
	    				<a href="#" title="Go to page 1">1</a>
	    			</li>
	    			<li class="next disabled">
	    				<a href="#">Next →</a>
	    			</li>
	    			<li class="disabled">
	    				<a href="#">Last →</a>
	    			</li>
	    		</ul>
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
			    <tr>
			    	<td colspan="5">
			    		<spring:message code="gcm-registered-users.empty-list"/>
			    	</td>
			    </tr>
			</table>
	    </c:if>
	    
	    <c:if test="${not empty phoneUsersList}">
		    <display:table name="phoneUsersList" id="phoneUser" requestURI="/gcm/users" pagesize="8">
			    <display:column titleKey="gcm-registered-users.reg-id" sortable="true">
			    	<c:out value="${fn:substring(phoneUser.regId, 0, 30)}..."/>
			    </display:column>
			    <display:column property="deviceModel" titleKey="gcm-registered-users.device-model" sortable="true" />
			    <display:column property="deviceOsVersion" titleKey="gcm-registered-users.device-os-version" sortable="true" />
			    <display:column property="registrationDate" titleKey="gcm-registered-users.registration-date" sortable="true" />
			    <display:column property="lastPushNotificationDate" titleKey="gcm-registered-users.last-push-notification-date" sortable="true" />
			</display:table>
		</c:if>
	</jsp:body>
	
</t:mainpage-template>