Carrot2 Document Clustering Server
----------------------------------

Requirements
------------

All applications in this package require Java Runtime Environment (JRE)
version 1.4.2 or newer. JRE can be downloaded free of charge from:

http://java.sun.com/javase/downloads/index.jsp


Applications
------------

This package provides the following applications:


1) l3cli

Clusters XML files from the "queries" folder and saves the results to
the "output" folder. Format for the input data is the following:

<searchresult>
    <query>new york hotels</query>
    <document>
    	<title>NewYork.com</title>
        <snippet>guide to New York City with links to hotels, attractions, shopping, restaurants, and more. NewYork.com also features local New York news, arts, and sports.</snippet>
    </document>

    <document>
        ...
    </document>
</searchresult>

2) l3dcs

Starts the Document Clustering Server (DCS) on a given port. DCS
provides the following access interfaces:

    HTTP POST 

    Clusters XML input data (in the same format as for the c2cli application)
    provided in a HTTP POST request. Assuming that the DCS is running on
    port 8080 at localhost, the HTTP POST service is available at
    http://localhost:8080/rest. Please access this address with a browser for
    an example HTML form showing how to post queries.


    XML-RPC

    Provides an XML-RPC interface to the Lingo3G clustering engine. Assuming
    that the DCS is running on port 8080 at localhost, the XML-RPC service is
    available at http://localhost:8080/xmlrpc.

    The XML-RPC service can be used together with example PHP code located in
    the examples/php directory of the distribution archive. Before using the
    PHP example, please configure DCS address and port number in the
    carrot2-config.inc file. Please see the test.php file for an example on
    how to call clustering.
