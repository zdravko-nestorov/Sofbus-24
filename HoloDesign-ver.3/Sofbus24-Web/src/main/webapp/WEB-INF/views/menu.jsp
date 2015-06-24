<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>
			<spring:message code="gcm-send-message.title"/>
		</title>

		<style type="text/css">
			<%@ include file="/resources/css/menu.css" %>
		</style>

		<script type="text/javascript">
			<%@ include file="/resources/js/jquery-1.10.1.min.js" %>
			<%@ include file="/resources/js/menu.js" %>
		</script>
	</head>

	<body>
		<div id="site-title">
			<h1>
				<spring:message code="sofbus.title" />
			</h1>
		</div>
	
		<div id="menu-outer">
			<div class="table">
				<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
				
				<ul id="nav">
					<sec:authorize access="hasRole('ADMIN') and isAuthenticated()">
						<li id="home" class="current">
							<a href="${contextPath}/index">
								<spring:message code="menu.home"/>
							</a>
						</li>
						<li id="gcm_push_notifications">
							<a href="#">
								<spring:message code="menu.gcm-notifications"/>
							</a>
							<ul>
								<li>
									<a href="${contextPath}/gcm/send">
										<spring:message code="menu.gcm-send-notification"/>
									</a>
								</li>
								<li>
									<a href="${contextPath}/gcm/users">
										<spring:message code="menu.gcm-registered-users"/>
									</a>
								</li>
							</ul>
						</li>
					</sec:authorize>
					<li id="about">
						<a href="${contextPath}/about">
							<spring:message code="menu.about"/>
						</a>
					</li>
					<li id="contact_us">
						<a href="${contextPath}/contact-us">
							<spring:message code="menu.contact-us"/>
						</a>
					</li>
					<li id="logout">
						<a href="${contextPath}/log-out">
							<spring:message code="menu.logout"/>
						</a>
					</li>
				</ul>
			</div>
		</div>
	</body>

</html>