<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="java.util.*,com.dawidweiss.carrot.controller.carrot2.struts.*,java.util.Iterator,com.dawidweiss.carrot.controller.carrot2.process.*,com.dawidweiss.carrot.controller.carrot2.components.*,com.dawidweiss.carrot.controller.carrot2.Carrot2InitServlet"
%>

<!-- ### Include page top ### -->

<%@include file="jsp-tmpl/prolog.txt" %>

<!-- ### Initialize query bean (request scope) ### -->
<jsp:useBean id="queryBean" scope="request" class="com.dawidweiss.carrot.controller.carrot2.struts.forms.QueryForm" />
<jsp:setProperty name="queryBean" property="*" />
<%
    if (!queryBean.isInitialized())
        queryBean.initialize(application);
%>

<!-- ### Top query banner (horizontal) -->

<html:form action="/newsearch.do" focus="query" method="GET" target="_top" >
    <table cellspacing="0" cellpadding="0" border="0" width="90%">
    <tr>
        <td align="center" valign="middle" width="70" style="padding-left: 5px; padding-right: 10px;"><%@include file="jsp-tmpl/logo-search-page.txt" %></td>
        <td align="left" valign="middle">
            <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom">
                <nobr>
                <html:link target="_parent" page="/components.jsp"><bean:message key="links.components" /></html:link>
                &nbsp;
                <html:link target="_parent" page="/admin/index.jsp"><bean:message key="links.admin"/></html:link>
                &nbsp;
                <html:link target="_parent" page="/largeInputSearch.jsp"><bean:message key="links.largeinputsearch"/></html:link>
                &nbsp;
                &nbsp;
                &nbsp;
                &nbsp;
                <html:link target="_parent" page="/demo.jsp"><bean:message key="links.demo"/></html:link>
                &nbsp;
                &nbsp;
                <html:link target="_top" forward="whatis"><bean:message key="links.whatis"/></html:link>
                </nobr>
                </td>
            </tr>
            <tr>
                <td align="right" valign="bottom" style="background-color: #e0e0e0"><html:text property="query" size="45" style="width: 100%; height: 23px;" /></td>
                <td align="left" valign="top" width="30"><input
                       onclick="javascript:document.forms[0].submit();"
                       style="height: 23px; font-size: 10px;"
                       name="startButton"
                       value="<%= StrutsHelpers.getMessageOrDefault(pageContext, "forms.search", "Search") %>"
                       type="button"></td>
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
            </table>
        </td>
        <td align="left" valign="middle" width="32">
        	<img id="hourglass" src="gfx/empty.gif" width="32" height="32" />
        </td>
    </tr>
    </table>
</html:form>

<%@include file="jsp-tmpl/epilog.txt" %>