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
		<%@ include file="/resources/css/tooltip.css" %>
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
	    		<span class="pagebanner well">
	    			<spring:message code="display-table.no-items-found" />
	    		</span>
	    	</div>
	    	<div class="pagination">
	    		<ul>
	    			<li class="prev disabled">
	    				<a href="#">
	    					<spring:message code="display-table.first" />
	    				</a>
	    			</li>
	    			<li class="prev disabled">
	    				<a href="#">
	    					<spring:message code="display-table.previous" />
	    				</a>
	    			</li>
	    			<li class="active">
	    				<a href="#" title="Go to page 1">1</a>
	    			</li>
	    			<li class="next disabled">
	    				<a href="#">
	    					<spring:message code="display-table.next" />
	    				</a>
	    			</li>
	    			<li class="disabled">
	    				<a href="#">
	    					<spring:message code="display-table.last" />
	    				</a>
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
		    	<display:setProperty name="paging.banner.no_items_found">
		    		<div class="pagebanner">
		    			<span class="pagebanner well">
							<spring:message code="display-table.no-found" />
						</span>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.one_item_found">
		    		<div class="pagebanner">
		    			<span class="pagebanner well">
		    				<spring:message code="display-table.one-found" />
		    			</span>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.all_items_found">
		    		<div class="pagebanner">
		    			<span class="pagebanner well">
		    				<spring:message code="display-table.many-found-all" />
		    			</span>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.some_items_found">
		    		<div class="pagebanner">
		    			<span class="pagebanner well">
		    				<spring:message code="display-table.many-found-some" />
		    			</span>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.full">
		    		<div class="pagination">
		    			<ul>
		    				<li class="prev">
		    					<a href="{1}">
									<spring:message code="display-table.first" />
								</a>
		    				</li>
		    				<li class="prev">
		    					<a href="{2}">
		    						<spring:message code="display-table.previous" />
		    					</a>
		    				</li>{0}
		    				<li class="next">
		    					<a href="{3}">
		    						<spring:message code="display-table.next" />
		    					</a>
		    				</li>
		    				<li class="next">
		    					<a href="{4}">
		    						<spring:message code="display-table.last" />
		    					</a>
		    				</li>
		    			</ul>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.first">
		    		<div class="pagination">
		    			<ul>
		    				<li class="prev disabled">
		    					<a href="{1}">
									<spring:message code="display-table.first" />
								</a>
		    				</li>
		    				<li class="prev disabled">
		    					<a href="{2}">
		    						<spring:message code="display-table.previous" />
		    					</a>
		    				</li>{0}
		    				<li class="next">
		    					<a href="{3}">
		    						<spring:message code="display-table.next" />
		    					</a>
		    				</li>
		    				<li class="next">
		    					<a href="{4}">
		    						<spring:message code="display-table.last" />
		    					</a>
		    				</li>
		    			</ul>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.last">
		    		<div class="pagination">
		    			<ul>
		    				<li class="prev">
		    					<a href="{1}">
									<spring:message code="display-table.first" />
								</a>
		    				</li>
		    				<li class="prev">
		    					<a href="{2}">
		    						<spring:message code="display-table.previous" />
		    					</a>
		    				</li>{0}
		    				<li class="next disabled">
		    					<a href="{3}">
		    						<spring:message code="display-table.next" />
		    					</a>
		    				</li>
		    				<li class="disabled">
		    					<a href="{4}">
		    						<spring:message code="display-table.last" />
		    					</a>
		    				</li>
		    			</ul>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.onepage">
		    		<div class="pagination">
		    			<ul>
		    				<li class="prev disabled">
		    					<a href="{1}">
									<spring:message code="display-table.first" />
								</a>
		    				</li>
		    				<li class="prev disabled">
		    					<a href="{2}">
		    						<spring:message code="display-table.previous" />
		    					</a>
		    				</li>{0}
		    				<li class="next disabled">
		    					<a href="{3}">
		    						<spring:message code="display-table.next" />
		    					</a>
		    				</li>
		    				<li class="disabled">
		    					<a href="{4}">
		    						<spring:message code="display-table.last" />
		    					</a>
		    				</li>
		    			</ul>
		    		</div>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.page.selected">
		    		<li class="active">
		    			<a href="{1}" title="<spring:message code="display-table.go-to-page" />">{0}</a>
		    		</li>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.page.link">
		    		<li>
		    			<a href="{1}" title="<spring:message code="display-table.go-to-page" />">{0}</a>
		    		</li>
		    	</display:setProperty>
		    	<display:setProperty name="paging.banner.page.separator" value="" />
			    <display:column titleKey="gcm-registered-users.reg-id" sortable="true">
			    	<div class="tooltip" title="${phoneUser.regId}">
			    		<span title="">
			    			<c:out value="${fn:substring(phoneUser.regId, 0, 30)}..."/>
			    		</span>
			    	</div>
			    </display:column>
			    <display:column property="deviceModel" titleKey="gcm-registered-users.device-model" sortable="true" />
			    <display:column property="deviceOsVersion" titleKey="gcm-registered-users.device-os-version" sortable="true" />
			    <display:column property="registrationDate" titleKey="gcm-registered-users.registration-date" sortable="true" />
			    <display:column property="lastPushNotificationDate" titleKey="gcm-registered-users.last-push-notification-date" sortable="true" />
			</display:table>
		</c:if>
	</jsp:body>
	
</t:mainpage-template>