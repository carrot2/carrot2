<html xsl:version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="stylesheet.css" />
</head>
<body>
<p>
Carrot<sup>2</sup> is an Open Source Search Results Clustering Engine, which can
automatically organize small collections of documents, for example search results,
into thematic categories, <a href="#overview_description">see below for more</a>.
</p>

<h1>Downloads &amp; more information</h1>
<p>
<a href="http://project.carrot2.org/download-java-api.html" class="download-small">Java API JAR, JavaDocs and example code</a><br />
<a href="http://project.carrot2.org/download.html" class="download-small">Other Carrot2 applications</a><br />
<a href="http://download.carrot2.org/head/manual/index.html" class="view-small">User and Developer Manual</a><br />
<a href="http://download.carrot2.org/head/manual/index.html#section.integration.adding-to-maven-project" class="view-small">Instructions for Maven2 users</a><br />
<a href="http://project.carrot2.org" class="view-small">Carrot2 project website</a><br />
<a href="http://search.carrot2.org" class="view-small">Carrot2 on-line demo</a>
</p>


<h1>Java API usage examples</h1>
<p>
  You can use Carrot<sup>2</sup> Java API to fetch documents from various sources (public search engines, Lucene, Solr), perform clustering, serialize the results to JSON or XML and many more. Below is some example code for the most common use cases. Please see the <tt>examples/</tt> directory in the <a href="http://project.carrot2.org/download-java-api.html">Java API distribution archive</a> for more examples.
</p>

<a name="clustering-documents"></a>
<h2>Clustering text documents</h2>
<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-document-list-intro" parse="xml" />


<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-document-list" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/ClusteringDocumentList.java?r=trunk">Full source code: ClusteringDocumentList.java</a>


<a name="clustering-from-document-sources"></a>
<h2>Clustering documents from document sources</h2>

<a name="clustering-from-document-sources-with-default-settings"></a>
<h3>With default settings</h3>
<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-data-from-document-sources-simple-intro" parse="xml" />

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-data-from-document-sources-simple" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/ClusteringDataFromDocumentSources.java?r=trunk">Full source code: ClusteringDataFromDocumentSources.java</a>


<a name="clustering-from-document-sources-with-custom-settings"></a>
<h3>With custom settings</h3>
<p>
<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-data-from-document-sources-advanced-intro" parse="text" />
</p>

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-data-from-document-sources-advanced" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/ClusteringDataFromDocumentSources.java?r=trunk">Full source code: ClusteringDataFromDocumentSources.java</a>


<a name="setting-attributes"></a>
<h2>Setting attributes of clustering algorithms and document sources</h2>


<a name="setting-attributes-by-attribute-key"></a>
<h3>By attribute keys</h3>
<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-attributes-raw-map-intro" parse="xml" />

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-attributes-raw-map" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/UsingAttributes.java?r=trunk">Full source code: UsingAttributes.java</a>


<a name="setting-attributes-using-attribute-builders"></a>
<h3>Using attribute builders</h3>
<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-attributes-builders-intro" parse="xml" />

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-attributes-builders" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/UsingAttributes.java?r=trunk">Full source code: UsingAttributes.java</a>


<a name="collecting-output-attributes"></a>
<h3>Collecting output attributes</h3>
<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-attributes-output-intro" parse="xml" />

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-attributes-output" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/UsingAttributes.java?r=trunk">Full source code: UsingAttributes.java</a>



<a name="caching-controller"></a>
<h2>Pooling of processing component instances, caching of processing results</h2>

<p>
  The examples shown above used a simple controller 
  to manage the clustering process. While the simple controller
  is enough for one-shot requests, for long-running applications, such as web 
  applications, it's better to use a controller which supports pooling of 
  processing component instances and caching of processing results. 
</p>

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/using-caching-controller" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/UsingCachingController.java?r=trunk">Full source code: UsingCachingController.java</a>


<a name="non-english"></a>
<h2>Clustering non-English content</h2>

<xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-non-english-content-intro" parse="xml" />

<pre class="brush: java"><xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="snippets/clustering-non-english-content" parse="text" /></pre>
<a class="source-link" href="http://fisheye3.atlassian.com/browse/carrot2/trunk/applications/carrot2-examples/src/org/carrot2/examples/clustering/ClusteringNonEnglishContent.java?r=trunk">Full source code: ClusteringNonEnglishContent.java</a>


<link type="text/css" rel="stylesheet" href="{{@docRoot}}/sh/shCore.css"/>
<link type="text/css" rel="stylesheet" href="{{@docRoot}}/sh/shThemeDefault.css"/>
<script type="text/javascript" src="{{@docRoot}}/sh/shCore.js"></script>
<script type="text/javascript" src="{{@docRoot}}/sh/shBrushJava.js"></script>
<script type="text/javascript">
  SyntaxHighlighter.defaults.light = false;
  SyntaxHighlighter.defaults.gutter = false;
  SyntaxHighlighter.all();
</script>

</body>
</html>
