<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="com.dawidweiss.carrot.controller.carrot2.struts.StrutsHelpers,com.dawidweiss.carrot.controller.carrot2.Carrot2InitServlet,com.dawidweiss.carrot.controller.carrot2.process.*"
%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%-- Any processing errors? --%>
  
<%@include file="jsp-tmpl/prolog.txt" %>

<logic:present name="processingErrors" scope="request">
    <!-- This comment required for test methods. do not remove: *C2TESTSERRORPAGE* -->
    <table cellspacing="0" cellpadding="0" width="100%">
    <tr><td align="center" valign="middle">
        <div class="frame" style="width: 80%; text-align: left;">
            <h1><bean:message key="errors.processing-errors.head" /></h1>
            <p><bean:message key="errors.processing-errors.unknown" /></p>

            <ul>
                <logic:iterate id="error" name="processingErrors" scope="request" type="Throwable">
                    <li><%
                            String x = "Exception: " + error.toString();
                            String reformatted = "";
                            while (x.indexOf(": ") > 0)
                            {
                                reformatted += x.substring(0, x.indexOf(": "))
                                              + ": <div style='margin: 0px; padding: 0px; margin-left: 10px;'>";
                                x = x.substring( x.indexOf(": ") + 2 )
                                              + "</div>";
                            }
                            reformatted += x;

                            out.write(StrutsHelpers.getMessageOrDefault(pageContext,
                                error.getMessage(), reformatted)); %></li>
                </logic:iterate>
            </ul>
        </div>
    </td></tr>
    </table>
</logic:present>

<logic:present name="debugInfo" scope="request">
    <center>
    <table cellspacing="0" cellpadding="0">
    <tr><td align="center" valign="middle">
    <div style="text-align: left; margin-top: 10px; margin-bottom: 10px; padding: 4px; border: 1px solid gray;">
        <%= ((DebugQueryExecutionInfo) request.getAttribute("debugInfo")).getDebugLogAsHTML() %>
    </div>
    </td>
    </tr>
    </table>
    </center>
</logic:present>

<%@include file="jsp-tmpl/epilog.txt" %>

