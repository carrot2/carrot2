<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="com.dawidweiss.carrot.controller.carrot2.struts.StrutsHelpers,com.dawidweiss.carrot.controller.carrot2.Carrot2InitServlet,com.dawidweiss.carrot.controller.carrot2.process.*"
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
