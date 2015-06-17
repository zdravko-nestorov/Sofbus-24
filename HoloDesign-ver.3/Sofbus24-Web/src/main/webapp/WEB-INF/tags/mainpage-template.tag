<%@tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@attribute name="header" fragment="true" %>
<%@attribute name="footer" fragment="true" %>

<html>

	<body class="main_page">
		<div id="menu">
			<jsp:include page="/WEB-INF/views/menu.jsp" />
		</div>
		
		<div id="content">
			<jsp:doBody />
		</div>
	</body>

</html>