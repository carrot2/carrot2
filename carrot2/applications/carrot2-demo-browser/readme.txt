Carrot2 Tuning Application
--------------------------

This archive contains a GUI application for tuning parameters of clustering
algorithms available in the Carrot2 Framework (http://project.carrot2.org).

Requirements
------------

All applications in this package require Java Runtime Environment (JRE)
version 1.4.2 or newer. JRE can be downloaded free of charge from:

http://java.sun.com/javase/downloads/index.jsp


Running the application
-----------------------

To run the application, execute the demo.bat (MS Windows) or demo.sh
script.


Running clustering
------------------

Select a clustering algorithm from the "Process" combo box (e.g. "Lingo
Classic clusterer", type a query (e.g.  "data mining") to the "Query" field
and hit the "Search" button.


Forcing Java HTML browser
-------------------------

By default the demo uses JDIC (native) browser component under Windows.
To use a pure Java browser, set the following JVM property:

use.java.browser=true

A convenient way to do it is to define JAVA_OPTS property:

JAVA_OPTS=-Duse.java.browser=true


Building from sources
---------------------

Use the following ant command:

ant -q build
