<?php

/*
 * XML-RPC bridge from PHP to Carrot2.
 *
 * (c) Carrot Search s.c.
 * Version: $Id: carrot2.inc 467 2005-12-15 12:32:03Z dweiss $
 */


require_once("carrot2-config.inc");
require_once("xmlrpc.inc");


// Override PHP XML-RPC internal encoding to UTF-8.
$GLOBALS['xmlrpc_defencoding'] = 'UTF-8';
$GLOBALS['xmlrpc_internalencoding'] = 'UTF-8';


/**
 * A class representing a single cluster. The class has three properties:
 * <ul>
 *     <li><code>label</code> - holds a string with the label of this cluster,
 *     <li><code>documents</code> - holds identifiers of the documents previously
 *         added to the clusterer ({@link Carrot2} class). The identifiers are numeric
 *         indices of documents starting from 0.
 *     <li><code>subclusters</code> - an array of subclusters of this cluster.
 * </ul>
 */
class Cluster {
	function Cluster($label, $documentIds, $subclusters) {
		$this->label = $label;
		$this->documents = $documentIds;
		$this->subclusters = $subclusters;
	}
};


/**
 * Clusterer facade that connects to a remote Carrot2 service using
 * XML-RPC.
 *
 * @author Dawid Weiss
 * @copyright Carrot Search s.c.
 */ 
class Carrot2 {
    /**
     * Creates a new instance of the Carrot2 clustering class with an
     * external URL to the Carrot2 clustering service.
     *
     * @param urlPrefix   servlet path of the service to POST XML-RPC messages to. Usually the context
     *                    path of the Web application.
     * @param phpEncoding The encoding of strings passed to {@link addDocument}.
     * @param serviceName Carrot2 service name (carrot2 is the open source Lingo engine).
     * @param processingOptions A map of processing options (keys and values must be strings).
     * @param requestOptions A map of request-time options (passed to the controller).
     */
    function Carrot2($serviceHost, $servicePort, $urlPrefix = "/", $phpEncoding, 
       $serviceName = "cluster", $processingOptions = array(), $requestOptions = array())
    {
        $this->serviceHost = $serviceHost;
        $this->servicePort = $servicePort;
        $this->urlPrefix = $urlPrefix;

        $this->documents = array();
        $this->id = 0;

        $this->processingOptions = $this->_toXMLRPC($processingOptions);
        $this->requestOptions = $this->_toXMLRPC($requestOptions);

        $this->phpEncoding = $phpEncoding;
        $this->transferEncoding = "UTF-8";

        $this->serviceName = $serviceName;
    }

    /**
     * Adds a single document to the collection of documents to be clustered.
     */
    function addDocument($url = "", $title = "", $snippet = "") {
	    $this->documents[] = new xmlrpcval($this->id, "string");
	    $this->documents[] = new xmlrpcval(iconv($this->phpEncoding, $this->transferEncoding, $url), "string");
	    $this->documents[] = new xmlrpcval(iconv($this->phpEncoding, $this->transferEncoding, $title), "string");
	    $this->documents[] = new xmlrpcval(iconv($this->phpEncoding, $this->transferEncoding, $snippet), "string");

	    $this->id++;
    }

    /**
     * Clusters all documents previously added using {@link #addDocument()}. The
     * <code>queryHint</code> parameter can be used to give the clusterer a query which was
     * issued to collect the documents (it is useful to prevent the clusterer from recreating
     * a cluster with an identical name to the query).
     *
     * Returns an array of {@link Cluster} objects.
     */
    function clusterQuery($queryHint = "") {
	    $msg = $this->_getXmlRpcMsg($queryHint);

		$client = new xmlrpc_client($this->urlPrefix, $this->serviceHost, $this->servicePort);

		$client->setDebug(0);
  		
		$response = $client->send($msg);
  
		if (!$response) {
            trigger_error("No response from XML-RPC service.", E_USER_ERROR );
			return null;
		}

  		if ($response->faultCode()) {
            trigger_error("Clustering failed: " . $response->faultCode() . " (" . $response->faultString() . ")", E_USER_ERROR );
	  		return null;
  		} else {
	  		// there is a response, convert it to a PHP structure and return it.
			$v = $response->value();
			
			$clusters = array();
			$this->_convertCluster($v, $clusters);

			return $clusters;
  		}
    }
    
    /**
     * Converts an XML-RPC representation of a cluster into PHP-friendly data structure.
     */
    function _convertCluster(&$v, &$clusters) {
		$len = $v->arraysize();
		for ($i = 0; $i < $len; $i++) {
			$tmpcls = $v->arraymem($i);

			$label = $tmpcls->structmem("label");
			$label = iconv($this->transferEncoding, $this->phpEncoding, $label->scalarval());

			// Convert documents into references.
			$documents = $tmpcls->structmem("documents");
            if (isset($documents)) {
                $docs = array();
                for ($j = 0; $j < $documents->arraysize(); $j++) {
                    $id = $documents->arraymem($j);
                    $id = $id->scalarval();
                    $docs[] = $id;
                }
            }

			// Convert subclusters
			$subclusters = array();
			$this->_convertCluster($tmpcls->structmem("subclusters"), $subclusters);

			$clusters[] = new Cluster($label, $docs, $subclusters);
		}
    }

    /**
     * Returns an <code>xmlrpcmsg</code> object, ready
     * to be sent to a remote XML-RPC service. To be used internally
     * only.
     */
    function _getXmlRpcMsg($query = "") {
    	return new xmlrpcmsg($this->serviceName . '.doCluster',
			array(
				new xmlrpcval($query, "string"),
				$this->processingOptions,
				$this->requestOptions,
				new xmlrpcval($this->documents, "array")
			));
    }

    /**
     * Converts a map of string-string entries to XML RPC data structures.
     */
    function _toXMLRPC($map) {
    	$result = array();
    	foreach ($map as $key => $value) {
    		$result[$key] = new xmlrpcval($value, "string");
    	}
    	return new xmlrpcval($result, "struct");
    }
}

?>
