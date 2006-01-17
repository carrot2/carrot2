<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="
        com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers,
        com.dawidweiss.carrot.remote.controller.Carrot2InitServlet,
        com.dawidweiss.carrot.remote.controller.process.*"
%><%@include file="jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="blocks.whatis.whatis" /></h1>

<bean:message key="blocks.whatis.info"/>

<%@include file="jsp-tmpl/epilog.txt" %>
