<%@page contentType="text/plain; charset=UTF-8"
        import="com.dawidweiss.carrot.controller.carrot2.*"
%>

<%

QueryProcessor processor = (QueryProcessor) application.getAttribute(Carrot2InitServlet.CARROT_PROCESSOR_KEY);
if (processor == null)
        throw new RuntimeException("Query Processor is null?");

if (request.getParameter("usecacheonly") != null) {
    boolean usecacheonly = Boolean.valueOf(request.getParameter("usecacheonly")).booleanValue();
    processor.setUseCacheOnly(usecacheonly);
    out.print("use cache only: " + usecacheonly + "\n");
}
%>
