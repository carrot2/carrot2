
Carrot2 Project

See the license file for legal information (carrot2.LICENSE)


INFORMATION
===========

Carrot2 is a component-based data processing framework, created
primarily for clustering search results from search engines (but not only).

See more at:

http://www.carrot2.org
http://carrot.cs.put.poznan.pl


ACQUIRING A BINARY RELEASE
========================

Prebuilt demo applications are also available on SourceForge's file
download area:

http://sourceforge.net/projects/carrot2/


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

External tests suite (HTTPUnit tests) and documentation are available as a 
separate checkout.

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

Commercial consulting is offered by a spin-off company Carrot Search:

info@carrot-search.com
