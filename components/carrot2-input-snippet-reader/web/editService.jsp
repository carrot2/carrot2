<%@ page import="org.apache.xmlrpc.*,java.util.*,org.put.util.text.HtmlHelper,gnu.regexp.*"
         session="false"
         contentType="text/html; charset=utf-8"
%>

<html>
        <jsp:include page="top.jsp" flush="true" />

	<h1>SnippetReader. Service configuration.</h1>
	<h2>Editing configuration of: <%= request.getParameter("service") %></h2>

<!-- query part -->
    <hr>
<form action="updateService.jsp" method="post">
<input type="hidden" name="service" value="<%= request.getParameter("service") %>">
<input type="submit" name="submit" value="Update configuration"><br>
<textarea style="width=95%; height=45em;" name="config"><%
    // read service's xml.
    XmlRpcClientLite client = new XmlRpcClientLite( org.put.snippetreader.XmlRpcServlet.getSnippetReaderServiceURL(request));
    Vector v = new Vector();
    v.add( request.getParameter("service"));
    String conf = (String) client.execute("_meta.getServiceConfig", v);
    out.print( HtmlHelper.escapeHtmlTags(conf));
%></textarea>
</form>
	</body>
</html>