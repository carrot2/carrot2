Carrot2
=======

Carrot2 is a text clustering engine. It can automatically organize small to medium
collections of documents (like search results or abstracts) into thematic categories.

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

* Project website is at:
  [project.carrot2.org](https://project.carrot2.org)

* Developer documentation and examples are part of
  [binary releases](https://github.com/carrot2/carrot2/releases).
  Once downloaded and unpacked, start the DCS:

  ```shell script
  cd dcs
  ./dcs
  ```

  and open [the documentation](http://localhost:8080/doc/) or
  [JavaDoc API reference](http://localhost:8080/javadoc/).

Contact and more information
----------------------------

* Issues, pull-requests, communication:  
  https://github.com/carrot2/carrot2

License
-------

Carrot2 is licensed under the [BSD license](carrot2.LICENSE). 
