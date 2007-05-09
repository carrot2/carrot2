Carrot2 Document Clustering Server
----------------------------------

The Carrot2 Document Clustering Server (DCS) exposes Carrot2 clustering as an
HTTP/REST service. Essentially, you make an HTTP/POST request with an XML
containing the documents you want to have clustered and the DCS responds with
an XML containing the clusters created by Carrot2. For quick integration with
Ruby, A JSON output format is also available. Finally, for batch processing, a
simple command-line application is provided.

Requirements
------------

All applications in this package require Java Runtime Environment (JRE)
version 1.4.2 or newer. JRE can be downloaded free of charge from:

http://java.sun.com/javase/downloads/index.jsp


Building
--------

To build the DCS, run: ant dist. You will find the assembled applications
in the tmp/dist/web directory.


Applications
------------

This package provides the following applications:


1) batch

Clusters XML files and saves the results to an output folder. 
Format for the input data is the following:

<searchresult>
    <query>new york hotels</query>
    <document>
    	<title>NewYork.com</title>
        <snippet>guide to New York City with links to hotels, attractions, shopping, restaurants, and more. NewYork.com 
        also features local New York news, arts, and sports.</snippet>
    </document>

    <document>
        ...
    </document>
</searchresult>

For a description of the batch application switches, run the script
(batch.cmd, batch.sh) without parameters.


2) dcs

Starts the Document Clustering Server (DCS) on a given port. DCS
provides several access interfaces, REST (XML over HTTP) and XML-RPC
among them. When you start the Web dcs version, access it using
a Web browser for a full description of available options.