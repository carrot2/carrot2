<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="com.dawidweiss.carrot.controller.carrot2.*,
                com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query,
                com.dawidweiss.carrot.controller.carrot2.process.*,
                com.dawidweiss.carrot.controller.carrot2.struts.StrutsHelpers"
%><%@include file="../jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="blocks.recent-queries.head"/></h1>

<div style="margin-left: 2em;">
<%@include file="../jsp-tmpl/recent-queries.txt" %>
</div>

<%@include file="../jsp-tmpl/epilog-infopage.txt" %>
