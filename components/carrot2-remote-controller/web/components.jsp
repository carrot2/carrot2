<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="
        com.dawidweiss.carrot.remote.controller.process.*,
        com.dawidweiss.carrot.controller.carrot2.xmlbinding.*,
        java.util.Iterator,
        com.dawidweiss.carrot.remote.controller.Carrot2InitServlet,
        com.dawidweiss.carrot.*,
        com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers"
%>

<%@include file="jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="blocks.components.processing-chains" /></h1>

<ul>
    <logic:iterate id="p" type="ProcessDefinition"
        name="<%= Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER %>"
        scope="application"
        property="processDefinitions">
        <%  String pname = StrutsHelpers.getMessageOrDefault(pageContext, p.getId(), p.getDefaultDescription()); %>
        <li><b><%= pname %></b><br/>Id:&nbsp;<tt><%= p.getId() %></tt>
            <table width="100%" border="0" cellspacing="2" cellpadding="0" style="margin-left: 10%; margin-top: 10px; margin-bottom: 10px; padding-left: 10px; border-left: solid 10px gray;">
                <% if (p instanceof ResolvedScriptedProcess) { %>
                    <tr>
                        <td colspan="2">
                        	<i>This is a scripted process.</i>
                        	<br/>
							<html:link page="/showProcess.jsp"
									   paramId="process" paramName="p" paramProperty="id">
							        <bean:message key="links.showProcessScript" /></html:link>
                        </td>
                    </tr>
                <% }; %>
            </table>
        </li>
    </logic:iterate>
</ul>

<h1><bean:message key="blocks.components.components" /></h1>

<h2><bean:message key="blocks.components.input-components" /></h2>

<ul>
<logic:iterate id="filter" type="ComponentDescriptor"
    name="<%= Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER %>"
    scope="application"
    property="componentLoader.inputComponents">

    <li><b><%= StrutsHelpers.getMessage(pageContext, filter.getId()) %></b>

        <!-- Edit button -->
        <!--
        <html:link page="/admin/editComponent.do" paramId="componentId"
                   paramName="filter" paramProperty="id">
        [edit]</html:link>
        -->

        <logic:present name="filter" property="configurationURL">
            <br/>
            <html:link href="<%= filter.getConfigurationURL() %>">
            <bean:message key="blocks.components.configuration" />
            </html:link>
        </logic:present>
        <logic:present name="filter" property="infoURL">
            <br/>
            <html:link href="<%= filter.getInfoURL() %>">
            <bean:message key="blocks.components.information" />
            </html:link>
        </logic:present>
    </li>
</logic:iterate>
</ul>

<h2><bean:message key="blocks.components.output-components" /></h2>

<ul>
<logic:iterate id="filter" type="ComponentDescriptor"
    name="<%= Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER %>"
    scope="application"
    property="componentLoader.outputComponents">

    <li><b><%= StrutsHelpers.getMessage(pageContext,
                filter.getId()) %></b>
        <logic:present name="filter" property="configurationURL">
            <br/>
            <html:link href="<%= filter.getConfigurationURL() %>">
            <bean:message key="blocks.components.configuration" />
            </html:link>
        </logic:present>
        <logic:present name="filter" property="infoURL">
            <br/>
            <html:link href="<%= filter.getInfoURL() %>">
            <bean:message key="blocks.components.information" />
            </html:link>
        </logic:present>
    </li>
</logic:iterate>
</ul>

<h2><bean:message key="blocks.components.filter-components" /></h2>

<ul>
<logic:iterate id="filter" type="ComponentDescriptor"
    name="<%= Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER %>"
    scope="application"
    property="componentLoader.filterComponents">

    <li><b><%= StrutsHelpers.getMessage(pageContext,
                filter.getId()) %></b>
        <logic:present name="filter" property="configurationURL">
            <br/>
            <html:link href="<%= filter.getConfigurationURL() %>">
            <bean:message key="blocks.components.configuration" />
            </html:link>
        </logic:present>
        <logic:present name="filter" property="infoURL">
            <br/>
            <html:link href="<%= filter.getInfoURL() %>">
            <bean:message key="blocks.components.information" />
            </html:link>
        </logic:present>
    </li>
</logic:iterate>
</ul>


<%@include file="jsp-tmpl/epilog-infopage.txt" %>