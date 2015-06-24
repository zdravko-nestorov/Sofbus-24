<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
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
		<%@ include file="/resources/css/display-table.css" %>
	</style>

</head>

<t:mainpage-template>

	<jsp:body>
		<div id="top">
			<h1>
				<spring:message code="index.title"/>
			</h1>
		</div>
	    
	    <display:table name="gmailUsersList" requestURI="/index" pagesize="8">
	    	<display:setProperty name="sort.amount" value="list" />
	   	 	<display:setProperty name="basic.msg.empty_list" value="index.empty-list" />
		    <display:column property="gmailId" titleKey="index.gmail-id" sortable="true" />
		    <display:column property="nickname" titleKey="index.nickname" sortable="true" />
		    <display:column property="nickname" titleKey="index.email" sortable="true" />
		    <display:column property="authorities" titleKey="index.authorities" sortable="true" />
		    <display:column property="registrationDate" titleKey="index.registration-date" sortable="true" />
		    <display:column property="lastOnlineDate" titleKey="index.last-online-date" sortable="true" />
		</display:table>
	</jsp:body>
	
</t:mainpage-template>