
An example of using Carrot2 components to clustering search 
results from Lucene.
===========================================================


Prerequisities
--------------

You must have an index created with Lucene and containing
documents with the following fields: url, title, summary.

The Lucene demo works with exactly these fields -- I just indexed
all of Lucene's source code and documentation using the following line:

mkdir index
java -Djava.ext.dirs=build org.apache.lucene.demo.IndexHTML -create -index index .

The index is now in 'index' folder.

Remember that the quality of snippets and titles heavily influences the
output of the clustering; in fact, the above example index of Lucene's API is
not too good because most queries will return nonsensical cluster labels
(see below). 

Building Carrot2-Lucene demo
----------------------------

Basically you should have all of Carrot2 source code checked out and
issue the building command:

ant -Dcopy.dependencies=true

All of the required libraries and Carrot2 components will end up
in 'tmp/dist/deps-carrot2-lucene-example-jar' folder.

You can also spare yourself some time and download precompiled binaries
I've put at:

http://www.cs.put.poznan.pl/dweiss/tmp/carrot2-lucene.zip

Now, once you have the compiled binaries, issue the following command
(all on one line of course):

java -Djava.ext.dirs=tmp\dist;tmp\dist\deps-carrot2-lucene-example-jar \
	com.dawidweiss.carrot.lucene.Demo index query

The first argument is the location of the Lucene's index created before. The second argument
is a query. In the output you should have clusters and max. three documents from every cluster:

Results for: query
Timings: index opened in: 0,181s, search: 0,13s, clustering: 0,721s
 :> Search Lucene Rc1 Dev API
    - F:/Repositories/cvs.apache.org/jakarta-lucene/build/docs/api/org/apache/lucene/search/class-use/Query.html
      Uses of Class org.apache.lucene.search.Query (Lucene 1.5-rc1-dev API)
    - F:/Repositories/cvs.apache.org/jakarta-lucene/build/docs/api/org/apache/lucene/search/package-summary.html
      org.apache.lucene.search (Lucene 1.5-rc1-dev API)
    - F:/Repositories/cvs.apache.org/jakarta-lucene/build/docs/api/org/apache/lucene/search/package-use.html
      Uses of Package org.apache.lucene.search (Lucene 1.5-rc1-dev API)
      (and 19 more)

 :> Jakarta Lucene
    - F:/Repositories/cvs.apache.org/jakarta-lucene/src/java/overview.html
      Jakarta Lucene API
    - F:/Repositories/cvs.apache.org/jakarta-lucene/docs/whoweare.html
      Jakarta Lucene - Who We Are - Jakarta Lucene
    - F:/Repositories/cvs.apache.org/jakarta-lucene/docs/index.html
      Jakarta Lucene - Overview - Jakarta Lucene
      (and 12 more)

If you look at the source code of Demo.java, there are plenty of things
apt for customization -- number of results from each cluster, number of displayed
clusters (I would cut it to some reasonable number, say 10 or 15 -- the further a
cluster is from the "top", the less it is likely to be important). Also keep
in mind that some of Carrot2 components produce hierarchical clusters. This demonstration
works with "flat" version of Lingo algorithm, so you don't need to worry about it.

Hope this gets you started with using Carrot2 and Lucene.
Please let me know about any successes or failures.

Dawid
