<%@page contentType="text/html; charset=UTF-8" 
        errorPage="/error.jsp"
%>

<%@include file="jsp-tmpl/prolog-base.txt" %>

<jsp:useBean id="queryBean" scope="request" class="com.dawidweiss.carrot.controller.carrot2.struts.forms.QueryForm" />
<jsp:setProperty name="queryBean" property="*" />
<%
    if (!queryBean.isInitialized())
        queryBean.initialize(application);

	String qry = queryBean.getQuery();
	if (qry.length() > 1000) {
		// assume it was a verbatim query and forget it.
		queryBean.setQuery("");
	}

    // calculate query params.
    StringBuffer buf = new StringBuffer();
    buf.append("query="); buf.append( org.put.util.net.URLEncoding.encode(queryBean.getQuery(), "UTF-8"));
    buf.append('&');
    if (queryBean.getProcessingChain() != null)
    {
        buf.append("processingChain="); buf.append( org.put.util.net.URLEncoding.encode(queryBean.getProcessingChain(), "UTF-8"));
        buf.append('&');
    }
    buf.append("resultsRequested="); buf.append( queryBean.getResultsRequested());
%>

<script type="text/javascript" language="JavaScript">
<!--
var loaded = false;
function disableAnim()
{
	loaded = true;
	try
	{
		if (document.all)
			window.frames[0].document.all['hourglass'].src = "gfx/empty.gif";
		else
			window.frames[0].document.getElementById('hourglass').src = "gfx/empty.gif";

	}
	catch (e)
	{
	}
}
function enableAnim()
{
	if (loaded)
		return;
	try
	{
		if (document.all)
			window.frames[0].document.all['hourglass'].src = "gfx/wait.gif";
		else
			window.frames[0].document.getElementById('hourglass').src = "gfx/wait.gif";

	}
	catch (e)
	{
	}
}
// -->
</script>

<frameset rows="80,*">
    <frame name="controller" src="<%= response.encodeURL("search.jsp?" + buf.toString() ) %>" marginwidth="0" marginheight="0" scrolling="no" frameborder="0" onLoad="javascript:enableAnim()">
    <frame name="output"     src="<%= response.encodeURL("search.do?"  + buf.toString() ) %>" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" onLoad="javascript:disableAnim()">
</frameset>
<noframes>
This controller of Carrot<sup>2</sup> architecture requires frames. Sorry.
</noframes>

<%@include file="jsp-tmpl/epilog-base.txt" %>
