
Carrot2 Web Search Clustering Engine
------------------------------------

This archive contains a web-based application for clustering search results
based on the clustering algorithms available in the Carrot2 Framework
(http://www.carrot2.org).


Requirements
------------

The application requires:

    * Java Runtime Environment (JRE) version 1.4.2 or newer:
      http://java.sun.com/javase/downloads/index.jsp

    * Tomcat servlet container:
      http://tomcat.apache.org/download-55.cgi


Installation and running
------------------------

read Installation nodes first.

1. Copy the carrot2-demo-webapp.war file to Tomcat's webapps/ directory.

2. Start Tomcat.

3. Access: http://localhost:8080/carrot2-demo-webapp/


Installation notes
------------------

1. If you are using Tomcat 5.x, you will encounter query encoding issues.

You must enforce proper decoding of URI parameters in the connector. You
can do it by adding this attribute to the connector spec. in server.xml

URIEncoding="UTF-8"

An example connector should look similar to this one:

<Connector port="8080"
    maxThreads="25" minSpareThreads="5" maxSpareThreads="10"
    minProcessors="5" maxProcessors="25" enableLookups="false"
    redirectPort="8443" acceptCount="10" debug="0" connectionTimeout="20000" 
    URIEncoding="UTF-8" />

If you use direct Apache connectors (AJP), a similar attribute must be added.


2. For Google API input component, you need to define 'googleapi.keypool'
system property pointing at a folder where GoogleAPI keys can be found. Each
key file in that folder should have a ".key" extension and consist of a single
GoogleAPI key (one per file).

You can pass a JVM system property to Tomcat by defining CATALINA_OPTS:

CATALINA_OPTS=-Dgoogleapi.keypool=[absolute-path]


3. Carrot2 uses Log4j logging extensively. Log4j is by default included in the
Web application (WEB-INF/lib folder) and defines a default logging configuration
that places logging statements in Catalina's /log folder. If you plan to deploy
more than one application that uses Log4j, you may encounter class loading problems.
This is an advanced topic -- search mailing list archives for Log4j, Apache Commons Logging
and Tomcat issues.
