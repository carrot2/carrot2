
Carrot2 Project

See the license file for legal information (carrot2.LICENSE)


INFORMATION
===========

Carrot2 is a component-based data processing framework, created
especially for the reason of clustering search results from
search engines (but not only).

See more at:
http://www.cs.put.poznan.pl/dweiss/carrot
http://carrot.cs.put.poznan.pl


ACQUIRING BINARY RELEASE
========================

It is advised that you grab a binary release of the Carrot2 components from the 
nightly build drop-off zone at:

http://carrot.cs.put.poznan.pl/static/download/nightly/


BUILDING FROM SOURCES
=====================

In order to build Carrot2's components, you must have ANT in your path. We use 
ANT 1.6.2, previous versions are not supported but may work. 

Carrot2 components can work in two architecture designs: as remote components
using HTTP and as local Java components.

0) Build 'bootstrap' tasks

Carrot2 uses some ANT tasks that must be built prior to other
tasks.

ant -q bootstrap

a) Building only remote components

ant -q remote

This command should build all the components for you and place them in a 
directory pointed to by an ANT property 'distribution.dir.remote'.

Any libraries, or local components, required by remote components are copied to 
a directory  pointed to by an ANT property 'distribution.dir.remote.libs'. 
Search engine adapters for Egothor and Nutch are also copied there.

Default values of these properties point at: 'tmp/remote' and 'tmp/remote-libs'.

b) Building all components and applications

All components can be compiled using the following ANT command:

ant -q build

Components are placed in their respective directories, under 'tmp/dist' folder.
Their required components and libraries are listed in an *.info file found
together with the resulting WAR or JAR file.

c) Assembling a 'distribution'.

A distribution contains a binary distribution of remote components that is
'tomcat-ready', that is simply copy the contents of the ZIP file over to a 
Tomcat installation and everything should be fine.

ant -q dist

d) Assembling a 'custom' component set.

To assemble a 'custom' component set, a build file and a dependency file
are needed. Follow example applications (e.g. applications/carrot2-lucene-example).
A build that copies all required dependencies looks like this (build.xml in the
application's folder):

ant -Dcopy.dependencies=true


THE REMOTE CONTROLLER COMPONENT
===============================

The remote controller component for Carrot2 uses the rest of the components to 
process user queries. The controller looks for services offered by components 
using 'descriptors'. Sample component descriptors (and process descriptors that 
bind them together) can be found in 'remote-descriptors' folder. 

YOU SHOULD ADJUST THE DEFAULT HOST AND PORT IN THESE DESCRIPTORS TO MATCH YOUR 
DEPLOYMENT CONFIGURATION.

You can also override these properties at build-time using the following 
variables:

deployment.port   -- the port to deploy components on
deployment.host   -- the host to deploy components on

For example:

ant -q clean
ant -Ddeployment.port=80 -Ddeployment.host=myhost.com -q remote

Builds all remote components and a remote controller that binds process descriptors
to 'myhost.com:80'.


TEST SUITE AND DOCUMENTATION
============================

Class and method tests for all modules that support 'test' target can
be executed by invoking the following ANT targets:

ant -q build
ant -q test

External tests suite (HTTPUnit tests) and documentation are available as a 
separate CVS checkout.

See the results of the most recent tests on-line at:
http://carrot.cs.put.poznan.pl


CONTACT
=======

If you have questions or problems, subscribe to:

carrot2-developers@lists.sourceforge.net
carrot2-cvscommits@lists.sourceforge.net

This e-mailing list will keep you up to date with changes:

carrot2-news@lists.sourceforge.net

Other inquiries can be directed to the project's coordinator:

Dawid Weiss <dawid.weiss@cs.put.poznan.pl>

