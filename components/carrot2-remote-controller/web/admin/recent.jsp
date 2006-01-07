<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="com.dawidweiss.carrot.remote.controller.*,
                com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query,
                com.dawidweiss.carrot.remote.controller.process.*,
                com.dawidweiss.carrot.util.net.*,
                com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers"
%><%@include file="/jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="blocks.recent-queries.head"/></h1>

<div style="margin-left: 2em;">
<%
	int num = 20;
%>
<%@include file="/jsp-tmpl/recent-queries.txt" %>
</div>

<%@include file="/jsp-tmpl/epilog-infopage.txt" %>
