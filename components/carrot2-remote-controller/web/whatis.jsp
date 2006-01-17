<%@page contentType="text/html; charset=UTF-8"
        errorPage="/error.jsp"
        import="
        com.dawidweiss.carrot.remote.controller.struts.StrutsHelpers,
        com.dawidweiss.carrot.remote.controller.Carrot2InitServlet,
        com.dawidweiss.carrot.remote.controller.process.*"
%><%@include file="jsp-tmpl/prolog-infopage.txt" %>

<h1><bean:message key="blocks.whatis.whatis" /></h1>

<p>
Welcome!<br/>
Carrot<sup>2</sup> is a system for clustering textual data.
</p>
<p>
First, select the <em>clustering process</em>. A clustering
process consists of an input data source (usually a search engine), some
clustering algorithm (Lingo rocks!) and finally the output visualization component
(<em>dynamic tree</em> in most cases). Then, type a query, just as
you would type it into the search engine and wait for the result.
</p>
<p style="text-align: center;">
<img style="border: 1px solid black;" src="gfx/scr-process.gif" />
</p>
<p>
The result depends on the last component in the processing chain, for example
in case of the dynamic tree component, you may expect the raw <em>ranking
list</em> of results returned from the search engine and an additional
set of <em>clusters</em> - thematically organized groups of documents discovered in the
search result. 
</p>
<p style="text-align: center;">
<img style="border: 1px solid black;" src="gfx/scr-groups.gif" />
</p>
<p>
The process of discovering clusters depends on the clustering component 
and is usually fully automated, so clusters
may not be sensible. Please report successes as well as failures of the
algorithm to help improve it in the future!
</p>
<p>
Thank you for your interest in Carrot</sup>2</sup>!<br/>
</p>

<%@include file="jsp-tmpl/epilog.txt" %>
