
INFO
====

This is a set of binary Carrot2 components.

Copy these folders to a Tomcat installation. Run Tomcat. Access:

http://localhost:8080/carrot2-remote-controller


IMPORTANT
=========

1. 

If your Tomcat is not set up to work on port 8080, you will have to manually edit
Component descriptors found in:

webapps/carrot2-remote-controller.war/WEB-INF/components

2.

If you are using Tomcat 5, you will encounter query encoding issues.

You must enforce proper decoding of URI parameters in the connector. You
can do it by adding this attribute to the connector spec. in server.xml

URIEncoding="utf-8"

An example connector should look similar to this one:

<Connector port="8080"
    maxThreads="25" minSpareThreads="5" maxSpareThreads="10"
    minProcessors="5" maxProcessors="25" enableLookups="false"
    redirectPort="8443" acceptCount="10" debug="0" connectionTimeout="20000" 
    URIEncoding="UTF-8" />

3.

For Google API input component, you need to define 'googleapi.keypool' system property
pointing at a folder where GoogleAPI keys can be found. Each key file in that folder should
have a ".key" extension and consist of a single GoogleAPI key (one per file).

You can pass a JVM system property to Tomcat by defining CATALINA_OPTS:

CATALINA_OPTS=-Dgoogleapi.keypool=[absolute-path]

4.

To access the "admin" link in Carrot2 controller, add a user with role "carrot-admin"
to your tomcat users (/conf/tomcat-users.xml in Tomcat 5.x). For example:

<role rolename="carrot-admin"/>
<user username="c2admin" password="password:)" roles="carrot-admin"/>

--
Carrot2, http://carrot2.sf.net
