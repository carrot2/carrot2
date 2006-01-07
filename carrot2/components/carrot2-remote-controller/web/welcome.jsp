<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="com.dawidweiss.carrot.remote.controller.*,
                com.dawidweiss.carrot.controller.carrot2.xmlbinding.*,
                com.dawidweiss.carrot.remote.controller.process.*,
                com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers,
                com.dawidweiss.carrot.util.net.*"
%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@include file="jsp-tmpl/prolog.txt" %>

<table width="100%" border="0" style="margin-top: 3em;">
	<tr>
		<td align="center" valign="top" width="50%">
			<div style="text-align: left; margin-left: 10%; margin-right: 10%;">
			<b>
			<bean:message key="blocks.welcome.head"/>
			</b><br/><br/>
			<bean:message key="blocks.welcome.info"/>
			</div>
		</td>
		<td align="center" valign="top" width="50%">
			<div style="text-align: left; margin-left: 10%; margin-right: 10%;">
			<b>
			<bean:message key="blocks.recent-queries.head"/>
			</b><br/><br/>
			
			<!-- Recent queries block -->
			<%
				int num = 12;
			%>
			<%@include file="jsp-tmpl/recent-queries.txt" %>
			
			</div>
		</td>
	</tr>
</table>
</center>


<%@include file="jsp-tmpl/epilog.txt" %>
