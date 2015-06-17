<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<head>

	<title>
		<spring:message code="index.title" />
	</title>

	<script type="text/javascript">
		<%@ include file="/resources/js/jquery-1.10.1.min.js" %>
		<%@ include file="/resources/js/index.js" %>
	</script>

	<style type="text/css">
		<%@ include file="/resources/css/index.css" %>
	</style>

</head>

<t:mainpage-template>

	<jsp:body>
		<div id="top">
			<h1>
				<spring:message code="index.title"/>
			</h1>
		</div>
			
		<table>
		    <tr>
		    	<th>
		    		<spring:message code="index.gmail-id"/>
		    	</th>
		    	<th>
		    		<spring:message code="index.nickname"/>
		    	</th>
		    	<th>
		    		<spring:message code="index.email"/>
		    	</th>
		    	<th>
		    		<spring:message code="index.authorities"/>
		    	</th>
		    	<th>
		    		<spring:message code="index.registration-date"/>
		    	</th>
		    	<th>
		    		<spring:message code="index.last-online-date"/>
		    	</th>
		    </tr>
		    
		    <c:if test="${empty gmailUsersList}">
			    <tr>
			    	<td colspan="6">
			    		<spring:message code="index.empty-list"/>
			    	</td>
			    </tr>
		    </c:if>
		    
		    <c:if test="${not empty gmailUsersList}">
			    <c:forEach items="${gmailUsersList}" var="gmailUser">
				    <tr>
				    	<td>${gmailUser.gmailId}</td>
				    	<td>${gmailUser.nickname}</td>
				    	<td>${gmailUser.nickname}</td>
				    	<td>${gmailUser.authorities}</td>
				    	<td>${gmailUser.registrationDate}</td>
				    	<td>${gmailUser.lastOnlineDate}</td>
				    </tr>
		    	</c:forEach>
		    </c:if>
    	</table>
	</jsp:body>
	
</t:mainpage-template>