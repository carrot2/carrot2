<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Quick start - Document Clustering Server - Carrot2</title>
    <style type="text/css">
      body { font-family: Tahoma, sans-serif; font-size: 11px; }
      img { float: left; margin: 0.5ex 1ex 2ex 0; }
      p { clear: both; margin-bottom: 2ex; width: 40em }
      h1 { margin-top: 2ex; border-bottom: 1px solid #a0a0a0; width: 50%}
    </style>
  </head>

  <body>

<?php
  // This is an example of using Carrot2 PHP API. Please refer to the
  // documentation of specific methods for the available options.

  // Import Carrot2 integration code
  require_once('Carrot2.php');

  // Create a Carrot2 processor that will handle all clustering requests
  $processor = new Carrot2Processor();

  //
  //
  // The DCS can fetch results from an external source, such as a search
  // engine, and cluster these results. The code below shows this scenario.
  //
  //
  echo '<h1>Clustering documents from external source</h1>';
  $source = "etools";
  $query = "data mining";
  $results = 20;
  $algorithm = "url"; // cluster by url to show how to handle subclusters
  echo "<strong>Source:</strong> " . $source . '<br />';
  echo "<strong>Query:</strong> " . $query . '<br />';
  echo "<strong>Number of results:</strong> " . $results . '<br />';
  echo "<strong>Algorithm:</strong> " . $algorithm . '<br />';

  // Carrot2Job defines data required for clustering.
  $job = new Carrot2Job();
  $job->setSource($source);
  $job->setQuery($query);
  $job->setAlgorithm($algorithm);
  $job->setAttribute("results", $results);

  // Perform clustering
  try {
    $result = $processor->cluster($job);
  }
  catch (Carrot2Exception $e) {
    echo 'An error occurred during processing: ' . $e->getMessage();
    exit(10);
  }

  // Display results
  $documents = $result->getDocuments();
  $clusters = $result->getClusters();

  echo "<h2>Clusters</h2>";
  if (count($clusters) > 0) {
    echo "<ul>";
    foreach ($clusters as $cluster) {
      displayCluster($cluster);
    }
    echo "</ul>";

    // Display the first few documents from the first cluster
    // to show how documents are referenced from clusters.
    $cluster = $clusters[0];
    $count = 3;
    echo "<h2>First " . $count . " Documents in cluster " . $cluster->getLabel() . "</h2>";
    foreach ($cluster->getAllDocumentIds() as $documentId) {
      // documentId is an index to the documents array obtained from Carrot2Result
      displayDocument($documents[$documentId]);
      if (--$count === 0) {
        break;
      }
    }
  }

  // Display documents
  echo "<h2>All documents</h2>";
  foreach($documents as $document) {
    displayDocument($document);
  }

  //
  //
  // The DCS can also cluster documents provided directly by the caller.
  // This scenario is shown below.
  //
  //
  echo '<h1>Clustering directly provided documents</h1>';

  $job = new Carrot2Job();
  $algorithm = "lingo";

  addExampleDocuments($job);
  $job->setAlgorithm($algorithm);
  $job->setQuery("data mining"); // set the query as a hint for the clustering algorithm (optional)
  $job->setAttributes(array (
        'TermDocumentMatrixBuilder.termWeighting' => 'org.carrot2.text.vsm.LinearTfIdfTermWeighting'
  ));

  $result = $processor->cluster($job);
  $clusters = $result->getClusters();

  echo "<h2>Clusters</h2>";
  if (count($clusters) > 0) {
    echo "<ul>";
    foreach ($clusters as $cluster) {
      displayCluster($cluster);
    }
    echo "</ul>";
  }

  echo "<h2>Other attributes</h2>";
  $attributes = $result->getAttributes();
  foreach ($attributes as $key => $value) {
    echo "<strong>" . $key . ":</strong> " . $value . "<br />";
  }

  //
  // Examples end here, below are utility functions.
  //

  /**
   * A utility function to display clusters.
   */
  function displayCluster(Carrot2Cluster $cluster)
  {
    echo '<li>' . $cluster->getLabel() . ' (' . $cluster->size() . ')';
    if (count($cluster->getSubclusters()) > 0) {
       echo '<ul>';
       foreach ($cluster->getSubclusters() as $subcluster) {
         displayCluster($subcluster);
       }
       echo '</ul>';
    }
    echo '</li>';
  }

  /**
   * A utility function to display documents.
   */
  function displayDocument(Carrot2Document $document)
  {
    echo '<p>';
    // Here we'll handle one specific optional field of documents
    // Refer to Carrot2 documentation for a list of other fields
    $thumbnailUrl = $document->getField('thumbnail-url');
    echo ($document->getId() + 1) . '. ';
    echo '<strong>' . $document->getTitle() . '</strong><br />';
    if ($thumbnailUrl) {
      echo '<img src="' . htmlentities($thumbnailUrl) . '" alt="' . $document->getTitle() . '" />';
    }
    echo $document->getContent();
    echo '<br /><a href="' . htmlentities($document->getUrl()) . '">' . htmlentities($document->getUrl()) . '</a>';
    echo '</p>';
  }

  /**
   * Returns some example hard coded data for clustering
   */
  function addExampleDocuments(Carrot2Job $job)
  {
    $docs = array(
      array("Data Mining - Wikipedia", "http://en.wikipedia.org/wiki/Data_mining"),
      array("KD Nuggets", "http://www.kdnuggets.com/"),
      array("The Data Mine", "http://www.the-data-mine.com/"),
      array("DMG", "http://www.dmg.org/"),
      array("Two Crows: Data mining glossary", "http://www.twocrows.com/glossary.htm"),
      array("Jeff Ullman's Data Mining Lecture Notes", "http://www-db.stanford.edu/~ullman/mining/mining.html"),
      array("Thearling.com", "http://www.thearling.com/"),
      array("Data Mining", "http://www.eco.utexas.edu/~norman/BUS.FOR/course.mat/Alex"),
      array("CCSU - Data Mining Book", "http://www.ccsu.edu/datamining/resources.html"),
      array("Data Mining: Practical Machine Learning Tools and Techniques", "http://www.cs.waikato.ac.nz/~ml/weka/book.html"),
      array("Data Mining - Monografias.com", "http://www.monografias.com/trabajos/datamining/datamining.shtml"),
      array("Amazon.com: Data Mining: Books: Pieter Adriaans,Dolf Zantinge", "http://www.amazon.com/exec/obidos/tg/detail/-/0201403803?v=glance"),
      array("DMReview", "http://www.dmreview.com/"),
      array("Data Mining @ CCSU", "http://www.ccsu.edu/datamining"),
      array("What is Data Mining", "http://www.megaputer.com/dm/dm101.php3"),
      array("Electronic Statistics Book: Data Mining Techniques", "http://www.statsoft.com/textbook/stdatmin.html"),
      array("data mining - a definition from Whatis.com - see also: data miner, data analysis", "http://searchcrm.techtarget.com/sDefinition/0,,sid11_gci211901,00.html"),
      array("St@tServ - About Data Mining", "http://www.statserv.com/datamining.html"),
      array("DATA MINING 2005", "http://www.wessex.ac.uk/conferences/2005/data05"),
      array("Investor Home - Data Mining Book", "http://www.investorhome.com/mining.htm"),
      array("SAS | Data Mining and Text Data Mining", "http://www.sas.com/technologies/data_mining"),
      array("Data Mining Student Notes, QUB", "http://www.pcc.qub.ac.uk/tec/courses/datamining/stu_notes/dm_book_1.html"),
      array("Data Mining", "http://datamining.typepad.com/data_mining"),
      array("Two Crows Corporation", "http://www.twocrows.com/"),
      array("Statistical Data Mining Tutorials", "http://www.autonlab.org/tutorials"),
      array("Data Mining: An Introduction", "http://databases.about.com/library/weekly/aa100700a.htm"),
      array("Data Mining Project", "http://research.microsoft.com/dmx/datamining"),
      array("An Introduction to Data Mining", "http://www.thearling.com/text/dmwhite/dmwhite.htm"),
      array("Untangling Text Data Mining", "http://www.sims.berkeley.edu/~hearst/papers/acl99/acl99-tdm.html"),
      array("Data Mining Technologies", "http://www.data-mine.com/"),
      array("SQL Server Data Mining Tutorials", "http://www.sqlserverdatamining.com/"),
      array("Data Warehousing Information Center", "http://www.dwinfocenter.org/"),
      array("ITworld.com - Data mining Tutorials", "http://www.itworld.com/App/110/050805datamining"),
      array("IBM Research | Almaden Research Center | Computer Science", "http://www.almaden.ibm.com/cs/quest"),
      array("Data Mining and Discovery", "http://www.aaai.org/AITopics/html/mining.html"),
      array("Data Mining: An Overview", "http://www.fas.org/irp/crs/RL31798.pdf"),
      array("Data Mining", "http://www.gr-fx.com/graf-fx.htm"),
      array("Data Mining Benchmarking Association (DMBA)", "http://www.dmbenchmarking.com/"),
      array("Data Mining", "http://www.computerworld.com/databasetopics/businessintelligence/datamining"),
      array("National Center for Data Mining (NCDM) - University of Illinois at Chicago", "http://www.ncdm.uic.edu/")
    );

    foreach ($docs as $doc) {
      $job->addDocument($doc[0], '', $doc[1]);
    }
  }
?>
  </body>
</html>
