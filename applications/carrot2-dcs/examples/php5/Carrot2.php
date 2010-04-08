<?php

if (!function_exists('curl_init')) {
  throw new Exception("Curl is required for this class.");
}

//
// This code is based on the Carrot2 integration code from:
// http://www.roryoung.co.uk/2008/03/15/playing-with-carrot2-clustering-in-php/
//

/**
 * Contains data for Carrot2 processing. Two kinds of jobs can be submitted:
 *
 * - Clustering directly provided documents. For this job, add the documents
 *   to be clustered using the addDocument() method. All other data items are optional.
 *
 * - Clustering documents fetched by Carrot2 from some data source. For this job,
 *   set the query and document source. All other data items are optional.
 */
class Carrot2Job
{
  private $documents = array();
  private $query;
  private $source;
  private $algorithm;
  private $attributes = array();

  /**
   * Adds a document for clustering. You can either provide documents using this 
   * method or the document source to search using the setSource() method, but not
   * both. 
   *
   * @param $title title of the document to cluster, required
   * @param $content content of the document to cluster, optional
   * @param $url url of the document to cluster, optional
   */
  public function addDocument($title, $content = '', $url = '')
  {
    $this->documents[] = new Carrot2Document($title, $content, $url);
  }

  public function getDocuments()
  {
    return $this->documents;
  }

  /**
   * Sets the source from which Carrot should fetch documents for clustering. You can 
   * either set the document source using this method or directly add documents using
   * addDocument but not both.
   *
   * @param $source identifier of the document source to query. Check the "Parameters"
   * tab of the DCS welcome screen for the list of supported source identifiers.
   */
  public function setSource($source)
  {
    $this->source = $source;
  }

  public function getSource()
  {
    return $this->source;
  }

  /**
   * Sets the algorithm Carrot should use to cluster documents. Setting the algorithm
   * is optional, if not set, the default algorithm will be used.
   * 
   * @param $algorithm identified of the clustering algorithm to use. Check the "Parameters"
   * tab of the DCS welcome screen for the list of supported algorithm identifiers.
   */
  public function setAlgorithm($algorithm)
  {
    $this->algorithm = $algorithm;
  }

  public function getAlgorithm()
  {
    return $this->algorithm;
  }

  /**
   * Sets the query to execute or query hint. If you want to use Carrot to query some data
   * source specified by setSource(), you must set the query using this method. If you
   * provide documents directly using the addDocument() method and you know which query
   * generated these documents, you can optionally set this query using this method, which
   * will improve the clustering quality.
   *
   * @param $query query to execute when document source is set or query hint when
   * providing documents directly
   */
  public function setQuery($query)
  {
    $this->query = $query;
  }

  public function getQuery()
  {
    return $this->query;
  }

  /**
   * Sets additional tuning attributes for Carrot2 document sources or algorithms. Please
   * refer to Carrot2 manual for the list of supported attributes for each source and
   * algorithm.
   *
   * @param $attributes an associative array with attribute keys as keys, and attribute
   * values as values
   */
  public function setAttributes(array $attributes)
  {
    if (is_array($attributes)) {
      $this->attributes = $attributes;
    }
  }

  /**
   * Sets one additional tuning attribute for Carrot2 document sources or algorithms.
   * Please refer to Carrot2 manual for the list of supported attributes for each source
   * and algorithm.
   *
   * @param $key attribute key
   * @param $value attribute value
   */ 
  public function setAttribute($key, $value)
  {
    $this->attributes[$key] = $value;
  }

  public function getAttributes()
  {
    return $this->attributes;
  }
}

/**
 * Represents a Carrot2 document. Instances of this class are used both to provide documents
 * for clustering and access the documents retrieved from an external source, if requested.
 */
class Carrot2Document
{
  private $id;
  private $title;
  private $content;
  private $url;
  private $otherFields;

  public function __construct($title, $content = '', $url = '', array $otherFields = array(), $id = null)
  {
    $this->id = $id;
    $this->title = $title;
    $this->content = $content;
    $this->url = $url;
    $this->otherFields = $otherFields;
  }

  /**
   * Returns a unique document identifier.
   */
  public function getId()
  {
    return $this->id;
  }

  /**
   * Returns document's title.
   */
  public function getTitle()
  {
    return $this->title;
  }

  /**
   * Returns document's content.
   */
  public function getContent()
  {
    return $this->content;
  }

  /**
   * Returns document's url.
   */
  public function getUrl()
  {
    return $this->url;
  }

  /**
   * Returns value of a document fields, other than title, content and url.
   *
   * @param $fieldName name of the field
   * @return value of the field or null
   */ 
  public function getField($fieldName)
  {
     return isset($this->otherFields[$fieldName]) ? $this->otherFields[$fieldName] : null;
  }

  /**
   * Returns an array of other document's fields. Keys in the array correspond to field names,
   * values to field values. Please refer to Carrot2 documentation for the fields supported by
   * specific document sources.
   */
  public function getOtherFields()
  {
    return $this->otherFields;
  }
}

/**
 * Represents a Carrot2 cluster.
 */
class Carrot2Cluster
{
  private $label;
  private $score;
  private $documentIds = array();
  private $allDocumentIds;
  private $subclusters  = array();

  public function __construct($label, $score, $documentIds, $subclusters)
  {
    $this->label = $label;
    $this->score = $score;
    $this->documentIds = $documentIds;
    $this->subclusters = $subclusters;
  }

  /**
   * Returns this cluster's label.
   */
  public function getLabel()
  {
    return $this->label;
  }

  /**
   * Returns the actual size of this cluster, which is the number of unique
   * documents in the cluster and its subclusters.
   */
  public function size()
  {
    if (!$this->allDocumentIds) {
      $this->allDocumentIds = array();
      $this->addDocumentIds($this->allDocumentIds);
    }
    return count($this->allDocumentIds);
  }

  /**
   * Returns subclusters of this cluster.
   */
  public function getSubclusters()
  {
    return $this->subclusters;
  }

  /**
   * Returns identifiers of documents assigned directly to this cluster (not
   * the subclusters). Use these identifiers to retrieve documents from the array
   * returned by Carrot2Result::getDocuments().
   */
  public function getDocumentIds()
  {
    return $this->documentIds;
  }

  /**
   * Returns identifiers of documents assigned to this cluster and its
   * subclusters. Use these identifiers to retrieve documents from the array
   * returned by Carrot2Result::getDocuments().
   */
  public function getAllDocumentIds()
  {
    return $this->allDocumentIds;
  }

  /**
   * Recursive function for collecting document ids from subclusters.
   */ 
  private function addDocumentIds(&$ids) 
  {
     foreach ($this->documentIds as $id) {
       $ids[$id] = $id;
     }

     foreach ($this->subclusters as $subcluster) {
       $subcluster->addDocumentIds($ids);
     }

     return $ids;
  }
}

/**
 * Represents the results of Carrot2 processing, contains document fetched from the 
 * external document source (if requested) and clusters.
 */
class Carrot2Result
{
   private $documents;
   private $clusters;
   private $attributes;

   public function __construct($documents = array(), $clusters = array(), $attributes = array())
   {
      $this->documents = $documents;
      $this->clusters = $clusters;
      $this->attributes = $attributes;
   }

   /**
    * Returns the documents that have been clustered. Keys in this array correspond do
    * document identifiers obtained, e.g. from Carrot2Cluster::getDocumentIds().
    */
   public function getDocuments()
   {
      return $this->documents;
   }

   /**
    * Returns the created clusters.
    */
   public function getClusters()
   {
      return $this->clusters;
   }

   /**
    * Returns an array of additional attributes set by the clustering engine.
    * Keys in the returned array correspond to attribute keys, values to attribute values.
    * For a list of supported attribute keys, please refer to Carrot2 Manual.
    */
   public function getAttributes()
   {
      return $this->attributes;
   }
}

/**
 * Performs processing using Carrot2 Document Clustering Server. 
 */
class Carrot2Processor
{
  private $baseurl;

  /**
   * Creates a Carrot2 processor.
   *
   * @param $baseurl Carrot2 DCS service url, defaults to 'http://localhost:8080/dcs/rest'
   */
  public function __construct($baseurl = 'http://localhost:8080/dcs/rest')
  {
    $this->baseurl = $baseurl;
  }

  /**
   * Processes the provided Carrot2 job.
   *
   * @return returns Carrot2Result with processing results
   * @throws Carrot2Exception in case of unrecoverable errors, e.g. no connection to DCS
   */
  public function cluster(Carrot2Job $job)
  {
    $curl   = curl_init($this->baseurl);

    // Prepare request parameters
    $fields = array_merge($job->getAttributes(), array(
      'dcs.output.format' => 'XML'));

    $documents = $job->getDocuments();
    if (count($documents) > 0) {
       $fields['dcs.c2stream'] = $this->generateXml($documents);
    }
    self::addIfNotNull($fields, 'dcs.source', $job->getSource());
    self::addIfNotNull($fields, 'dcs.algorithm', $job->getAlgorithm());
    self::addIfNotNull($fields, 'query', $job->getQuery());

    // Make POST request
    curl_setopt_array($curl,
      array(
        CURLOPT_POST           => true,
        CURLOPT_HTTPHEADER     => array('Content-Type: multipart/formdata'),
        CURLOPT_HEADER         => false,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS     => $fields
      )
    );
    $response = curl_exec($curl);

    $error = curl_errno($curl);
    if ($error !== 0) {
       throw new Carrot2Exception(curl_error($curl));
    }
    $httpStatus = curl_getinfo($curl, CURLINFO_HTTP_CODE);
    if ($httpStatus >= 400) {
       throw new Carrot2Exception('HTTP error occurred, error code: ' . $httpStatus);
    }

    return $this->extractResponse($response);
  }

  /**
   * Generates XML with the directly provided documents.
   */
  private function generateXml($documents)
  {
    $dom     = new DOMDocument('1.0', 'UTF-8');
    $resultsElement = $dom->createElement('searchresult');
    $dom->appendChild($resultsElement);
    foreach ($documents as $document) {
      $documentElement = $dom->createElement('document');
      $this->appendTextField($dom, $documentElement, 'title', $document->getTitle());
      $this->appendTextField($dom, $documentElement, 'snippet', $document->getContent());
      $this->appendTextField($dom, $documentElement, 'url', $document->getUrl());
      $resultsElement->appendChild($documentElement);
    }
    return $dom->saveXML();
  }

  private function appendTextField($dom, $elem, $name, $value)
  {
    $text = $dom->createElement($name);
    $text->appendChild($dom->createTextNode((string)$value));
    $elem->appendChild($text);
  }

  /**
   * Extracts Carrot2Results from the XML response.
   */
  private function extractResponse($xml)
  {
    if (!($xml instanceof SimpleXMLElement)) {
      $xml = new SimpleXMLElement($xml);
    }

    return new Carrot2Result($this->extractDocuments($xml), 
                             $this->extractClusters($xml->xpath('/searchresult/group')),
                             $this->extractAttributes($xml->xpath('/searchresult/attribute')));
  }

  private function extractDocuments($xml)
  {
    $documents = array();
    foreach ($xml->xpath('/searchresult/document') as $documentElement) {
      $document = new Carrot2Document(
        (string)$documentElement->title,
        (string)$documentElement->snippet,
        (string)$documentElement->url,
        $this->extractAttributes($documentElement->xpath('field')),
        (string)$documentElement['id']
      );
      $documents[] = $document;
    }
    return $documents;
  }

  private function extractClusters($groupElements)
  {
    $clusters = array();

    foreach ($groupElements as $group) {
      $documentIds = array();
      foreach ($group->xpath('document') as $document) {
        $documentIds []= (string)$document['refid'];
      }

      $subclusters = $this->extractClusters($group->xpath('group'));

      $cluster = new Carrot2Cluster(
        (string)$group->title->phrase,
        (string)$group['score'],
        $documentIds,
        $subclusters
      );
      $clusters[] = $cluster;
    }

    return $clusters;
  }

  private function extractAttributes($attributeElements)
  {
     $attributes = array();
     foreach($attributeElements as $attribute) {
        $key = $attribute['key'];
        $valueElement = $attribute->xpath('value');
        if (count($valueElement) > 0) {
          $value = $valueElement[0]['value'];
          if ($value) {
             $attributes[(string)$key] = (string)$value;
          }
        }
     }
     return $attributes;
  }

  private static function addIfNotNull(&$array, $key, $value)
  {
    if ($value) {
      $array[$key] = $value;
    }
  }
}

/**
 * Exception thrown when an unrecoverable error occurs in Carrot2 DCS.
 */
class Carrot2Exception extends Exception {
}
