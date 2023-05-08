![Github Build Status](https://github.com/carrot2/carrot2/workflows/Gradle%20Check/badge.svg)

Carrot2
=======

Carrot2 is a programming library for clustering text. It can automatically 
discover groups of related documents and label them with short key terms 
or phrases.

Carrot2 can turn, for example, search result titles and snippets into 
groups like these:

![Search result titles and snippets and corresponding cluster labels (right).](doc/src/content/images/carrot2-intro-example-light.png "")


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


Building from Sources
---------------------

If you need to build the distribution from sources, run:
```
./gradlew -p distribution assemble
```
The distribution is placed under distribution/build/dist/ and a compressed
version is available at distribution/build/distZip/


Documentation
-------------

* The documentation for the latest release is always at 
  [https://carrot2.github.io/release/latest](https://carrot2.github.io/release/latest).

* Developer documentation and examples are part of
  [binary releases](https://github.com/carrot2/carrot2/releases).
  Once downloaded and unpacked, start the DCS:

  ```shell script
  cd dcs
  ./dcs
  ```

  and open the documentation at [localhost:8080/doc/](http://localhost:8080/doc/) or
  JavaDoc API reference [localhost:8080/javadoc/](http://localhost:8080/javadoc/).

* Additional information is published on the project's 
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
