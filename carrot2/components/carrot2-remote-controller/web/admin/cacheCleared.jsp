<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="
        com.dawidweiss.carrot.remote.controller.process.*,
        com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*,
        java.util.Iterator,
        com.dawidweiss.carrot.remote.controller.Carrot2InitServlet,
        com.dawidweiss.carrot.*,
        com.dawidweiss.carrot.util.common.*,
        com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers"
%>

<%@include file="/jsp-tmpl/prolog-infopage.txt" %>

<h1>Cache cleared.</h1>
<html:link target="_top" page="/admin/index.jsp">Back</html:link>

<%@include file="/jsp-tmpl/epilog-infopage.txt" %>