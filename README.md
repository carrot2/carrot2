Carrot2
=======

Carrot2 is a programming library for clustering text. It can automatically 
discover groups of related documents and label them with short key terms 
or phrases.

Carrot2 can turn, for example, search result titles and snippets into 
groups like these:

![Search result titles and snippets and corresponding cluster labels (right).](doc/src/content/images/carrot2-intro-example-light.png "")


[![Build Status](https://travis-ci.org/carrot2/carrot2.svg?branch=master)](https://travis-ci.org/carrot2/carrot2)


Installation
------------

Carrot2 is a software component (see [Java API examples](core-examples)).

Binary releases ship with a HTTP/JSON REST API service called the DCS 
(document clustering server) for integration with other languages.

Integration with document retrieval services is possible
via [Apache Solr plugin](https://lucene.apache.org/solr/guide/result-clustering.html) 
and [Elasticsearch plugin](https://github.com/carrot2/elasticsearch-carrot2).

Source code
-----------

Source code is at [GitHub](https://github.com/carrot2/carrot2). 

Documentation
-------------

* Developer documentation and examples are part of
  [binary releases](https://github.com/carrot2/carrot2/releases).
  Once downloaded and unpacked, start the DCS:

  ```shell script
  cd dcs
  ./dcs
  ```

  and open the documentation at [localhost:8080/doc/](http://localhost:8080/doc/) or
  JavaDoc API reference [localhost:8080/javadoc/](http://localhost:8080/javadoc/).

* Some information is published on this project's 
  [wiki pages](https://github.com/carrot2/carrot2/wiki).


Contact and more information
----------------------------

* Issues, pull-requests, communication:  
  https://github.com/carrot2/carrot2

License
-------

Carrot2 is licensed under the [BSD license](carrot2.LICENSE). 
