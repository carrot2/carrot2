<%@page contentType="text/html; charset=UTF-8" %>
<HTML>
<BODY>
<h1>LSI clusterer filters</h1>
<p>
This service provides two LSI-based clustering algorithms:
<dl>
    <dt>Default LSI Cluster</dt>
    <dd>
    	<p>
          A monolingual version of LSI clusterer. Uses linguistic information
          contained in the input stream.
   		</p>
   		<p>
   		The following algorithm parameters are available:
   		<dl>
   			<dt>
   				<pre>lsi.threshold.clusterAssignment</pre>
   			</dt>
   		 	<dd>
   		 		Determines the similarity threshold that must be exceeded
   		 		in order for a document to be added to a cluster. The larger 
   		 		the value, the less documents in a cluster and the 
   		 		larger assignment precission. Range: 0.0 - 1.0
			</dd>
			
   			<dt>
   				<pre>lsi.threshold.candidateCluster</pre>
   			</dt>
   		 	<dd>
   		 		Determines the maximum number of candidate clusters. 
   		 		The larger the value, the more candidate clusters.
   		 		Range: 0.0 - 1.0
			</dd>
		</dl>
		</p>
    </dd>
    <dt>Multilingual LSI Cluster</dt>
    <dd>
    	<p>
   		A multilingual version of LSI clusterer. For the time being, linguistic
   		processing is done internally, but the functionality will be moved to 
   		stemmer filter.
   		</p>
   		<p>
   		The following algorithm parameters are available:
   		<dl>
   			<dt>
   				<pre>lsi.threshold.clusterAssignment</pre>
   			</dt>
   		 	<dd>
   		 		Determines the similarity threshold that must be exceeded
   		 		in order for a document to be added to a cluster. The larger 
   		 		the value, the less documents in a cluster and the 
   		 		larger assignment precission. Range: 0.0 - 1.0
			</dd>
			
   			<dt>
   				<pre>lsi.threshold.candidateCluster</pre>
   			</dt>
   		 	<dd>
   		 		Determines the maximum number of candidate clusters. 
   		 		The larger the value, the more candidate clusters.
   		 		Range: 0.0 - 1.0
			</dd>
			
   			<dt>
   				<pre>stemmer.[language-name]</pre>
   			</dt>
   		 	<dd>
   		 		Specifies the stemmer class. Language names must correspond
   		 		to stopword file names pesent in webapp-dir/stopwords
   		 		(an ugly solution - I admit it).
			</dd>
		</dl>
		</p>
    </dd>
</dl>
</p>

        
</ul>
</BODY>
</HTML>