<%@ page session="false" contentType="text/html; charset=utf-8" %>
<html>
        <jsp:include page="top.jsp" flush="true" />

	<h1>SnippetReader</h1>
	<h2>An XmlRpc Service for extracting snippets from Web Search Engines</h2>

	<p>
	<b>Please note that most search service providers forbid automated querying
	   of their services. Always read terms-of-use carefully.
        </b>
        </p>
        <p>
           This program is part of a larger research project, Carrot2.
	</p>

        <hr>

	<h3>How to use SnippetReader</h3>
        <ol>
            <li>SnippetReader extracts snippets from various web sources using regular expression templates.
                <p>
                You may use it using XMLRPC API (this is the default method, which these
                pages rely on). A short description of methods available via XMLRPC is given below.
                <p>
                Alternatively, a Carrot2 Input-type component is exposed. POSTing a request in Carrot2
                XML format will result in plain Carrot2 XML data returned. The URL where request should
                be posted depends on the service you want to connect to.
                <p>
                <a href="query.jsp">An administrator interface</a> allows to edit and configure services.

            <li>In order for SnippetReader to work, it needs <b>a configuration file</b>. This file is a XML
                description of the service. You may edit this configuration file online (there is a link
                from the query page, which allows you to do that).
        </ol>

	<h3>Registered services</h3>
        <p>
        A list of registered services may be acquired using XmlRpc service by calling <code>_meta.getServicesList()</code>
        method. An online verbose list of services is also available under this link:
        <b><a href="listServices.jsp"><%= response.encodeRedirectURL("listServices.jsp") %></a></b>.
    </body>
</html>
