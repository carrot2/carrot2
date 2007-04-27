Carrot2 Document Clustering Server
----------------------------------

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
        <snippet>guide to New York City with links to hotels, attractions, shopping, restaurants, and more. NewYork.com also features local New York news, arts, and sports.</snippet>
    </document>

    <document>
        ...
    </document>
</searchresult>

For a description of the batch application switches, run the script
(batch.cmd, batch.sh) without parameters.


2) dcs

Starts the Document Clustering Server (DCS) on a given port. DCS
provides the following access interfaces:

    HTTP POST 

    Clusters XML input data (in the same format as for the c2cli application)
    provided in a HTTP POST request. Assuming that the DCS is running on
    port 8080 at localhost, the HTTP POST service is available at
    http://localhost:8080/rest. Please access this address with a browser for
    an example HTML form showing how to post queries.


