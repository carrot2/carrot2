<%@page contentType="text/html; charset=UTF-8" %>

<%@include file="/jsp-tmpl/prolog.txt" %>

    <table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%">
    <tr><td align="center" valign="middle">

        <%@include file="/jsp-tmpl/logo-infopage.txt" %>

        <div style="text-align: justify; margin-top: 10px; margin-bottom: 10px; width: 400px; border-top: solid 1px gray; border-bottom: solid 1px gray; padding: 10px;">
            <p style="margin-top: 20px;">
            Admin options:
            </p>
            <p>
            <html:link target="_top" page="/admin/recent.jsp">A list of recent queries</html:link>
            </p>
            <p>
            <html:link target="_top" page="/admin/cacheclear.do">Clear cached queries</html:link>
            </p>
        </div>
    </td></tr>
    </table>

<%@include file="/jsp-tmpl/epilog.txt" %>
