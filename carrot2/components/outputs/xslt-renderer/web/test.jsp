<%@ page session="false" contentType="text/html; charset=utf-8"
    import="java.util.*"
%>
<html>
        <head>
            <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        </head>
        <body>
            <h1>Carrot-output XSLT Renderer</h1>
            <h2>Manual POST test.</h2>

            <p>
                <b>Please put a valid Carrot2 data XML into the text field below and post it to
                   the service.</b>
            </p>

            <hr />

            <p>
                <form action="<%= response.encodeURL( request.getContextPath() + "/service") %>"
                      method="post"
                      enctype="application/x-www-form-urlencoded"
                >
                    Stylesheet:
                    <select style="height: 23px; font-size: 10px" name="stylesheet">
                        <%
                        HashMap styles = (HashMap) application.getAttribute(
                            com.dawidweiss.carrot.output.xsltrenderer.XsltRendererServlet.XSLT_RENDERER_STYLESHEETS);
                        if (styles != null)
                        {
                            for (Iterator i = styles.keySet().iterator();i.hasNext();)
                            {
                                String name = (String) i.next();
                                %>
                                <option value="<%= name %>"><%=name%></option>
                                <%
                            }
                        }
                        %>
                    </select>
                    <br/>
                    <textarea name="carrot-xchange-data" style="width: 100%; height: 250;">&lt;input your XML here&gt;</textarea><br/>
                    <input type="submit" name="submit" value="Submit" />
                </form>
            </p>
        </body>
</html>

