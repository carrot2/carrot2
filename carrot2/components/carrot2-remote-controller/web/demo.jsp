<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="
            com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*,
	        com.dawidweiss.carrot.remote.controller.struts.*,
        	com.dawidweiss.carrot.remote.controller.cache.*,
	        com.dawidweiss.carrot.remote.controller.*,
	        com.dawidweiss.carrot.remote.controller.process.*,
	        com.dawidweiss.carrot.util.common.*,
	        com.dawidweiss.carrot.util.net.*,
        	java.util.*"
%>

<%@include file="jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="links.demo" /></h1>
<p>
<bean:message key="blocks.demo.intro" />
</p>

<hr />

<%
    ProcessingChainLoader loader =
        (ProcessingChainLoader) application.getAttribute(Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER);

    String PRECACHED_QUERIES_KEY = "DEMO-PRECACHED-QUERIES";
    if (application.getAttribute(PRECACHED_QUERIES_KEY)==null)
    {
        ZIPCachedQueriesContainer cached = new ZIPCachedQueriesContainer();
        cached.setReadOnly(true);
        cached.setContextRelativeDir( "WEB-INF/precached-queries" );
        cached.setServletBase( getServletContext().getRealPath("/") );
        cached.configure();

        HashMap map = new HashMap();
        for (Iterator i = cached.getCachedElementSignatures();i.hasNext();)
        {
            CachedQuery q  = (CachedQuery) cached.getCachedElement(i.next());
            String inputId = q.getComponentId();

            List l;
            if ( (l = (List) map.get( inputId )) == null)
            {
                l = new LinkedList();
                map.put(inputId, l);
            }
            l.add(q);
        }

        List components = new ArrayList(map.keySet());
        Collections.sort(components);
        List precached = new ArrayList( components.size());
        for (Iterator k = components.iterator(); k.hasNext(); )
        {
            String key = (String) k.next();
            List   queries = (List) map.get(key);
            Collections.sort(queries, new Comparator() {
                public int compare(Object a, Object b)
                {
                    return ((CachedQuery) a).getQuery().getContent().compareTo(
                                ((CachedQuery) b).getQuery().getContent());
                }
            });
            precached.add(queries);
        }
        application.setAttribute(PRECACHED_QUERIES_KEY, precached);
    }

    List precachedQueries = (List) application.getAttribute(PRECACHED_QUERIES_KEY);
    int formCounter = 0;
    for (Iterator i = precachedQueries.iterator(); i.hasNext();)
    {
        List queries = (List) i.next();

        String componentId   = ((CachedQuery)queries.get(0)).getComponentId();
        String componentName = StrutsHelpers.getMessageOrDefault(pageContext, componentId, componentId);

        %>
        <div style="margin: 10 0 0 0; background-color: #e0e0e0;"><bean:message key="blocks.demo.cachedfor" /></div>
        <div style="margin: 0 0 0 0; font-size: 1.5em; font-weight: bold; background-color: #f5f5f5;"><%= componentName %></div>
        <%

        out.write("<ul>");
		XMLSerializerHelper serializer = XMLSerializerHelper.getInstance(); 

        for (Iterator j = queries.iterator(); j.hasNext();)
        {

            CachedQuery q = (CachedQuery) j.next();
            out.write(
                  "<li><b>"
                + serializer.toValidXmlText((q.getQuery().getContent()), false) + "</b>");
            out.write(
                    "&nbsp;&nbsp;<i>"
                + (q.getQuery().hasRequestedResults() ? "(" + q.getQuery().getRequestedResults()
                + " " +  StrutsHelpers.getMessageOrDefault(pageContext, "blocks.demo.snippets", "snippets") + ")"
                : "(?)" ) + "</i>");

            %>
            <div style="font-size: 9px; margin: 5 0 0 0; padding: 0px;">
            <%

            ComponentDescriptor component = loader.getComponentLoader().findComponent(componentId);
            for (Iterator k = loader.getProcessDefinitions().iterator(); k.hasNext();)
            {
                ProcessDefinition p = (ProcessDefinition) k.next();

                if (!p.isHidden() && p.usesComponent(component))
                {
                    out.write("[");
                    %>
                        <a href="<%= response.encodeURL(
                            request.getContextPath() + "/index.jsp?"
                            + "query=" + URLEncoding.encode(q.getQuery().getContent(), "UTF-8")
                            + "&processingChain=" + URLEncoding.encode(p.getId(), "UTF-8")
                            + "&resultsRequested=" + (q.getQuery().hasRequestedResults() ? q.getQuery().getRequestedResults() : 0)) %>" target="_top">
                        <%= StrutsHelpers.getMessageOrDefault(pageContext, p.getId(), p.getDefaultDescription()) %>
                        </a>
                    <%
                    if (p.isScripted())
                    {
                        %><a class="nodecoration" href="#scripted"><span style="color: red">*</span></a><%
                    }
                    out.write("] ");
                    if (k.hasNext()) out.write("<br>");
                }
            }
            %></form></div><%

            out.write("</li>");
        }
        out.write("</ul>");
    }
%>

<a name="scripted"><hr width="100" size="1"></a>
<span style="color: red">*</span> &ndash; <bean:message key="blocks.demo.scripted" />


<%@include file="jsp-tmpl/epilog-infopage.txt" %>

