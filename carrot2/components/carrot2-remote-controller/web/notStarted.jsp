<%@page contentType="text/html; charset=UTF-8" 
	import="
		com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers,
		com.dawidweiss.carrot.remote.controller.util.*"
%>

<%
    pageContext.setAttribute("dontRedirect", "true");
 %>

<%@include file="jsp-tmpl/prolog-infopage.txt" %>

    <div class="frame">
    <h1>Application not started</h1>
    <pre><%
        Object reason = application.getAttribute("CARROT_INIT_ERROR");
        if (reason instanceof Throwable)
        {
            out.write( ExceptionHelper.getStackTrace( (Throwable) reason ));
        }
        else
        {
            out.write( reason.toString() );
        }
    %></pre>
    </div>

<%@include file="jsp-tmpl/epilog-infopage.txt" %>
