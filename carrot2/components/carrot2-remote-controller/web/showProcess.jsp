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

<%@include file="jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="blocks.components.processing-chains" /></h1>

<%
ProcessingChainLoader loader = ((ProcessingChainLoader) application.getAttribute(Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER));
ProcessDefinition p = loader.findProcessDefinition(request.getParameter("process"));
request.setAttribute("process", p);
%>

<logic:present name="process" scope="request">
	<p>
		The script below controls how process <b><%= StrutsHelpers.getMessageOrDefault(pageContext, p.getId(), p.getDefaultDescription()) %></b>
		executes user queries. Refer to Carrot<sup>2</sup> documentation
		for details.
	</p>
	<p>
	<% if (p.isScripted()) { %>
		<% if (p instanceof ResolvedScriptedProcess) { %>
		Script's language: <b><%= ((ResolvedScriptedProcess) p).getScriptLanguage() %></b><br/>
		<textarea cols="75" rows="25" readonly="true" style="color: #606060" WRAP="OFF"><%
            XMLSerializerHelper serializer = XMLSerializerHelper.getInstance(); 
			out.print(
				serializer.toValidXmlText( ((ResolvedScriptedProcess) p).getScript(), false));
		%></textarea>
		<% } %>
	<% } else { %>
		Error: Unknown process class: <%= p.getClass().getName() %>
	<% }; %>
	</p>
</logic:present>

<%@include file="jsp-tmpl/epilog-infopage.txt" %>