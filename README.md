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

Carrot2 is a software component and typically integrates with other software
as a library dependency (see the API documentation available with each release).

[Binary releases are published on GitHub](https://github.com/carrot2/carrot2/releases) and they 
ship with a HTTP/JSON REST API service called the DCS 
(document clustering server) for integration with other languages.

Integration with document retrieval services is possible
via [Apache Solr plugin](https://lucene.apache.org/solr/guide/result-clustering.html) 
and [Elasticsearch plugin](https://github.com/carrot2/elasticsearch-carrot2).

Releases
--------

Quick access to releases and their documentation.

| Version | Artifact                         | Documentation |
| ---     | ---                              | ---           |
| 4.0.0   | [Binary bundle](releases/download/release-4.0.0/carrot2-4.0.0.zip) | [Docs](https://carrot2.github.io/release/4.0.0/doc/) |   


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


Source code
-----------

Source code is at [GitHub](https://github.com/carrot2/carrot2). 


Contact and more information
----------------------------

* Issues, pull-requests, communication:  
  https://github.com/carrot2/carrot2

License
-------

Carrot2 is licensed under the [BSD license](carrot2.LICENSE). 
