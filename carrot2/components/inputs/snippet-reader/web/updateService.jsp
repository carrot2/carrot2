<%@ page import="org.apache.xmlrpc.*,java.util.*,org.put.snippetreader.*,org.put.util.text.HtmlHelper,gnu.regexp.*,java.util.*"
         session="false"
         contentType="text/html; charset=utf-8"
%><%
    // Attempt to update service's configuration.

    XmlRpcClientLite client = new XmlRpcClientLite( org.put.snippetreader.XmlRpcServlet.getSnippetReaderServiceURL(request));
    Vector v = new Vector();
    String service = request.getParameter("service");
    String configuration =  request.getParameter("config");

    %>
    <jsp:include page="top.jsp" flush="true" />
    <%

    try
    {
        if (service==null) throw new Exception("Service cannot be null.");
        if (configuration==null) throw new Exception("Config cannot be null.");

        v.add( service );
        v.add( configuration );
        client.execute("_meta.updateServiceConfig", v);
    }
    catch (Exception e)
    {
        %>
        <h1>Service configuration update failed.</h1>
        <tt><%= e.toString() %><BR><BR></tt>
        <hr>
        <h2>File retrieved.</h2>
        <pre><%= HtmlHelper.escapeHtmlTags(configuration==null?"":configuration)%></pre>
        </body>
        </html>
        <%
        return;
    }
%>
    <h2>Service <%= service %> has been updated.</h2>
    <p>You may now verify whether it really works on the <a href="query.jsp?service=<%= service %>">query page.</a>
</body>
</html>