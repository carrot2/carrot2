<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="java.util.*,com.dawidweiss.carrot.controller.carrot2.struts.*,com.dawidweiss.carrot.controller.carrot2.process.*,com.dawidweiss.carrot.controller.carrot2.components.*,com.dawidweiss.carrot.controller.carrot2.Carrot2InitServlet"
%>

<!-- ### Include page top ### -->

<%@include file="jsp-tmpl/prolog.txt" %>

<!-- ### Initialize query bean (request scope) ### -->
<jsp:useBean id="queryBean" scope="request" class="com.dawidweiss.carrot.controller.carrot2.struts.forms.QueryForm" />
<jsp:setProperty name="queryBean" property="*" />
<%
    if (!queryBean.isInitialized()) queryBean.initialize(application);
%>

<!-- ### Top query banner (horizontal) -->

<html:form action="/search.do" focus="query" target="newFrame">
    <table cellspacing="0" cellpadding="0" border="0" width="90%" height="80%" style="padding-left: 1em;">
    <tr>
        <td align="left" valign="top" width="70" style="padding-left: 5px; padding-right: 10px;"><%@include file="jsp-tmpl/logo-search-page.txt" %></td>
        <td align="left" valign="middle" >
            <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom">
                <div style="margin-bottom: 1em; background-color: #FFDFDF; border: 1px solid #FF7B7B; padding: 2px;" >
                   <bean:message key="pages.largeinput.warning"/>
                </div>
                </td>
            </tr>
            <tr>
                <td align="left" valign="bottom">
                <nobr>
                <html:link target="_parent" page="/components.jsp"><bean:message key="links.components" /></html:link>
                <html:link target="_parent" page="/admin/index.jsp"><bean:message key="links.admin"/></html:link>
                <html:link target="_parent" page="/search.jsp"><bean:message key="links.smallinputsearch"/></html:link>
                &nbsp;
                <html:link target="_parent" page="/demo.jsp"><bean:message key="links.demo"/></html:link>
                </nobr>
                </td>
            </tr>
            <tr>
                <td align="right" valign="bottom" style="background-color: #e0e0e0"><html:textarea property="query" rows="30" style="width: 100%;" /></td>
            </tr>
            <tr>
                <td align="right" valign="top" style="border: solid 1px gray; border-top: none; background-color: #e0e0e0">
                <nobr><bean:message key="forms.process-chain" />:
                <select style="font-size: 10px" name="processingChain">
                    <%
                    Map processGroups = ((ProcessingChainLoader)application.getAttribute(Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER))
                         .getProcessGroups();
                    for (Iterator i = processGroups.keySet().iterator();i.hasNext();)
                    {
                        Object key = i.next();
                        List l = (List) processGroups.get(key);
                        %><optgroup label="<%= key.toString() %>"><%
                        for (Iterator j = l.iterator(); j.hasNext(); )
                        {
                            ProcessDefinition p = (ProcessDefinition) j.next();
                            String   pname    = StrutsHelpers.getMessageOrDefault(pageContext, p.getId(), p.getDefaultDescription());

                            %><option value="<%= p.getId() %>"
                            <%= p.getId().equals(queryBean.getProcessingChain()) ? "SELECTED" : "" %>><%= pname %></option><%
                        }
                        %></optgroup><%
                    }
                    %>
                </select>
                &nbsp;<bean:message key="forms.requested-results" />:
                <select style="font-size: 10px" name="resultsRequested">
                	<%
                	int j = queryBean.getResultsRequested();
                	for (int i=50;i<=400;i+=50) {
                		boolean selected = (j>=i && j<i+50);
                		%><option value="<%= i %>" <%= selected ? "SELECTED" : "" %> ><%= i %></option><%
                	}
                	%>
                </select>
                </nobr>
                </td>
            </tr>
            <tr>
                <td align="right" valign="top"><html:submit style="height: 23px; font-size: 11px;"><bean:message key="forms.search" /></html:submit></td>
            </tr>
            </table>
        </td>
    </tr>
    </table>
</html:form>

<%@include file="jsp-tmpl/epilog.txt" %>