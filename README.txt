
This version is put together exclusively as a backport for Lucene/Solr 3.x (they require 1.5-compatible binaries).
Certain things have been disabled (Yahoo Boss is not supported).

Compilation:

- use 1.6 or 1.7 compiler (annotation processing!)
ant clean test
- use 1.5 to run tests again after the code has been compiled.
ant test