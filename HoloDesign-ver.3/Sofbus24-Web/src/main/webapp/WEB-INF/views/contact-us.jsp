<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<head>

	<title>
		<spring:message code="contact-us.title" />
	</title>

	<style type="text/css">
		<%@ include file="/resources/css/contact-us.css" %>
	</style>

	<script type="text/javascript">
		<%@ include file="/resources/js/jquery-1.10.1.min.js" %>
		<%@ include file="/resources/js/contact-us.js" %>
	</script>

</head>

<t:mainpage-template>

	<jsp:body>
		<form action="contact-us" method="post" id="contact-form">
			<fieldset>
				<label for="name">
					<spring:message code="contact-us.name"/>
					<input type="text" name="name" id="name" value="${user.nickname}" />
				</label>
		    	<label for="email">
		    		<spring:message code="contact-us.email"/>
		    		<input type="text" name="email" id="email" value="${user.email}" disabled="true" />
		    	</label>
		    	<label for="subject">
		    		<spring:message code="contact-us.subjct"/>
		    		<input type="text" name="subject" id="subject" />
		    	</label>    
			</fieldset>
			
			<fieldset>
				<label for="msg">
					<spring:message code="contact-us.message"/>
			    	<textarea name="msg" id="msg"></textarea>
			    </label>
			</fieldset>
			
			<fieldset>
				<input id="submit" class="buttons" type="submit" value="<spring:message code="contact-us.button"/>" />
			</fieldset>
		</form>
	</jsp:body>
	
</t:mainpage-template>