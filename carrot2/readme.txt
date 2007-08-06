
Carrot2 Project

See the license file for legal information (carrot2.LICENSE)


INFORMATION
===========

Carrot2 is a component-based data processing framework, created
primarily for clustering search results from search engines (but not only).

See more at:

http://www.carrot2.org


ACQUIRING A BINARY RELEASE
========================

Prebuilt demo applications are available from our build server:

http://project.carrot2.org/download.html


BUILDING FROM SOURCES
=====================

In order to build Carrot2's components, you must have ANT in your path. We try to
ensure the build file works flawlessly with the newest ANT available.

All components can be compiled using the following ANT command:

ant -q build

Components are placed in their respective directories, under 'tmp/dist' folder.
Their required dependencies and libraries are listed in an *.info file found
together with the resulting WAR or JAR file.


TEST SUITE AND DOCUMENTATION
============================

Class and method tests for all modules that support 'test' target can
be executed by invoking the following ANT targets:

ant -q build
ant -q test


CONTACT
=======

If you have questions or problems, subscribe to:

carrot2-developers@lists.sourceforge.net


This e-mailing list will keep you up to date with major changes:

carrot2-news@lists.sourceforge.net


Commercial consulting is offered by a spin-off company Carrot Search:

info@carrot-search.com
