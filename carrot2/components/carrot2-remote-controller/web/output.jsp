<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="
        com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers,
        com.dawidweiss.carrot.remote.controller.Carrot2InitServlet,
        com.dawidweiss.carrot.remote.controller.process.*"
%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%-- ### Query results (if query exists) --%>

<logic:notPresent name="processingErrors" scope="request">
    <logic:present name="queryResults" scope="request">
        <%
        	out.clearBuffer();
        	out.print(request.getAttribute("queryResults").toString());
        %>
    </logic:present>
</logic:notPresent>
