<%@ page contentType="text/html; charset=UTF-8"
         import="java.net.URL,com.dawidweiss.carrot.util.net.http.*,org.apache.xmlrpc.*,org.put.util.text.HtmlHelper,gnu.regexp.*,java.util.*"
         session="false"
%><%
    request.setCharacterEncoding("UTF-8");
%>

<%-- THIS JSP CODE IS MORE OF A QUICK-AND-DIRTY IMPLEMENTATION OF THE NECESSARY
     FUNCTIONALITY THAN A REAL THING IT SHOULD BE. I WISH I'LL HAVE THE TIME TO
     REIMPLEMENT IT IN THE FUTURE, IF NOT, SORRY ABOUT IT :) --%>

<html>
        <%@include file="top.jsp" %>

	<h1>SnippetReader</h1>
        <h2>Online query page</h2>
	<P>Please note that this page is to be a demonstration of the service -
	   XmlRpc protocol should be used for application programming.

<!-- query part -->
<hr>
<form action="query.jsp">
<pre>
Choose the service to query : <select name="service">
<%
            XmlRpcClientLite client = new XmlRpcClientLite( org.put.snippetreader.XmlRpcServlet.getSnippetReaderServiceURL(request));
            Vector services = (Vector) client.execute("_meta.getServicesList", new Vector());

            for (int i=0;i<services.size();i++)
            {
                String option = (String) services.elementAt(i);

                if (option.length() < 1 || option.charAt(0)=='_')
                    continue;

                String selected = (option.equals(request.getParameter("service")) ? "SELECTED":"");

                out.print("<option value=\"" + option + "\" " + selected + ">");
                out.print(option);
                out.println("</option>");
            }
%>
            </select> <input type="submit" name="submit" value="Edit configuration">
Enter your query            : <input type="text" name="query" size="20" value="<%= request.getParameter("query")%>">
How many results are needed?: <select name="needed">
<%
            String [] values = { "50", "100", "150", "300" };
            for (int i=0;i<values.length;i++)
            {
                String selected = (values[i].equals(request.getParameter("needed")) ? "SELECTED":"");

                out.print("<option value=\"" + values[i] + "\" " + selected + ">");
                out.print(values[i]);
                out.println("</option>");
            }
%>
            </select>
</pre>
<input type="submit" name="submit" value="Get snippets">
<input type="submit" name="submit" value="Get match-highlighted source">
<input type="submit" name="submit" value="Get exact source">
<input type="submit" name="submit" value="Get html-highlighted source">
<input type="submit" name="submit" value="Get Carrot2XML">
</form>
    <hr>

    <%
        if ("Edit configuration".equals( request.getParameter("submit") ) && request.getParameter("service") != null)
        {
            %><jsp:forward page="editService.jsp"/><%
        }
    %>

<!-- response part -->

	<P>
    <%
    String service = request.getParameter("service");
    String query   = request.getParameter("query");
    String needed  = request.getParameter("needed");

    if ( service != null && query != null && needed != null ) {
        Vector v = new Vector();
        v.add( query );
        v.add( new Integer( needed ) );

        if ("Get snippets".equals(request.getParameter("submit")))
        {
            String method  = "getSnippets";

            Vector result = (Vector) client.execute( service + "." + method, v);

            out.println("<ol>");
             for (int i=0; i<result.size();) {
                out.print("<li>");
                    out.println("<b>" + result.elementAt(i++) + "</b><br>");
                    out.println("<font color=gray><i>" + result.elementAt(i++) + "</i></font><br>");
                    out.println("<font size=-1>" + result.elementAt(i++) + "</font>" );
                out.print( "<p></li>" );
             }
            out.println("</ol>");
        }
        else
        if ("Get match-highlighted source".equals(request.getParameter("submit")))
        {
            String method  = "getHighlightedMatches";

            String result = (String) client.execute( service + "." + method, v);

            out.println( result);
        }
        else
        if ("Get exact source".equals(request.getParameter("submit")))
        {
            String method  = "getQuerySource";
            String result = (String) client.execute( service + "." + method, v);

            out.print("<pre>");
            String source = result;

            int MAX_PER_LINE = 90;
            int last = 0;
            for (int i=0;i<source.length();i++)
            {
                if (source.charAt(i) == '\n')
                {
                    out.print( HtmlHelper.escapeHtmlTags( source.substring(last,i+1)));
                    last = i+1;
                }
                if (i - last > MAX_PER_LINE)
                {
                    out.print( HtmlHelper.escapeHtmlTags( source.substring(last,i+1)));
                    out.println("<SPAN style=\"background-color: yellow;\">&nbsp;</span>");
                    last = i+1;
                }
            }
            if (last < source.length())
            {
                    out.print( HtmlHelper.escapeHtmlTags( source.substring(last,source.length())));
            }
            out.print("</pre>");
        }
        else
        if ("Get html-highlighted source".equals( request.getParameter("submit")))
        {
                String method  = "getQuerySource";
                String result = (String) client.execute( service + "." + method, v);

                out.print("<pre>");
                String source = result;
                StringBuffer buf = new StringBuffer();

                int MAX_PER_LINE = 90;
                int last = 0;
                for (int i=0;i<source.length();i++)
                {
                    if (source.charAt(i) == '\n')
                    {
                        buf.append( HtmlHelper.escapeHtmlTags( source.substring(last,i+1)));
                        last = i+1;
                    }
                    if (i - last > MAX_PER_LINE)
                    {
                        buf.append( HtmlHelper.escapeHtmlTags( source.substring(last,i+1)));
                        buf.append("<SPAN style=\"background-color: yellow;\">&nbsp;</span>\n");
                        last = i+1;
                    }
                }
                if (last < source.length())
                {
                        buf.append( HtmlHelper.escapeHtmlTags( source.substring(last,source.length())));
                }

                // marg tags.
                String [] highlights = {
                    "&lt;.*?&gt;", "<font color=gray>","</font>",
                    "&lt;!--.*?--&gt;", "<i><font color=green>","</font></i>",
                    "&lt;a.*?&gt;", "<font color=blue>","</blue>",
                    "&lt;/a&gt;", "<font color=blue>","</blue>"
                };

                int i=0;
                while (i < highlights.length) {
                    RE match = new RE( highlights[i], RE.REG_DOT_NEWLINE|RE.REG_ICASE );
                    i++;

                    REMatchEnumeration e = match.getMatchEnumeration(buf);

                    int pos = 0;
                    StringBuffer newb = new StringBuffer( buf.length() );
                    while (e.hasMoreMatches())
                    {
                        REMatch m = e.nextMatch();
                        newb.append( buf.substring(pos, m.getStartIndex()));
                        newb.append( highlights[i] );
                        newb.append( m.toString() );
                        newb.append( highlights[i+1] );
                        pos = m.getEndIndex();
                    }
                    newb.append( buf.substring( pos ) );
                    buf.setLength(0);
                    buf = newb;

                    i+=2;
                }

                // dump it.
                out.print(buf.toString());
                out.print("</pre>");
        }
        else
        if ("Get Carrot2XML".equals( request.getParameter("submit")))
        {
            String url = org.put.snippetreader.XmlRpcServlet.getSnippetReaderServiceURL(request) + "/carrot2/" + service;

            FormActionInfo    actionInfo = new FormActionInfo(new URL(url), "post");
            FormParameters    queryParameters = new FormParameters();
            HTTPFormSubmitter submitter = new HTTPFormSubmitter( actionInfo );

            java.io.StringWriter sw = new java.io.StringWriter();
            com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query xmlQuery = new com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query();
            xmlQuery.setContent(query);
            xmlQuery.setRequestedResults(Integer.parseInt(needed));
            xmlQuery.marshal(sw);

            Parameter parameter = new Parameter(
                "carrot-request", sw.getBuffer().toString(), false );

            queryParameters.addParameter( parameter );

            java.io.InputStream z = submitter.submit( queryParameters, null, "UTF-8" );

            out.print("<tt>");
            out.print( HtmlHelper.escapeHtmlTags((String)parameter.getValue(null)) );
            out.print("</tt>");

            out.print("<hr>");

            out.print("<textarea style=\"width=95%; height=45em;\">");
            out.print( HtmlHelper.escapeHtmlTags( new String(org.put.util.io.FileHelper.readFullyAndCloseInput(z), "UTF-8") ));
            out.print("</textarea>");
        }
    }

    %>

	</body>
</html>