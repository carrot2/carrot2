<%@page import="org.apache.struts.action.Action,org.apache.log4j.Logger,com.dawidweiss.carrot.controller.carrot2.struts.StrutsHelpers" contentType="text/html; charset=UTF-8" isErrorPage="true" %>
<%
    if (request.getAttribute( Action.EXCEPTION_KEY ) != null)
    {
        org.apache.log4j.Logger.getLogger( this.getClass() ).error("STRUTS left an exception in request scope",
            (Throwable) request.getAttribute( Action.EXCEPTION_KEY ));
    }
%>

<%@include file="jsp-tmpl/prolog-infopage.txt" %>

<%
    if (exception == null)
    {
        exception = (Throwable) request.getAttribute("exception");
    }

    if (exception != null)
    {
        String exceptionInfo;

        org.apache.log4j.Logger.getLogger( this.getClass() ).error("JSP error occurred.", exception);

        exceptionInfo = StrutsHelpers.getMessage(pageContext, exception.getMessage());
        if (exceptionInfo == null)
            exceptionInfo = exception.getMessage();
        if (exceptionInfo == null)
            exceptionInfo = exception.toString();
        %>
		<!-- This comment required for test methods. do not remove: *C2TESTSUNHANDLEDERRORPAGE* -->
        <h1>Error: <span style="color: red;"><%= exceptionInfo %></span></h1>

        <p>We apologize for inconvenience. Error report has been logged, but please report any errors like this
           to keep us motivated ;)</p>

        <h2>Exception stack trace</h2>

        <pre><%= org.put.util.exception.ExceptionHelper.getStackTrace(exception) %></pre>
        <%
    };
%>

<%@include file="jsp-tmpl/epilog-infopage.txt" %>
