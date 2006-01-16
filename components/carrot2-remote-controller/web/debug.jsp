<%@page contentType="text/plain; charset=UTF-8"
        import="com.dawidweiss.carrot.remote.controller.*"
%><%
	response.setContentType("text/plain");

	QueryProcessor processor = (QueryProcessor) application.getAttribute(Carrot2InitServlet.CARROT_PROCESSOR_KEY);
	if (processor == null)
	        throw new RuntimeException("Query Processor is null?");

	if (request.getParameter("usecacheonly") != null) {
	    boolean usecacheonly = Boolean.valueOf(request.getParameter("usecacheonly")).booleanValue();
	    processor.setUseCacheOnly(usecacheonly);
	    out.print("Use cache only: " + usecacheonly + "\n");
	}
	
	if (request.getParameter("history") != null) {
	    boolean history = Boolean.valueOf(request.getParameter("history")).booleanValue();
	    processor.setUseHistory(history);
	    out.print("Use history of queries: " + history + "\n");
	}

	if (request.getParameter("status") != null) {
		out.print("UP\n");
	}
%>
