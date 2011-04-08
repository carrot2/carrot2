Carrot2 Solr @solr.version@ compatibility package
----------------------------------------

To use version @carrot2.version@ of Carrot2 with Solr @solr.version@, you need to apply
the compatibility package provided in this archive.


Installation:

1. Remove the following files from your Solr @solr.version@ installation:

   a. contrib/clustering/lib/* (all files)
   b. dist/apache-solr-clustering-*.jar (Solr clustering plugin JAR)

2. Copy the contents of this archive over the Solr home directory.

3. Start Solr, clustering should be performed by the updated Carrot2.




More information
----------------

For more information, please refer to Carrot2 Clustering Engine Manual.



