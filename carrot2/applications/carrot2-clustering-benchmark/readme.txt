Carrot2 Clustering Benchmar Application
---------------------------------------

This component provides a simple command-line application for benchmarking
clustering algorithms. For the time being all queries and process definitions 
are hard-coded in the benchmarking application.

The tests require an index of some part of the Open Directory Project data,
which can be downloaded from the following address:

http://ophelia.cs.put.poznan.pl/~sosinski/carrot/odp-cshpark.zip

Unzip the contents of the above archive into some local directory, e.g. 
c:\odp-index and run the tests:

ant build
ant -Dodp.index.dir=c:\odp-index html-report

To view the benchmarking reports point your browser to the 
'reports/html/report.html' file.